package com.uos.project_new_windy.navigationlobby.detailviewactivity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPhotoDetailViewBinding
import kotlinx.android.synthetic.main.activity_photo_detail_view.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoDetailViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityPhotoDetailViewBinding

    var url : String ? = null
    var photoList : ArrayList<String> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_detail_view)
        binding.activityphotodetailview = this@PhotoDetailViewActivity

        url = intent.getStringExtra("photoUrl")


        Glide.with(this).load(url).apply(RequestOptions()).into(binding.activityPhotoDetailViewImageviewPhoto)



    }

}