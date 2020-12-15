package com.uos.project_new_windy.navigationlobby.detailviewactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPhotoDetailViewBinding

class PhotoDetailViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityPhotoDetailViewBinding

    var url : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_detail_view)
        binding.activityphotodetailview = this@PhotoDetailViewActivity

        url = intent.getStringExtra("photoUrl")

        Glide.with(this).load(url).apply(RequestOptions()).into(binding.activityPhotoDetailViewImageviewPhoto)


    }
}