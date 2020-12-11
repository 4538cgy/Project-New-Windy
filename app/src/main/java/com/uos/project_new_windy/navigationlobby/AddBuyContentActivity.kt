package com.uos.project_new_windy.navigationlobby

import android.app.Activity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.TimeUtil

class AddBuyContentActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener {

    var PICK_IMAGE_FROM_ALBUM = 0

    lateinit var binding : ActivityAddBuyContentBinding
    var photoUri : Uri? = null
    var storage : FirebaseStorage = FirebaseStorage.getInstance()
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var progressDialog : ProgressDialogLoading? = null
    var pickCategory : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_buy_content)
        binding.buycontent = this@AddBuyContentActivity


        val items = resources.getStringArray(R.array.content_category)
        val spinnerAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,items)
        binding.activityAddBuyContentSpinnerCategory.adapter = spinnerAdapter
        binding.activityAddBuyContentSpinnerCategory.onItemSelectedListener = this

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

            System.out.println("클릭클릭클릭" + pickCategory.toString())

            if (photoUri != null && pickCategory != null) {
                uploadPhoto()
            }else{
                if (photoUri == null)
                Toast.makeText(this,"사진을 추가해주세요." , Toast.LENGTH_LONG).show()

                if (pickCategory == null)
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
        contentBuyDTO.categoryHash = pickCategory
        contentBuyDTO.cost = binding.activityAddBuyContentEdittextCost.text.toString() + "원"
        contentBuyDTO.commentCount = 0
        //비교 전용 cost
        contentBuyDTO.costInt = binding.activityAddBuyContentEdittextCost.text as Int


        firestore?.collection("contents")?.document("buy")?.collection("data")?.document()?.set(contentBuyDTO)
            .addOnSuccessListener {
                progressDialog?.dismiss()
            }.addOnFailureListener {
                progressDialog?.dismiss()
            }

        setResult(Activity.RESULT_OK)

        finish()
    }

    fun uploadPhoto(){
        progressDialog?.show()
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        pickCategory = binding.activityAddBuyContentSpinnerCategory.selectedItem.toString()
        System.out.println("으아아아아" + pickCategory.toString())
        System.out.println("으아아아아2" + binding.activityAddBuyContentSpinnerCategory.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        System.out.println("선택되지않음")

    }
}