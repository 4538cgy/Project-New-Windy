package com.uos.project_new_windy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.model.ContentDTO
import kotlinx.android.synthetic.main.activity_detail_content.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_image_list.view.*

class DetailContentActivity : AppCompatActivity() {

    var contentUid : String ?= null
    var destinationUid : String ? = null
    var commentCount : String ? = null
    var likeCount : String ? = null
    var firestore : FirebaseFirestore? = null
    var uid : String ? = null
    var profileImageUrl : Any ? = null
    var contentTime : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_content)

        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")
        commentCount = intent.getStringExtra("commentCount")
        likeCount = intent.getStringExtra("likeCount")
        contentTime = intent.getStringExtra("contentTime")


        firestore = FirebaseFirestore.getInstance()

        //이미지 리사이클러뷰 초기화
        activity_detail_content_recycler.adapter = DetailContentRecyclerViewAdapter()
        activity_detail_content_recycler.layoutManager = GridLayoutManager(this,3)

        //아이디 초기화
        activity_detail_content_textview_id.text = FirebaseAuth.getInstance().currentUser?.email


        //댓글 리사이클러뷰 초기화
        activity_detail_content_recycler_comment.adapter = DetailContentCommentRecyclerViewAdapter()
        activity_detail_content_recycler_comment.layoutManager = LinearLayoutManager(this)

        //각종 버튼 초기화
        activity_detail_content_textview_comment_count.text = commentCount
        activity_detail_content_textview_favorite_count.text = likeCount

        //시간 초기화
        activity_detail_content_textview_time.text = contentTime

        Log.d("도착 확인" , contentUid + " and " + destinationUid )
        Log.d("uid 확인" , FirebaseAuth.getInstance().currentUser?.uid!!)


        
        //프로필 이미지
        firestore?.collection("profileImages")?.document(FirebaseAuth.getInstance().currentUser?.uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {

                    profileImageUrl = task.result!!["image"]
                    Glide.with(this)
                        .load(profileImageUrl)
                        .apply(RequestOptions().circleCrop()).into(activity_detail_content_circleimageview)
                    Log.d("glide 이미지 넣기" , "완료")
                }

            }
    }
    
    
    //댓글 리사이클러뷰 어댑터
    inner class DetailContentCommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentCommentList : ArrayList<ContentDTO.Comment> = arrayListOf()


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

    //사진 리사이클러뷰 어댑터
    inner class DetailContentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        var contentImageList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("contents")?.document(contentUid!!)
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
}