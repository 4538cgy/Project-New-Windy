package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPostSellSearchCategorySetBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity

class PostSellSearchCategorySetActivity : AppCompatActivity() {

    lateinit var binding : ActivityPostSellSearchCategorySetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_post_sell_search_category_set)
        binding.activitypostsellsearchcategoryset = this@PostSellSearchCategorySetActivity

        binding.activityPostSellSearchCategorySetRecycler.adapter = PostSellSearchCategoryRecyclerViewAdapter()
        binding.activityPostSellSearchCategorySetRecycler.layoutManager = GridLayoutManager(this,3)
    }


    inner class PostSellSearchCategoryRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {


            firestore?.collection("contents")
                ?.document("sell")
                ?.collection("data")
                ?.whereEqualTo("uid",uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->


                    if(querySnapshot == null) return@addSnapshotListener

                    //Get data
                    for(snapshot in querySnapshot.documents){
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                        contentUidList.add(snapshot.id)

                    }

                    System.out.println("내가 올린 게시글의 갯수" + contentDTOs.size)
                    //fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                    binding.accountTvPostCount.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var imageView = (holder as CustomViewHolder).imageView

            if(contentDTOs[position].imageDownLoadUrlList?.size!! > 0) {
                Glide.with(holder.itemView.context)
                    .load(contentDTOs[position].imageDownLoadUrlList?.get(0))
                    .apply(RequestOptions().centerCrop()).into(imageView)
            }
            // System.out.println("이미지의 url" + contentDTOs[position].imageDownLoadUrlList)
            // Glide.with(holder.itemView.context).load(contentImageList[position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)


            imageView.setOnClickListener {
                    i ->
                var intent = Intent(i.context, DetailSellViewActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("commentCount", contentDTOs!![position].commentCount.toString())
                intent.putExtra("likeCount",contentDTOs!![position].favoriteCount.toString())
                intent.putExtra("contentTime",contentDTOs!![position].time)

                startActivity(intent)

            }
        }


    }

}