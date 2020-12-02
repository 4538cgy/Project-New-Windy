package com.uos.project_new_windy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity() , View.OnClickListener {

    lateinit var binding : ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)
        binding.activityreport = this@ReportActivity


    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            binding.activityReportButton1.id -> {
                //음란물
            }

            binding.activityReportButton2.id -> {
                //불법 홍보물
            }

            binding.activityReportButton3.id -> {
                //사기
            }

            binding.activityReportButton4.id -> {
                //판매금지 품목
            }

            binding.activityReportButton5.id -> {
                //거래 환불 분쟁
            }

            binding.activityReportButton6.id -> {
                //비속어
            }

            binding.activityReportButton7.id -> {
                //기타
            }


        }
    }
}