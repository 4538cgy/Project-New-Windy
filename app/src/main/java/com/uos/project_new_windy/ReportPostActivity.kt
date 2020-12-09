package com.uos.project_new_windy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityReportBinding
import com.uos.project_new_windy.model.PostReportDTO
import com.uos.project_new_windy.util.TimeUtil

class ReportPostActivity : AppCompatActivity() , View.OnClickListener {

    lateinit var binding : ActivityReportBinding
    var uid : String ? = null
    var postUid : String ? = null
    var destinationUid : String ? = null
    var postExplain : String ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)
        binding.activityreport = this@ReportPostActivity

        var intent = intent
        postUid = intent.getStringExtra("postUid")
        destinationUid = intent.getStringExtra("destinationUid")
        postExplain = intent.getStringExtra("postExplain")


        binding.activityReportButton1.setOnClickListener {
            createReport("음란물")
        }

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            binding.activityReportButton1.id -> {
                //음란물
                createReport("음란물")
            }

            binding.activityReportButton2.id -> {
                //불법 홍보물
                createReport("불법홍보물")
            }

            binding.activityReportButton3.id -> {
                //사기
                createReport("사기")
            }

            binding.activityReportButton4.id -> {
                //판매금지 품목
                createReport("판매금지 품목")
            }

            binding.activityReportButton5.id -> {
                //거래 환불 분쟁
                createReport("거래 환불 분쟁")
            }

            binding.activityReportButton6.id -> {
                //비속어
                createReport("비속어")
            }

            binding.activityReportButton7.id -> {
                //기타
                createReport("기타")
            }


        }
    }

    fun createReport(title : String){

        var reportDTO  = PostReportDTO()
        reportDTO.destinationUid = destinationUid!!
        reportDTO.postExplain = postExplain!!
        reportDTO.serverTime = System.currentTimeMillis()
        reportDTO.title = title
        reportDTO.time = TimeUtil().getTime().toString()
        reportDTO.uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


        FirebaseFirestore.getInstance().collection("report").document("postReport").collection("report").document().set(reportDTO).addOnSuccessListener {
            Toast.makeText(this,"신고 완료",Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Log.d("신고 실패",it.toString())
        }
    }
}