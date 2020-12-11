package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityDeleteAcceptBinding
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailNormalViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity

class DeleteAcceptActivity : AppCompatActivity() {

    lateinit var binding : ActivityDeleteAcceptBinding
    var postUid : String ? = null
    var postType : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_accept)
        binding.activitydeleteaccept = this@DeleteAcceptActivity

        postUid = intent.getStringExtra("postUid")
        postType = intent.getStringExtra("postType")
        
        //아니오 버튼
        binding.activityDeleteAcceptButtonNo.setOnClickListener {
            var activity = DetailSellViewActivity.activity
            activity?.finishAffinity()

            System.out.println(" 액티비티는 ? " + DetailSellViewActivity.activity.toString())

        }
        
        
        //예 버튼
        binding.activityDeleteAcceptButtonYes.setOnClickListener {
            
            
            System.out.println("이전 액티비티 종료")
            var activity = DetailSellViewActivity.activity
            activity?.finishAffinity()
            System.out.println("으아아아아아" + activity?.isFinishing)

            //삭제 시도
            if(activity?.isFinishing == true) {
                FirebaseFirestore.getInstance().collection("contents").document(postType!!)
                    .collection("data").document(
                    postUid!!
                )
                    .delete()
                    .addOnFailureListener {
                        //실패
                        System.out.println("삭제 실패")
                    }.addOnSuccessListener {
                        //성공
                        System.out.println("삭제 성공")
                        /*
                        var intent = Intent(this, LobbyActivity::class.java)

                        //startActivity(Intent(this, LobbyActivity::class.java))
                        startActivity(intent)
                        finish()

                         */


                    }
                var intent = Intent( this, LobbyActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
    }


}