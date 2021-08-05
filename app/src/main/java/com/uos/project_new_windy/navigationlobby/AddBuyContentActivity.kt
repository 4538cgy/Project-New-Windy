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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddSellContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.SharedData
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.item_image_list.view.*

class AddBuyContentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var PICK_IMAGE_FROM_ALBUM = 0

    lateinit var binding: ActivityAddBuyContentBinding
    var photoUri: Uri? = null
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var progressDialog: ProgressDialogLoading? = null
    var imageUriList: ArrayList<Uri> = arrayListOf()

    var userNickName: String? = null

    //새 게시글 작성인지 or 수정 인지 확인하기 위한 변수
    var updateCheck: Boolean? = null
    var postUid: String? = null


    //이미지 갯수 체크를 위한 변수
    var count: Int = 0;
    var imageDownLoadUriList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_buy_content)
        binding.buycontent = this@AddBuyContentActivity
        val items = resources.getStringArray(R.array.content_category)
        // true = 수정로직 / false = 새게시글 작성 로직
        updateCheck = intent.getBooleanExtra("updateCheck", false)
        postUid = intent.getStringExtra("postUid")
        //수정하기면 해당 게시글의 정보 불러오기
        if (updateCheck == true) {
            updateDataLoad()
        }
        //유저 닉네임 가져오기
        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!)
            .document("accountInfo")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null) {
                    userNickName = documentSnapshot.get("userName")?.toString()
                }

            }
        //사진이 추가될 리사이클러뷰 초기화
        binding.activityAddBuyContentRecyclerPhoto.adapter =
            AddSellContentActivityRecyclerViewAdapter(this, imageUriList)
        binding.activityAddBuyContentRecyclerPhoto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //로딩 초기화
        progressDialog = ProgressDialogLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        binding.activityAddBuyContentImagebuttonBack.setOnClickListener {
            finish()
        }
        //게시글 업로드
        binding.activityAddBuyContentButtonUpload.setOnClickListener {
            println("게시글 업로드")
            if (updateCheck == false) {
                contentUploadCheck()
            } else {
                updateContent()
            }

        }
        //사진 추가
        binding.activityAddBuyContentImageviewProductImage.setOnClickListener {
            addPhoto()
        }
        checkSaveData()
    }

    fun checkSaveData() {
        if (SharedData.prefs.getString("addBuyData", "null").toString().equals("exist")) {
            var builder = AlertDialog.Builder(binding.root.context)

            builder.apply {
                setMessage("임시 저장된 게시글이 존재합니다. \n 불러오시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->

                    binding.activityAddBuyContentEdittextExplain.setText(
                        SharedData.prefs.getString(
                            "addBuyDataExplain",
                            ""
                        )
                    )
                    Toast.makeText(
                        binding.root.context,
                        "사진을 제외한 모든 정보가 정상적으로 불러와졌습니다.",
                        Toast.LENGTH_LONG
                    ).show()


                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setTitle("안내")
                show()
            }
        }
    }

    fun contentUploadCheck() {
        progressDialog?.show()
        if (count < imageUriList.size && imageUriList.size != 0) {
            uploadPhoto(imageUriList[count])
        } else if (imageUriList.size == 0) {
            //사진을 제외한 컨텐츠 내용만 Firestore에 업로드
            contentUpload()
        } else if (count == imageUriList.size) {
            //컨텐츠 내용 Firestore에 업로드
            contentUpload()

        }


    }

    fun updateDataLoad() {
        firestore?.collection("contents")?.document("buy")?.collection("data")?.document(postUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        var contentData = documentSnapshot.toObject(ContentBuyDTO::class.java)
                        binding.activityAddBuyContentEdittextExplain.setText(contentData?.explain.toString())
                    }
                }
            }
    }

    fun updateContent() {
        progressDialog?.show()
        var tsDoc = firestore?.collection("contents")?.document("buy")?.collection("data")
            ?.document(postUid!!)
        firestore?.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentBuyDTO::class.java)
            contentDTO?.explain = binding.activityAddBuyContentEdittextExplain.text.toString()
            contentDTO?.imageUrl = photoUri.toString()
            transaction.set(tsDoc, contentDTO!!)
        }?.addOnFailureListener {
        }?.addOnCompleteListener {
            Toast.makeText(binding.root.context, "게시글 수정 완료", Toast.LENGTH_LONG).show()
            progressDialog?.dismiss()
            finish()
        }
    }

    override fun onBackPressed() {
        var builder = AlertDialog.Builder(binding.root.context)
        if (binding.activityAddBuyContentEdittextExplain.text.length > 1) {
            builder.apply {
                setMessage("게시글을 임시 저장 하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addBuyData", "exist")
                    SharedData.prefs.setString(
                        "addBuyDataExplain",
                        binding.activityAddBuyContentEdittextExplain.text.toString()
                    )
                    finish()
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addBuyData", "none")
                    SharedData.prefs.setString(
                        "addBuyDataExplain",
                        ""
                    )
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

        var contentBuyDTO = ContentBuyDTO()

        contentBuyDTO.userId = auth.currentUser?.email
        contentBuyDTO.uid = auth.currentUser?.uid
        contentBuyDTO.time = TimeUtil().getTime()
        contentBuyDTO.imageDownLoadUriList = imageDownLoadUriList
        contentBuyDTO.timeStamp = System.currentTimeMillis()
        contentBuyDTO.explain = binding.activityAddBuyContentEdittextExplain.text.toString()
        contentBuyDTO.favoriteCount = 0
        contentBuyDTO.commentCount = 0
        contentBuyDTO.userNickName = userNickName
        firestore?.collection("contents")?.document("buy")?.collection("data")?.document()
            ?.set(contentBuyDTO)
            .addOnSuccessListener {
                progressDialog?.dismiss()
            }.addOnFailureListener {
                progressDialog?.dismiss()
            }

        setResult(Activity.RESULT_OK)

        finish()
    }

    fun uploadPhoto(uri : Uri) {
        progressDialog?.show()
        //var timestamp = SimpleDateFormat("yy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
        var timestamp = TimeUtil().getTime()
        var imageFileName = "Windy_IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("contents")?.child(imageFileName)

        storageRef?.putFile(uri!!)
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


    fun addPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            println("꾸앸")
            /*
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
             */
            //val thumbnail : Bitmap = data!!.getParcelableExtra("data")
            if (data!!.clipData != null) {
                val count = data.clipData!!.itemCount
                var currentItem = 0
                while (currentItem < count) {
                    val imageUri =
                        data.clipData!!.getItemAt(currentItem).uri
                    imageUriList.add(imageUri)
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    currentItem = currentItem + 1
                }
            } else {
                val fullPhotoUri: Uri = data!!.data!!
                imageUriList.add(fullPhotoUri)
            }


        } else {
            finish()
        }
        //activity_add_content_recycler_photo.adapter?.notifyDataSetChanged()
        binding.activityAddBuyContentRecyclerPhoto.adapter?.notifyDataSetChanged()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}