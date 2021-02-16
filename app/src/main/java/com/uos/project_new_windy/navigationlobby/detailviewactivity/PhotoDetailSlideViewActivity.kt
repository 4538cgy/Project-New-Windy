package com.uos.project_new_windy.navigationlobby.detailviewactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPhotoDetailSlideViewBinding
import kotlinx.android.synthetic.main.item_photo.view.*
import me.relex.circleindicator.CircleIndicator
import me.relex.circleindicator.CircleIndicator3

class PhotoDetailSlideViewActivity : AppCompatActivity() {

    lateinit var binding:ActivityPhotoDetailSlideViewBinding
    var photoList : ArrayList<String> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_detail_slide_view)
        binding.activityphotodetailslideview = this@PhotoDetailSlideViewActivity

        photoList = intent.getStringArrayListExtra("photoList")

        //뷰페이저 초기화
        binding.activityPhotoDetailSlideViewViewpager2.adapter = photoAdapter()

        //인디케이터 초기화
        binding.activityPhotoDetailSlideViewIndicator.setViewPager(binding.activityPhotoDetailSlideViewViewpager2)


    }

    inner class photoAdapter() : RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo,parent,false))


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(binding.root.context).load(photoList!![position]).into(holder.imageUrl)

           // holder.itemView.item_photo_indicator.setViewPager(binding.activityPhotoDetailSlideViewViewpager2)
//            var indicator = findViewById<CircleIndicator3>(R.id.item_photo_indicator)
//            indicator.setViewPager(binding.activityPhotoDetailSlideViewViewpager2)
        }

        override fun getItemCount(): Int = photoList?.size!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageUrl : PhotoView = view.findViewById(R.id.item_photo_photoview)
    }

}