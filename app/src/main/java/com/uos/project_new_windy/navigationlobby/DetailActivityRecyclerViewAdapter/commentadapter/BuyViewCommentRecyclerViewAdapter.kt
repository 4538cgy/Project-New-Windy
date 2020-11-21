package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.commentadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.navigationlobby.DetailViewFragment
import kotlinx.android.synthetic.main.item_comment.view.*

class BuyViewCommentRecyclerViewAdapter(var fragment : DetailViewFragment,var contentUid : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var contentCommentList : ArrayList<ContentDTO.Comment> = arrayListOf()
    var profileImageUrl : String ? = null

    init {


        FirebaseFirestore.getInstance()
            .collection("contents")
            .document(contentUid!!)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentCommentList.clear()
                if (querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot.documents!!){
                    contentCommentList.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                }
                notifyDataSetChanged()

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
        return CustomViewHolder(view)
    }

    private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var view  = holder.itemView

        view.item_comment_textview_comment.text = contentCommentList[position].comment
        view.item_comment_textview_profile.text = contentCommentList[position].userId
        view.item_comment_textview_time.text = contentCommentList[position].time.toString()

        Glide.with(holder.itemView.context).load(profileImageUrl).apply(RequestOptions().circleCrop()).into(view.item_comment_circleImageview)

    }

    override fun getItemCount(): Int {
        return contentCommentList.size
    }

}