package com.uos.project_new_windy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.Model.ContentDTO
import com.uos.project_new_windy.Navigation_Lobby.CommentActivity
import com.uos.project_new_windy.Navigation_Lobby.UserFragment
import kotlinx.android.synthetic.main.activity_detail_content.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_image_list.*

class DetailContentActivity : AppCompatActivity() {

    var contentUid : String ?= null
    var destinationUid : String ? = null
    var firestore : FirebaseFirestore? = null
    var uid : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_content)

        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        activity_detail_content_recycler.adapter = DetailContentRecyclerViewAdapter()
        activity_detail_content_recycler.layoutManager = GridLayoutManager(this,3)
    }


    inner class DetailContentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()
        var contentCommentSize : ArrayList<Int> = arrayListOf()

        init {

            firestore?.collection("contents")?.document(contentUid!!)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()

                    if(querySnapshot == null)
                        return@addSnapshotListener

                    contentDTOs.add(querySnapshot.toObject(ContentDTO::class.java)!!)

                }
                    notifyDataSetChanged()
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewHolder = (holder as CustomViewHolder).itemView

            Glide.with(viewHolder.context).load(contentDTOs[position].imageDownLoadUrlList!![position]).apply(RequestOptions().centerCrop()).into(item_image_list_imageview)

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }









    }
}