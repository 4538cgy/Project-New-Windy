package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallCartBinding

class MallCartActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_cart)
        binding.activitymallcart = this

        binding.activityMallCartImagebuttonClose.setOnClickListener { finish() }
        binding.activityMallCartButtonOrder.setOnClickListener {  }
    }
}