package com.uos.project_new_windy.Navigation_Lobby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.Model.ContentDTO
import com.uos.project_new_windy.R
import com.uos.project_new_windy.Util.TimeUtil
import kotlinx.android.synthetic.main.activity_add_content.view.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*
import java.sql.DatabaseMetaData
import java.text.SimpleDateFormat

class CommentActivity : AppCompatActivity() {

    var contentUid : String ?= null
    var destinationUid : String ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        activity_comment_recycler.adapter = CommentRecyclerViewAdapter()
        activity_comment_recycler.layoutManager = LinearLayoutManager(this)

        activity_comment_button_upload_comment.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = activity_comment_edittext_explain.text.toString()
            comment.timestamp = System.currentTimeMillis()
            comment.time = TimeUtil().getTime()



            FirebaseFirestore.getInstance().collection("contents").document(contentUid!!).collection("comments").document().set(comment)
                .addOnCompleteListener {
                    task ->
                    System.out.println("댓글 작성 완료")
                    if (task.isSuccessful) {
                        commentCountAdd()
                    }
                    System.out.println("댓글 카운트 증가 완료")


                }
            System.out.println("댓글 카운트 증가 시작1")
            var tsDoc = firestore?.collection("contents")?.document(contentUid!!)

            firestore?.runTransaction {
                    transaction ->

                System.out.println("댓글 카운트 증가 시작2")
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                contentDTO?.commentCount!! + 1


                transaction.set(tsDoc,contentDTO)
            }

        }

    }

    fun commentCountAdd(){


    }



    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()



        init {
            FirebaseFirestore.getInstance()
                .collection("contents")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    comments.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
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

            view.item_comment_textview_comment.text = comments[position].comment
            view.item_comment_textview_profile.text = comments[position].userId

            view.item_comment_textview_time.text = comments[position].time.toString()

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.item_comment_circleImageview)
                    }
                }
        }

        override fun getItemCount(): Int {
            return comments.size
        }




    }
}