package com.uos.project_new_windy.navigationlobby

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.util.TimeUtil

class AddBuyContentActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM = 0

    lateinit var binding : ActivityAddBuyContentBinding
    var photoUri : Uri? = null
    var storage : FirebaseStorage = FirebaseStorage.getInstance()
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth : FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_buy_content)
        binding.buycontent = this@AddBuyContentActivity

        binding.activityAddBuyContentImagebuttonBack.setOnClickListener {
            finish()
        }

        //카테고리 버튼 클릭
        binding.activityAddBuyContentImagebuttonCategory.setOnClickListener {

        }
        
        //게시글 업로드
        binding.activityAddBuyContentButtonUpload.setOnClickListener {
            if (photoUri != null && !binding.activityAddBuyContentTextviewCategory.text.toString().equals("카테고리")) {
                uploadPhoto()
            }else{
                if (photoUri == null)
                Toast.makeText(this,"사진을 추가해주세요." , Toast.LENGTH_LONG).show()

                if (binding.activityAddBuyContentTextviewCategory.text.toString().equals("카테고리"))
                    Toast.makeText(this,"카테고리를 추가해주세요." , Toast.LENGTH_LONG).show()
            }
            
            
        }

        //사진 추가
        binding.activityAddBuyContentImageviewProductImage.setOnClickListener {
            addPhoto()
        }
    }

    fun contentUpload(uri : Uri){

        var contentBuyDTO = ContentBuyDTO()

        contentBuyDTO.userId = auth.currentUser?.email
        contentBuyDTO.uid = auth.currentUser?.uid
        contentBuyDTO.time = TimeUtil().getTime()
        contentBuyDTO.imageUrl = uri.toString()
        contentBuyDTO.timeStamp = ServerValue.TIMESTAMP
        contentBuyDTO.explain = binding.activityAddBuyContentEdittextExplain.text.toString()
        contentBuyDTO.favoriteCount = 0
        contentBuyDTO.categoryHash = binding.activityAddBuyContentTextviewCategory.text.toString()
        contentBuyDTO.cost = binding.activityAddBuyContentEdittextCost.text.toString() + "원"



        firestore?.collection("contents")?.document("buy")?.collection("data")?.document()?.set(contentBuyDTO)

        setResult(Activity.RESULT_OK)

        finish()
    }

    fun uploadPhoto(){
        //var timestamp = SimpleDateFormat("yy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
        var timestamp = TimeUtil().getTime()
        var imageFileName = "Windy_IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("contents")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->

            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->



            contentUpload(uri)

        }
    }


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


            }else{
                //exit the addphoto activity if you leave the album without selecting it
                finish()
            }
        }
        //activity_add_content_recycler_photo.adapter?.notifyDataSetChanged()
        binding.activityAddBuyContentImageviewProductImage.setImageURI(photoUri)
    }
}