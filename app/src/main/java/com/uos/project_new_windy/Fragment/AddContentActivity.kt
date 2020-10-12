package com.uos.project_new_windy.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bolts.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.Model.ContentDTO
import com.uos.project_new_windy.R
import kotlinx.android.synthetic.main.activity_add_content.*
import kotlinx.android.synthetic.main.item_image_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddContentActivity : AppCompatActivity() {


    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage ? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore ? = null
    var imageUriList : ArrayList<Uri> = arrayListOf()
    var count: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_content)



        //파이어베이스 초기화
        storage  = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        //앨범 열기
        addPhoto()



        //리사이클러뷰 추가
        activity_add_content_recycler_photo.adapter = AddContentActivityRecyclerViewAdapter()
        activity_add_content_recycler_photo.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)


        //이미지 추가 이벤트
        activity_add_content_button_add_photo.setOnClickListener {
            addPhoto()

        }



        //이미지 업로드 이벤트 추가
        activity_add_content_button_upload.setOnClickListener {
            contentUpload()

        }



    }
    
    //게시글을 DB에 올리는 메서드
    fun contentUpload(){
        if(count < imageUriList.size && imageUriList.size != 0) {
            uploadPhoto(imageUriList[count])
        }else if(imageUriList.size == 0){
            //사진을 제외한 컨텐츠 내용만 Firestore에 업로드
        }else if(count == imageUriList.size){
            //컨텐츠 내용 Firestore에 업로드

        }


    }



    fun uploadPhoto(uri : Uri){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "Windy_IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("contents")?.child(imageFileName)
        storageRef?.putFile(uri)?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->

            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->

            count ++
            contentUpload()
            /*
                if (imageUriList.lastOrNull() != null){
                    var contentDTO = ContentDTO()

                    contentDTO.imageUrl = uri.toString()

                    contentDTO.uid = auth?.currentUser?.uid

                    contentDTO.userId = auth?.currentUser?.email

                    contentDTO.explain = activity_add_content_edittext_content.text.toString()

                    contentDTO.timestamp = System.currentTimeMillis()

                    firestore?.collection("contents")?.document()?.set(contentDTO)

                    setResult(Activity.RESULT_OK)

                    finish()
                }

             */


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
        activity_add_content_recycler_photo.adapter?.notifyDataSetChanged()
    }

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
}


