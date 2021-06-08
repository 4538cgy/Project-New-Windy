package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallAdminBinding

class MallAdminActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_admin)
        binding.activitymalladmin = this

        binding.activityMallAdminImagebuttonClose.setOnClickListener {
            finish()
        }
        binding.activityMallAdminButtonOrderList.setOnClickListener {

        }
    }
}