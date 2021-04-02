package com.uos.project_new_windy.navigationlobby

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddShopContentBinding
import com.uos.project_new_windy.model.contentdto.ContentShopDTO
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddSellContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddShopContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.SharedData
import com.uos.project_new_windy.util.TimeUtil

class AddShopContentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding : ActivityAddShopContentBinding

    //이미지 갯수 체크를 위한 변수
    var count: Int = 0;
    var imageDownLoadUriList: ArrayList<String> = arrayListOf()
    var pickCategoryData: String? = null
    var progressDialog: ProgressDialogLoading? = null
    var userNickName: String? = null
    var address : String ? = null

    //새 게시글 작성인지 or 수정 인지 확인하기 위한 변수
    var updateCheck : Boolean ? = null
    var postUid : String ? = null

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var imageUriList: ArrayList<Uri> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_shop_content)


        //로딩 초기화
        progressDialog = ProgressDialogLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        //파이어베이스 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // true = 수정로직 / false = 새게시글 작성 로직
        updateCheck = intent.getBooleanExtra("updateCheck",false)
        postUid = intent.getStringExtra("postUid")

        //수정하기면 해당 게시글의 정보 불러오기
        if(updateCheck == true){
            updateDataLoad()
        }

        //유저 닉네임,유저 주소 가져오기
        firestore!!.collection("userInfo").document("userData")
            .collection(auth!!.currentUser?.uid!!).document("accountInfo")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null) {
                    userNickName = documentSnapshot.get("userName")?.toString()
                    address = documentSnapshot.get("address")?.toString()
                }

            }

        val items = resources.getStringArray(R.array.content_category)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            items
        )
        //스피너에 어댑터 추가
        binding.activityAddShopContentSpinnerCategory.adapter = spinnerAdapter

        binding.activityAddShopContentSpinnerCategory.onItemSelectedListener = this

        binding.activityAddShopContentImagebuttonBack.setOnClickListener {
            finish()
        }
        //사진이 추가될 리사이클러뷰 초기화
        binding.activityAddShopContentRecyclerPhoto.adapter =
            AddShopContentActivityRecyclerViewAdapter(this, imageUriList)
        binding.activityAddShopContentRecyclerPhoto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //사진추가
        binding.activityAddShopContentImagebuttonAddphoto.setOnClickListener {
            addPhoto()
        }

        //게시글 업로드
        binding.activityAddShopContentButtonUpload.setOnClickListener {

            if (imageUriList.size <= 0) {
                Toast.makeText(this, "사진을 한장 이상 포함해주세요.", Toast.LENGTH_LONG).show()
            } else {
                //게시글 작성
                if (updateCheck == false) {
                    if(binding.activityAddShopContentEdittextCost.text.isNotEmpty()) {
                        contentUpload()
                    }else{
                        Toast.makeText(this,"가격을 입력해주세요.", Toast.LENGTH_LONG).show()
                    }
                }
                //게시글 수정
                else{
                    updateContent()
                }
            }
        }


        //임시 저장된 게시글 확인
        checkSaveData()

    }

    fun updateDataLoad(){
        firestore?.collection("contents")?.document("shop")?.collection("data")?.document(postUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot != null){
                    if (documentSnapshot.exists()){

                        imageUriList.clear()

                        var contentData = documentSnapshot.toObject(ContentShopDTO::class.java)
                        binding.activityAddShopContentEdittextCost.setText(contentData?.costInt.toString())
                        binding.activityAddShopContentEdittextProductExplain.setText(contentData?.productExplain)
                        binding.activityAddShopContentEdittextExplain.setText(contentData?.explain)

                        contentData?.imageDownLoadUrlList?.forEachIndexed { index, s ->

                            imageUriList.add(Uri.parse(s))
                            println("이미지다운로드유알엘리스트 " + index.toString() + " & " + s.toString())
                            println("이미지 유알엘 리스트" + imageUriList[index].toString() + "&" + s.toString())

                        }



                        binding.activityAddShopContentRecyclerPhoto.adapter?.notifyDataSetChanged()


                    }
                }
            }
    }

    fun updateContent(){
        progressDialog?.show()
        var tsDoc = firestore?.collection("contents")?.document("shop")?.collection("data")?.document(postUid!!)
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentShopDTO::class.java)

            contentDTO?.imageDownLoadUrlList?.clear()

            contentDTO?.cost = binding.activityAddShopContentEdittextCost.text.toString()
            contentDTO?.productExplain = binding.activityAddShopContentEdittextProductExplain.text.toString()
            contentDTO?.explain = binding.activityAddShopContentEdittextExplain.text.toString()


            imageUriList.forEach {
                contentDTO?.imageDownLoadUrlList?.add(it.toString())
            }



            transaction.set(tsDoc, contentDTO!!)



        }?.addOnFailureListener {
            println("viewCountIncreaseFail ${it.toString()}")
        }?.addOnCompleteListener {
            Toast.makeText(binding.root.context, "게시글 수정 완료", Toast.LENGTH_LONG).show()
            progressDialog?.dismiss()
            finish()
        }
    }

    fun checkSaveData() {
        if (SharedData.prefs.getString("addShopData", "null").toString().equals("exist")) {
            var builder = AlertDialog.Builder(binding.root.context)

            builder.apply {
                setMessage("임시 저장된 게시글이 존재합니다. \n 불러오시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->

                    binding.activityAddShopContentEdittextExplain.setText(
                        SharedData.prefs.getString(
                        "addShopDataExplain",
                        ""))
                    binding.activityAddShopContentEdittextProductExplain.setText(
                        SharedData.prefs.getString(
                        "addShopDataTitle",
                        ""))
                    binding.activityAddShopContentEdittextCost.setText(
                        SharedData.prefs.getString("addShopDataCost",
                        ""))

                    Toast.makeText(binding.root.context, "사진을 제외한 모든 정보가 정상적으로 불러와졌습니다.", Toast.LENGTH_LONG).show()



                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->


                    return@OnClickListener

                })
                setTitle("안내")
                show()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        var builder = AlertDialog.Builder(binding.root.context)

        if (binding.activityAddShopContentEdittextExplain.text.length > 1 || binding.activityAddShopContentEdittextProductExplain.text.length > 1 || binding.activityAddShopContentEdittextCost.text.length > 1) {

            builder.apply {
                setMessage("게시글을 임시 저장 하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addShopData", "exist")
                    SharedData.prefs.setString("addShopDataCost",
                        binding.activityAddShopContentEdittextCost.text.toString())
                    SharedData.prefs.setString("addShopDataTitle",
                        binding.activityAddShopContentEdittextProductExplain.text.toString())
                    SharedData.prefs.setString("addShopDataCategory", pickCategoryData.toString())
                    SharedData.prefs.setString("addShopDataExplain",
                        binding.activityAddShopContentEdittextExplain.text.toString())
                    finish()
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addShopData", "none")
                    SharedData.prefs.setString("addShopDataCost",
                        "")
                    SharedData.prefs.setString("addShopDataTitle",
                        "")
                    SharedData.prefs.setString("addShopDataCategory", "")
                    SharedData.prefs.setString("addShopDataExplain",
                        "")
                    finish()
                    return@OnClickListener

                })
                setTitle("안내")
                show()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun contentUpload() {
        progressDialog?.show()
        if (count < imageUriList.size && imageUriList.size != 0) {
            uploadPhoto(imageUriList[count])
        } else if (imageUriList.size == 0) {
            //사진을 제외한 컨텐츠 내용만 Firestore에 업로드
            uploadContentDetail()
        } else if (count == imageUriList.size) {
            //컨텐츠 내용 Firestore에 업로드
            uploadContentDetail()

        }


    }

    fun uploadContentDetail() {

        var contentShopDTO = ContentShopDTO()

        //게시글 내용
        contentShopDTO.explain = binding.activityAddShopContentEdittextExplain.text.toString()
        //제품 설명
        contentShopDTO.productExplain =
            binding.activityAddShopContentEdittextProductExplain.text.toString()
        //제품 가격
        contentShopDTO.cost = binding.activityAddShopContentEdittextCost.text.toString() + "원"
        //카테고리
        contentShopDTO.category = pickCategoryData.toString()
        //사진 리스트
        contentShopDTO.imageDownLoadUrlList = this.imageDownLoadUriList
        //uid
        contentShopDTO.uid = auth?.currentUser?.uid
        //email
        contentShopDTO.userId = auth?.currentUser?.email
        //timeStamp
        contentShopDTO.timeStamp = System.currentTimeMillis()
        //time
        contentShopDTO.time = TimeUtil().getTime()
        //유저 닉네임
        contentShopDTO.userNickName = userNickName

        //유저 주소
        if (address != null)
            contentShopDTO.sellerAddress = getAddressCityAndLoad()

        //비교 전용 cost
        contentShopDTO.costInt = binding.activityAddShopContentEdittextCost.text.toString()

        //파스에 set
        firestore?.collection("contents")?.document("shop")?.collection("data")?.document()
            ?.set(contentShopDTO)
            ?.addOnSuccessListener {
                progressDialog?.dismiss()
            }?.addOnFailureListener {
                progressDialog?.dismiss()
            }

        setResult(Activity.RESULT_OK)

        finish()


    }

    fun getAddressCityAndLoad() : String{

        var city = "city"
        var path = "null"
        var load = "load"

        if (address != null) {
            var index = address?.indexOf(" ")
            city = address?.substring(0, index!!)!!
            path = address?.substring(index!! +1)!!
            var index2 = path?.indexOf(" ")
            load = path?.substring(0,index2!!)
        }



        return city+" "+load
    }


    fun uploadPhoto(uri: Uri) {
        //var timestamp = SimpleDateFormat("yy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
        var timestamp = TimeUtil().getTime()
        var imageFileName = "Windy_IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("contents_shop")?.child(imageFileName)

        storageRef?.putFile(uri)
            ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->

                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->

                count++
                imageDownLoadUriList.add(uri.toString())

                imageDownLoadUriList.forEach { i ->
                    Log.d("URI 리스트으으으으으", i.toString())
                }
                contentUpload()

            }
    }

    //앨범에서 선택된 이미지 파일을 가져오는 메서드
    fun addPhoto() {
        val intent = Intent(Intent.ACTION_PICK).apply {

            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        }
        startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                //this is path to the selected image
                photoUri = data?.data
                imageUriList.add(photoUri!!)

                imageUriList.forEach { i ->
                    System.out.println("이미지 Uri = " + i)
                }

            } else {
                //exit the addphoto activity if you leave the album without selecting it
                finish()
            }
        }
        //activity_add_content_recycler_photo.adapter?.notifyDataSetChanged()
        binding.activityAddShopContentRecyclerPhoto.adapter?.notifyDataSetChanged()
    }

    //스피너에서 선택된 아이템 저장
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        pickCategoryData = binding.activityAddShopContentSpinnerCategory.selectedItem.toString()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }
}