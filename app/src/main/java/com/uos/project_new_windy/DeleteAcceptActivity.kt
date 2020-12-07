package com.uos.project_new_windy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityDeleteAcceptBinding

class DeleteAcceptActivity : AppCompatActivity() {

    lateinit var binding : ActivityDeleteAcceptBinding
    var postUid : String ? = null
    var postType : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delete_accept)
        binding.activitydeleteaccept = this@DeleteAcceptActivity

        postUid = intent.getStringExtra("postUid")
        postType = intent.getStringExtra("postType")
        
        //아니오 버튼
        binding.activityDeleteAcceptButtonNo.setOnClickListener { 
            finish()
        }
        
        
        //예 버튼
        binding.activityDeleteAcceptButtonYes.setOnClickListener {

            //삭제 시도
            FirebaseFirestore.getInstance().collection("contents").document(postType!!).collection("data").document(postUid!!)
                .delete()
                .addOnFailureListener {
                    //실패
                    System.out.println("삭제 실패")
                }.addOnSuccessListener {
                    //성공
                    System.out.println("삭제 성공")
                    finish()
                }
        }
    }
}