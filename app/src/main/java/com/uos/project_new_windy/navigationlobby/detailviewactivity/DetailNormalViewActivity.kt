package com.uos.project_new_windy.navigationlobby.detailviewactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityDetailNormalViewBinding

class DetailNormalViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailNormalViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_normal_view)
        binding.activitydetailviewnormal = this@DetailNormalViewActivity
    }
}