package com.uos.project_new_windy.navigationlobby.detailviewactivity.detailviewadapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.model.ContentDTO
import kotlinx.android.synthetic.main.item_image_list.view.*

class DetailContentViewPhotoRecyclerAdapter(var activity:Activity,var contentUid:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()

    var contentImageList : ArrayList<String> = arrayListOf()

    init {

        FirebaseFirestore.getInstance()?.collection("contents")?.document(contentUid!!)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()

                if(querySnapshot == null)
                    return@addSnapshotListener

                //querySnapshot["imageDownLoadUrlList"]
                contentImageList = querySnapshot.get("imageDownLoadUrlList") as ArrayList<String>
                contentDTOs.add(querySnapshot.toObject(ContentDTO::class.java)!!)

            }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
        return CustomViewHolder(view)
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var viewHolder = (holder as CustomViewHolder).itemView


        contentDTOs[0].imageDownLoadUrlList?.forEach {
                i ->
            Log.d("이미지 리스트" , i)
        }
        //Glide.with(holder.itemView.context).load(contentDTOs[0].imageDownLoadUrlList!![position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)


        /*
        Log.d("이미지 리스트 확인", contentDTOs[position].imageDownLoadUrlList!![position])
        Log.d("이미지 리스트 크기 확인", contentDTOs[0].imageDownLoadUrlList?.size.toString())
        Log.d("새로운 이미지 리스트 확인" , contentImageList[position])
        Log.d("새로운 이미지 리스트 크기 확인" , contentImageList.size.toString())

         */
        Glide.with(holder.itemView.context).load(contentImageList[position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)
        viewHolder.item_image_list_imageview.setOnClickListener {
                i ->

            Log.d("클릭완료",position.toString())
        }


    }

    override fun getItemCount(): Int {
        return contentImageList.size
    }
}