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
import com.uos.project_new_windy.util.SharedData
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
    var userNickName : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_buy_content)
        binding.buycontent = this@AddBuyContentActivity


        val items = resources.getStringArray(R.array.content_category)
        val spinnerAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,items)
        binding.activityAddBuyContentSpinnerCategory.adapter = spinnerAdapter
        binding.activityAddBuyContentSpinnerCategory.onItemSelectedListener = this

        //유저 닉네임 가져오기
        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()
                }

            }


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

        checkSaveData()
    }

    fun checkSaveData() {
        if (SharedData.prefs.getString("addBuyData", "null").toString().equals("exist")) {
            var builder = AlertDialog.Builder(binding.root.context)

            builder.apply {
                setMessage("임시 저장된 게시글이 존재합니다. \n 불러오시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->

                    binding.activityAddBuyContentEdittextExplain.setText(SharedData.prefs.getString(
                        "addBuyDataExplain",
                        ""))
                    binding.activityAddBuyContentEdittextCost.setText(SharedData.prefs.getString("addBuyDataCost",
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

        if (binding.activityAddBuyContentEdittextCost.text.length > 1 || binding.activityAddBuyContentEdittextExplain.text.length > 1) {

            builder.apply {
                setMessage("게시글을 임시 저장 하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addBuyData", "exist")
                    SharedData.prefs.setString("addBuyDataCost",
                        binding.activityAddBuyContentEdittextCost.text.toString())
                    SharedData.prefs.setString("addBuyDataExplain",
                        binding.activityAddBuyContentEdittextExplain.text.toString())
                    finish()
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    SharedData.prefs.setString("addBuyData", "none")
                    SharedData.prefs.setString("addBuyDataCost",
                        "")
                    SharedData.prefs.setString("addBuyDataExplain",
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

    fun contentUpload(uri : Uri){

        var contentBuyDTO = ContentBuyDTO()

        contentBuyDTO.userId = auth.currentUser?.email
        contentBuyDTO.uid = auth.currentUser?.uid
        contentBuyDTO.time = TimeUtil().getTime()
        contentBuyDTO.imageUrl = uri.toString()
        contentBuyDTO.timeStamp = System.currentTimeMillis()
        contentBuyDTO.explain = binding.activityAddBuyContentEdittextExplain.text.toString()
        contentBuyDTO.favoriteCount = 0
        contentBuyDTO.categoryHash = pickCategory
        contentBuyDTO.cost = binding.activityAddBuyContentEdittextCost.text.toString() + "원"
        contentBuyDTO.commentCount = 0
        contentBuyDTO.userNickName = userNickName
        //비교 전용 cost
        contentBuyDTO.costInt =   binding.activityAddBuyContentEdittextCost.text.toString()


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
        binding.activityAddBuyContentImageviewPhoto.setImageURI(photoUri)
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