package com.uos.project_new_windy.navigationlobby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityCommentBinding
import com.uos.project_new_windy.databinding.ItemCommentBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity.CommentRecyclerViewAdapter.CommentRecyclerViewAdapterViewHolder
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentActivity : AppCompatActivity() {

    var contentUid : String ?= null
    var destinationUid : String ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null



    lateinit var binding : ActivityCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_comment)
        binding.commentactivity = this@CommentActivity

        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")
        var postType = intent.getStringExtra("postType")


        firestore = FirebaseFirestore.getInstance()


        /*
        activity_comment_recycler.adapter = CommentRecyclerViewAdapter()
        activity_comment_recycler.layoutManager = LinearLayoutManager(this)
         */
        binding.activityCommentRecycler.addItemDecoration(DividerItemDecoration(binding.root.context,DividerItemDecoration.VERTICAL))
        binding.activityCommentRecycler.adapter = CommentRecyclerViewAdapter(postType)
        binding.activityCommentRecycler.layoutManager = LinearLayoutManager(this)

        
        //뒤로가기
        binding.activityCommentImagebuttonBack.setOnClickListener { 
            finish()
        }
        
        
        //댓글 발송
        binding.activityCommentButtonUploadComment.setOnClickListener {

            if(binding.activityCommentEdittextExplain.text.length > 1) {

                var comment = ContentDTO.Comment()
                comment.userId = FirebaseAuth.getInstance().currentUser?.email
                comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                comment.comment = activity_comment_edittext_explain.text.toString()
                comment.timestamp = System.currentTimeMillis()
                comment.time = TimeUtil().getTime()

                commentSave(postType, comment)
                commentAlarm(destinationUid!!, activity_comment_edittext_explain.text.toString())
                binding.activityCommentEdittextExplain.setText("")
            }


        }


    }

    fun commentSave(postType: String, comment: Any) {
        FirebaseFirestore.getInstance().collection("contents").document(postType).collection("data").document(contentUid!!).collection("comments").document().set(comment)
            .addOnCompleteListener {
                    task ->
                System.out.println("댓글 작성 완료")
                if (task.isSuccessful) {

                    System.out.println("태스크 종료!!!!!!!!!!!!!!!!!!")
                    System.out.println("콘텐츠 아이디" + contentUid)
                    commentCountTest(postType)
                }
                System.out.println("댓글 카운트 증가 완료")
            }
    }

    fun commentAlarm(destinationUid : String, message : String){

        firestore?.collection("userInfo")?.document("userData")?.collection(FirebaseAuth.getInstance().currentUser?.uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {

                    var userNickName = documentSnapshot.get("userName")?.toString()

                    println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
                    //아이디 초기화


                    var alarmDTO = AlarmDTO()
                    alarmDTO.destinationUid = destinationUid
                    alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
                    alarmDTO.kind = 1
                    alarmDTO.userNickName = userNickName
                    alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
                    alarmDTO.timestamp = System.currentTimeMillis()
                    alarmDTO.localTimestamp = TimeUtil().getTime()
                    alarmDTO.message = message
                    FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

                    var msg = userNickName + " " + getString(R.string.alarm_comment) + " of " + message
                    FcmPush.instance.sendMessage(destinationUid,"신바람 네트워크", msg)
                }

            }
        /*
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.kind = 1
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.localTimestamp = TimeUtil().getTime()
        alarmDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
        FcmPush.instance.sendMessage(destinationUid,"Howlstagram", msg)

         */
    }

    fun commentCountTest(postType: String){
        
        System.out.println("commentCountTest 시작")
        //val tsDoc = firestore?.collection("contents")?.document(contentUid!!)
        val tsDoc = firestore?.collection("contents")?.document(postType)?.collection("data")?.document(contentUid!!)

        firestore?.runTransaction {
            transition ->

            val snapshot = transition.get(tsDoc!!).toObject(ContentDTO::class.java)

            val newComment =  snapshot?.commentCount!! + 1


            transition.update(tsDoc,"commentCount",newComment)

        }?.addOnCompleteListener {
          System.out.println("트랙잰션 업데이트 성공 !!!!!!!!!!!!!!!!")
        }?.addOnFailureListener {
            i ->
            System.out.println("트랜잭션 업데이트 실패 : " + i.toString())
        }

    }


    fun commentCountAdd(){
        var tsDoc = firestore?.collection("contents")?.document(contentUid!!)

        firestore?.runTransaction {
                transaction ->

            System.out.println("댓글 카운트 증가 시작2")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            contentDTO?.commentCount!! + 1


            transaction.set(tsDoc,contentDTO)

        }?.addOnCompleteListener { System.out.println("트랜잭션 정상 실행 완료") }
    }

    inner class CommentRecyclerViewAdapter(postType: String) : RecyclerView.Adapter<CommentRecyclerViewAdapterViewHolder>(){

        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
        var data = listOf<ContentDTO.Comment>()



        init {

            FirebaseFirestore.getInstance()
                .collection("contents")
                .document(postType)
                .collection("data")
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
            data = comments




        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentRecyclerViewAdapterViewHolder {
            /*
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)

             */
            val binding = ItemCommentBinding.inflate(LayoutInflater.from(this@CommentActivity),parent,false)
            return CommentRecyclerViewAdapterViewHolder(binding)
        }


       // private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: CommentRecyclerViewAdapterViewHolder, position: Int) {

            //var view = holder.itemView
            holder.onBind(comments[position])
            comments.forEach {
                    it ->

                System.out.println("댓글 데이터어어어어어엌" + it.toString())
            }

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(holder.binding.itemCommentCircleImageview)

                    }
                }

            holder.binding.itemCommentTextviewTime.text = TimeUtil().formatTimeString(comments[position].timestamp!!)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class CommentRecyclerViewAdapterViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data : ContentDTO.Comment){
                binding.commentitem = data
            }
        }


    }
}