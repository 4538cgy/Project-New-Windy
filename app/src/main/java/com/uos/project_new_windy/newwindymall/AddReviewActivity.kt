package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddReviewBinding

class AddReviewActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_review)
        binding.activityaddreview = this


        binding.activityAddReviewImagebuttonClose.setOnClickListener { finish() }

    }
}