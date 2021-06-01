package com.uos.project_new_windy.newwindymall

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.malloption.BottomSheetDialogMallOption
import com.uos.project_new_windy.databinding.ActivityAddProductBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.addcontentadapter.AddSellContentActivityRecyclerViewAdapter
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.TimeUtil

class AddProductActivity : AppCompatActivity(), BottomSheetDialogMallOption.BottomSheetButtonClickListener {

    lateinit var binding : ActivityAddProductBinding


    private var imageUriList = arrayListOf<Uri>()
    private var imageDownLoadUriList = arrayListOf<String>()
    private var count = 0
    private var category : String ? = null

    var progressDialog: ProgressDialogLoading? = null

    var PICK_IMAGE_FROM_ALBUM = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_product)
        binding.activityaddproduct = this


        //로딩 초기화
        progressDialog = ProgressDialogLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        binding.activityAddProductButtonCategory.setOnClickListener {
            val bottomSheetDialog : BottomSheetDialogMallOption = BottomSheetDialogMallOption()

            bottomSheetDialog.show(supportFragmentManager,"lol")
        }


        binding.activityAddPhotoRecyclerview.adapter =
            ProductPhotoRecyclerAdapter(this, imageUriList)
        binding.activityAddPhotoRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        //상품 사진
        binding.activityAddProductImagebuttonPhotolistAdd.setOnClickListener {
            addPhoto()
        }

        binding.activityAddProductImagebuttonClose.setOnClickListener { finish() }

        binding.activityAddProductButtonUpload.setOnClickListener {
            if (imageUriList.size < 1){
                Toast.makeText(this,"사진을 한장 이상 추가해주세요.",Toast.LENGTH_LONG).show()
            }else if (binding.activityAddProductEdittextTitle.text.isEmpty()) {
                Toast.makeText(this, "상품 제목을 입력해주세요.", Toast.LENGTH_LONG).show()
            }else if (binding.activityAddProductEdittextExplain.text.isEmpty()){
                Toast.makeText(this,"상품 설명을 입력해주세요.", Toast.LENGTH_LONG).show()
            }else if (category == null){
                Toast.makeText(this,"카테고리를 선택해주세요.",Toast.LENGTH_LONG).show()
            }
            else{
                progressDialog!!.show()
                contentUpload()
            }
        }
    }

    override fun onBottomSheetButtonClick(text: String) {
        category = text
        binding.activityAddProductCategory.text = category
    }

    fun contentUpload(){
        if (count < imageUriList.size && imageUriList.size != 0){
            uploadPhoto(imageUriList[count])
        }else{
            uploadProduct()
        }
    }

    fun uploadPhoto(uri : Uri){
        var timestamp = TimeUtil().getTime()
        var imageFileName = "Windy_Image_" + timestamp + "_.png"

        var storageRef = FirebaseStorage.getInstance().reference.child("product").child(imageFileName)

        storageRef.putFile(uri)?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->

            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->


            count ++
            imageDownLoadUriList.add(uri.toString())

            contentUpload()

        }
    }

    fun uploadProduct(){
        var data = MallMainModel.Product()
        data.cost = binding.activityAddProductEdittextCost.text.toString().toLong()
        data.title = binding.activityAddProductEdittextTitle.text.toString()
        data.explain = binding.activityAddProductEdittextExplain.text.toString()
        data.category = category
        if (imageDownLoadUriList != null){
            data.imageUrlList = imageDownLoadUriList
        }
        data.timestamp = System.currentTimeMillis()

        uploadDB(data)


    }
    
    fun uploadDB(data : MallMainModel.Product){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product")
            .add(data).addOnCompleteListener { 
                println("성공")
                progressDialog!!.dismiss()
                finish()
            }.addOnFailureListener { 
                println("실패")
            }  
    }

    fun addPhoto(){
        Toast.makeText(this, "사진을 꾹 누르시면 여러장을 올릴 수 있어요.", Toast.LENGTH_LONG).show()

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
        binding.activityAddPhotoRecyclerview.adapter?.notifyDataSetChanged()
    }
}