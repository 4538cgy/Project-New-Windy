package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityOrderCompleteBinding

class OrderCompleteActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_complete)
        binding.activityordercomplete = this


        binding.activityOrderCompleteButtonBack.setOnClickListener { finish() }
        binding.activityOrderCompleteButtonContinue.setOnClickListener {  }
        binding.activityOrderCompleteOrderInfo.setOnClickListener {  }
    }
}