package com.uos.project_new_windy.navigationlobby

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.databinding.ActivityAddContentBinding
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddNormalContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.SharedData

import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.activity_add_content.*
import kotlinx.android.synthetic.main.item_image_list.view.*
import kotlin.collections.ArrayList

class AddContentActivity : AppCompatActivity() {


    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage ? = null

    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore ? = null
    var imageUriList : ArrayList<Uri> = arrayListOf()
    //이미지 갯수 체크를 위한 변수
    var count: Int = 0;
    var imageDownLoadUriList : ArrayList<String> = arrayListOf()
    var progressDialog : ProgressDialogLoading ? = null
    var userNickName : String ? = null

    //새 게시글 작성인지 or 수정 인지 확인하기 위한 변수
    var updateCheck : Boolean ? = null
    var postUid : String ? = null

    lateinit var binding : ActivityAddContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_content)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_content)
        binding.addnormalcontent = this@AddContentActivity



        
        //로딩 초기화
        progressDialog = ProgressDialogLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)
        //파이어베이스 초기화
        storage  = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // true = 수정 로직 / false = 새 게시글 작성 로직
        updateCheck = intent.getBooleanExtra("updateCheck",false)
        postUid = intent.getStringExtra("postUid")


        //수정하기면 해당 게시글의 정보 가져오기
        if(updateCheck == true){
            updateDataLoad()
        }

        //유저 닉네임 가져오기
        firestore!!.collection("userInfo").document("userData").collection(auth!!.currentUser?.uid!!).document("accountInfo")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()
                }

            }

        //앨범 열기
        //addPhoto()



        //리사이클러뷰 추가
        //activity_add_content_recycler_photo.adapter = AddContentActivityRecyclerViewAdapter()
        //activity_add_content_recycler_photo.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        binding.activityAddContentRecyclerPhoto.adapter = AddNormalContentActivityRecyclerViewAdapter(this,imageUriList)
        binding.activityAddContentRecyclerPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)


        //뒤로가기
        binding.activityAddNormalContentImagebuttonBack.setOnClickListener {
            finish()
        }
        
        //이미지 추가
        binding.activityAddContentButtonAddPhoto.setOnClickListener {
            addPhoto()
        }



        //이미지 업로드 이벤트 추가
        binding.activityAddContentButtonUpload.setOnClickListener {
            if (binding.activityAddContentEdittextContent.text.length < 6)
            {
                Toast.makeText(this,"내용을 6자 이상 적어주세요.",Toast.LENGTH_LONG).show()
            }else if(imageUriList.size < 1){
                Toast.makeText(this,"사진을 추가해주세요.",Toast.LENGTH_LONG).show()
            }

            else {

                //게시글 작성
                if (updateCheck == false){
                    contentUpload()
                    
                }
                //게시글 수정
                else {
                    updateContent()
                }
            }
            
        }


        checkSaveData()
    }

    //수정할 게시글 가져오기
    fun updateDataLoad(){
        firestore?.collection("contents")?.document("normal")?.collection("data")?.document(postUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot != null){
                    if (documentSnapshot.exists()){

                        imageUriList.clear()

                        var contentData = documentSnapshot.toObject(ContentNormalDTO::class.java)
                        binding.activityAddContentEdittextContent.setText(contentData?.explain)

                        contentData?.imageDownLoadUrlList?.forEachIndexed { index, s ->

                            imageUriList.add(Uri.parse(s))
                            println("이미지다운로드유알엘리스트 " + index.toString() + " & " + s.toString())
                            println("이미지 유알엘 리스트" + imageUriList[index].toString() + "&" + s.toString())

                        }



                        binding.activityAddContentRecyclerPhoto.adapter?.notifyDataSetChanged()


                    }
                }
            }
    }

    //게시글 업데이트
    fun updateContent(){
        progressDialog?.show()
        var tsDoc = firestore?.collection("contents")?.document("normal")?.collection("data")?.document(postUid!!)
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentNormalDTO::class.java)

            contentDTO?.imageDownLoadUrlList?.clear()

            contentDTO?.explain = binding.activityAddContentEdittextContent.text.toString()


            imageUriList.forEach {
                contentDTO?.imageDownLoadUrlList?.add(it.toString())
            }



            transaction.set(tsDoc, contentDTO!!)



        }?.addOnFailureListener {
            println("viewCountIncreaseFail ${it.toString()}")
        }?.addOnCompleteListener {
            Toast.makeText(binding.root.context, "게시글 수정 완료",Toast.LENGTH_LONG).show()
            progressDialog?.dismiss()
            finish()
        }
    }

    
    //게시글을 DB에 올리는 메서드
    fun contentUpload(){
        progressDialog?.show()
        if(count < imageUriList.size && imageUriList.size != 0) {
            uploadPhoto(imageUriList[count])
        }else if(imageUriList.size == 0){
            //사진을 제외한 컨텐츠 내용만 Firestore에 업로드
            uploadContentDetail()
        }else if(count == imageUriList.size){
            //컨텐츠 내용 Firestore에 업로드
            uploadContentDetail()

        }


    }

    fun checkSaveData() {
        if (SharedData.prefs.getString("addNormalData", "null").toString().equals("exist")) {
            var builder = AlertDialog.Builder(binding.root.context)

            builder.apply {
                setMessage("임시 저장된 게시글이 존재합니다. \n 불러오시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->

                    binding.activityAddContentEdittextContent.setText(SharedData.prefs.getString(
                        "addNormalDataExplain",
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

        if (binding.activityAddContentEdittextContent.text.length > 1 ) {

            builder.apply {
                setMessage("게시글을 임시 저장 하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addNormalData", "exist")
                    SharedData.prefs.setString("addNormalDataExplain",
                        binding.activityAddContentEdittextContent.text.toString())
                    finish()
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addNormalData", "none")

                    SharedData.prefs.setString("addNormalDataExplain",
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

    fun uploadContentDetail(){
                    /*
                    var contentDTO = ContentDTO()


                    //contentDTO.title = activity_add_content_edittext_title.text.toString()

                    contentDTO.imageDownLoadUrlList = this.imageDownLoadUriList

                    contentDTO.uid = auth?.currentUser?.uid

                    contentDTO.commentCount = 0

                    contentDTO.userId = auth?.currentUser?.email

                    contentDTO.explain = activity_add_content_edittext_content.text.toString()

                    contentDTO.timestamp = System.currentTimeMillis()

                    contentDTO.time = TimeUtil().getTime()

                     */
                    var contentNormalDTO  = ContentNormalDTO()
                    contentNormalDTO.commentCount = 0
                    contentNormalDTO.explain = binding.activityAddContentEdittextContent.text.toString()
                    contentNormalDTO.favoriteCount = 0
                    contentNormalDTO.imageDownLoadUrlList = this.imageDownLoadUriList
                    contentNormalDTO.time = TimeUtil().getTime()
                    contentNormalDTO.uid = auth?.currentUser?.uid
                    contentNormalDTO.userId = auth?.currentUser?.email
                    contentNormalDTO.timestamp = System.currentTimeMillis()
                    contentNormalDTO.userNickName = userNickName





                    //firestore?.collection("contents")?.document()?.set(contentDTO)
                    firestore?.collection("contents")?.document("normal")?.collection("data")?.document()?.set(contentNormalDTO)
                        ?.addOnSuccessListener {
                            progressDialog?.dismiss()
                        }?.addOnFailureListener {
                            progressDialog?.dismiss()
                        }

                    setResult(Activity.RESULT_OK)

                    finish()



    }


    fun uploadPhoto(uri : Uri){
        //var timestamp = SimpleDateFormat("yy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
        var timestamp = TimeUtil().getTime()
        var imageFileName = "Windy_IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("contents")?.child(imageFileName)

        storageRef?.putFile(uri)?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->

            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->

            count ++
            imageDownLoadUriList.add(uri.toString())

            imageDownLoadUriList.forEach {i ->
                Log.d("URI 리스트으으으으으" , i.toString())
            }
            contentUpload()

        }
    }


    
    //앨범에서 선택된 이미지 파일을 가져오는 메서드
    fun addPhoto(){
        val intent = Intent(Intent.ACTION_PICK).apply {

            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        }
        startActivityForResult(intent,PICK_IMAGE_FROM_ALBUM)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM)
        {
            if (resultCode == Activity.RESULT_OK){
                //this is path to the selected image
                photoUri = data?.data
                imageUriList.add(photoUri!!)

                imageUriList.forEach {
                        i->
                    System.out.println("이미지 Uri = "+ i)
                }

            }else{
                //exit the addphoto activity if you leave the album without selecting it
                finish()
            }
        }
        //activity_add_content_recycler_photo.adapter?.notifyDataSetChanged()
        binding.activityAddContentRecyclerPhoto.adapter?.notifyDataSetChanged()
    }
    /*
    inner class AddContentActivityRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                viewHolder.item_image_list_imageview.setImageURI(imageUriList[position])
            }


        }

        override fun getItemCount(): Int {
            return imageUriList.size
        }

    }

     */
}


