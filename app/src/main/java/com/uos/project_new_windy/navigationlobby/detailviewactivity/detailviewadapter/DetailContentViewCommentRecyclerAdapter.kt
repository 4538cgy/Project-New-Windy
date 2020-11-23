package com.uos.project_new_windy.navigationlobby.detailviewactivity.detailviewadapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.model.ContentDTO
import kotlinx.android.synthetic.main.item_comment.view.*


//DetailView의 리사이클러뷰에서 아이템( 게시글 ) 을 누르면 새로운 뷰에서 게시글의 댓글을 불러와줄 어댑터
//불러올 액티비티의 context , collection , contentUid, profileImageUrl을 인자로 넘겨줘야한다.
class DetailContentViewCommentRecyclerAdapter(var activity: Activity,var collection : String,var contentUid: String,var profileImageUrl : Any) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var contentCommentList : ArrayList<ContentDTO.Comment> = arrayListOf()

    init {


        FirebaseFirestore.getInstance()
            .collection(collection)
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