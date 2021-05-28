package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallDetailProductBinding

class MallDetailProductActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallDetailProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_detail_product)
        binding.activitymalldetailproduct = this

        binding.activityMallDetailImagebuttonClose.setOnClickListener { finish() }

        binding.activityMallDetailProductButtonAddCart.setOnClickListener {  }
        binding.activityMallDetailProductBuy.setOnClickListener {  }
    }
}