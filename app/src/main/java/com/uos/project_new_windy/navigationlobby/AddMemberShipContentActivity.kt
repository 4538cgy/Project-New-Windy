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
import com.uos.project_new_windy.databinding.ActivityAddMemberShipContentBinding
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddMemberShipContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddSellContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.SharedData
import com.uos.project_new_windy.util.TimeUtil

class AddMemberShipContentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var binding : ActivityAddMemberShipContentBinding

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var imageUriList: ArrayList<Uri> = arrayListOf()
    var address : String ? = null

    //이미지 갯수 체크를 위한 변수
    var count: Int = 0;
    var imageDownLoadUriList: ArrayList<String> = arrayListOf()
    var pickCategoryData: String? = null
    var progressDialog: ProgressDialogLoading? = null
    var userNickName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_member_ship_content)
        binding.activityaddmembershipcontent = this@AddMemberShipContentActivity

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
        binding.activityAddMemberShipContentSpinnerCategory.adapter = spinnerAdapter

        binding.activityAddMemberShipContentSpinnerCategory.onItemSelectedListener = this

        binding.activityAddMemberShipContentImagebuttonBack.setOnClickListener {
            finish()
        }
        //사진이 추가될 리사이클러뷰 초기화
        binding.activityAddMemberShipContentRecyclerPhoto.adapter =
            AddMemberShipContentActivityRecyclerViewAdapter(this, imageUriList)
        binding.activityAddMemberShipContentRecyclerPhoto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //사진추가
        binding.activityAddMemberShipContentImagebuttonAddphoto.setOnClickListener {
            addPhoto()
        }

        //게시글 업로드
        binding.activityAddMemberShipContentButtonUpload.setOnClickListener {
            if (imageUriList.size <= 0) {
                Toast.makeText(this, "사진을 한장 이상 포함해주세요.", Toast.LENGTH_LONG).show()
            } else {
                contentUpload()
            }
        }


        //임시 저장된 게시글 확인
        checkSaveData()
    }

    fun checkSaveData() {
        if (SharedData.prefs.getString("addMembershipData", "null").toString().equals("exist")) {
            var builder = AlertDialog.Builder(binding.root.context)

            builder.apply {
                setMessage("임시 저장된 게시글이 존재합니다. \n 불러오시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->

                    binding.activityAddMemberShipContentEdittextExplain.setText(SharedData.prefs.getString(
                        "addMembershipDataExplain",
                        ""))
                    binding.activityAddMemberShipContentEdittextProductExplain.setText(SharedData.prefs.getString(
                        "addMembershipDataTitle",
                        ""))
                    binding.activityAddMemberShipContentEdittextCost.setText(SharedData.prefs.getString("addMembershipDataCost",
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

        if (binding.activityAddMemberShipContentEdittextExplain.text.length > 1 || binding.activityAddMemberShipContentEdittextProductExplain.text.length > 1 || binding.activityAddMemberShipContentEdittextCost.text.length > 1) {

            builder.apply {
                setMessage("게시글을 임시 저장 하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addMembershipData", "exist")
                    SharedData.prefs.setString("addMembershipDataCost",
                        binding.activityAddMemberShipContentEdittextCost.text.toString())
                    SharedData.prefs.setString("addMembershipDataTitle",
                        binding.activityAddMemberShipContentEdittextProductExplain.text.toString())
                    SharedData.prefs.setString("addMembershipDataCategory", pickCategoryData.toString())
                    SharedData.prefs.setString("addMembershipDataExplain",
                        binding.activityAddMemberShipContentEdittextExplain.text.toString())
                    finish()
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addMembershipData", "none")
                    SharedData.prefs.setString("addMembershipDataCost",
                        "")
                    SharedData.prefs.setString("addMembershipDataTitle",
                        "")
                    SharedData.prefs.setString("addMembershipDataCategory", "")
                    SharedData.prefs.setString("addMembershipDataExplain",
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

        var contentSellDTO = ContentSellDTO()

        //게시글 내용
        contentSellDTO.explain = binding.activityAddMemberShipContentEdittextExplain.text.toString()
        //제품 설명
        contentSellDTO.productExplain =
            binding.activityAddMemberShipContentEdittextProductExplain.text.toString()
        //제품 가격
        contentSellDTO.cost = binding.activityAddMemberShipContentEdittextCost.text.toString() + "원"
        //카테고리
        contentSellDTO.category = pickCategoryData.toString()
        //사진 리스트
        contentSellDTO.imageDownLoadUrlList = this.imageDownLoadUriList
        //uid
        contentSellDTO.uid = auth?.currentUser?.uid
        //email
        contentSellDTO.userId = auth?.currentUser?.email
        //timeStamp
        contentSellDTO.timeStamp = System.currentTimeMillis()
        //time
        contentSellDTO.time = TimeUtil().getTime()
        //유저 닉네임
        contentSellDTO.userNickName = userNickName

        //유저 주소
        if (address != null)
            contentSellDTO.sellerAddress = getAddressCityAndLoad()

        //비교 전용 cost
        contentSellDTO.costInt = binding.activityAddMemberShipContentEdittextCost.text.toString()

        //파스에 set
        firestore?.collection("contents")?.document("membership")?.collection("data")?.document()
            ?.set(contentSellDTO)
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

        var storageRef = storage?.reference?.child("contents_membership")?.child(imageFileName)

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
        binding.activityAddMemberShipContentRecyclerPhoto.adapter?.notifyDataSetChanged()
    }

    //스피너에서 선택된 아이템 저장


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        pickCategoryData = binding.activityAddMemberShipContentSpinnerCategory.selectedItem.toString()
        System.out.println("으아아아" + pickCategoryData)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }
}