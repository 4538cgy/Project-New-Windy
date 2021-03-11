package com.uos.project_new_windy.chat

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityChatBinding
import com.uos.project_new_windy.databinding.ItemChatBubbleBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.chatmodel.ChatDTO
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    var uid: String? = null
    var destinationUid: String? = null
    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    var chatRoomUid: String? = null
    var userNickName : String ? = null
    var chatMessageData : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.activitychat = this@ChatActivity





        //채팅을 요구하는 아이디 [ 단말기에 로그인 된 ]
        uid = FirebaseAuth.getInstance().currentUser?.uid









        //채팅을 당하는 아이디
        destinationUid = intent.getStringExtra("destinationUid")

        FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(destinationUid!!).document("accountInfo")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->


                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()
                    binding.activityChatTextviewUsernickname.text = userNickName
                }


            }



        //뒤로가기
        binding.activityChatImagebuttonBack.setOnClickListener {
            finish()
        }

        //챗 보내기
        binding.activityChatImagebuttonUpload.setOnClickListener {

            var chatDTOs = ChatDTO()


            chatDTOs.users.put(uid!!, true);
            chatDTOs.commentTimestamp = System.currentTimeMillis()
            chatDTOs.users.put(destinationUid!!, true)

            if (chatRoomUid == null) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(
                    chatDTOs).addOnSuccessListener {

                }.addOnSuccessListener {
                    checkChatRoom()
                }

            } else {
                var comment = ChatDTO.Comment()
                comment.uid = uid;
                comment.message = binding.activityChatEdittextExplain.text.toString()
                chatMessageData = binding.activityChatEdittextExplain.text.toString()
                comment.timestamp = TimeUtil().getTime()
                comment.serverTimestamp = ServerValue.TIMESTAMP
                FirebaseDatabase.getInstance().getReference().child("chatrooms")
                    .child(chatRoomUid!!).child(
                        "comments").push().setValue(comment).addOnCompleteListener {
                        binding.activityChatEdittextExplain.setText(" ")
                        chatAlarm(destinationUid!!)
                    }

            }

        }

        checkChatRoom()
    }

    fun chatAlarm(destinationUid : String){

        System.out.println("채팅 알람 이벤트")
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 3
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.localTimestamp = TimeUtil().getTime()
        alarmDTO.userNickName = userNickName
        alarmDTO.chatMessage = chatMessageData
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

       // var message = FirebaseAuth.getInstance()?.currentUser?.email + (R.string.alarm_favorite)
        var message = userNickName + ": " + chatMessageData

        System.out.println("푸쉬 알람이 도착하는 id 주소" + destinationUid.toString())
        FcmPush.instance.sendMessage(destinationUid!!,"신바람 네트워크:",message)
        //FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi2", "bye2")

    }

    fun checkChatRoom() {


        FirebaseDatabase.getInstance().getReference().child("chatrooms")
            .orderByChild("users/" + uid).equalTo(
                true).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {

                        var chatDTOs: ChatDTO = it.getValue(ChatDTO::class.java)!!

                        if (chatDTOs.users.containsKey(destinationUid)) {
                            chatRoomUid = it.key
                            binding.activityChatRecyclerBuble.layoutManager = LinearLayoutManager(
                                applicationContext,
                                LinearLayoutManager.VERTICAL,
                                false)
                            binding.activityChatRecyclerBuble.adapter = RecyclerViewAdapter()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    System.out.println("checkChatRoom 데이터 조회 실패")
                }


            })


    }

    inner class RecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        var comments: ArrayList<ChatDTO.Comment> = arrayListOf()
        var userModel = UserModel()


        init {
            /*
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid!!)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userModel = snapshot.getValue(UserModel::class.java)!!
                            getMessageList()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })

             */
            getMessageList()
        }

        fun getMessageList() {

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid!!)
                .child(
                    "comments").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()


                        snapshot.children.forEach {
                            comments.add(it.getValue(ChatDTO.Comment::class.java)!!)
                        }

                        notifyDataSetChanged()

                        binding.activityChatRecyclerBuble.scrollToPosition(comments.size - 1)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            //var view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_bubble,parent,false)
            val binding = ItemChatBubbleBinding.inflate(LayoutInflater.from(binding.root.context),
                parent,
                false)


            return MessageViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.onBind(comments[position])

            //내가 보낸 메세지
            if (comments[position].uid.equals(uid)) {
                holder.binding.messageItemTextViewMessage.text = comments[position].message
                holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.background_round_white)
                holder.binding.messageItemLinearlayoutDestination.visibility = View.INVISIBLE
                holder.binding.messageItemTextViewMessage.textSize = 18F

                holder.binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT

                //상대방이 보낸 메세지
            } else {
                /*
                Glide.with(holder.itemView.context)
                    .load(userModel.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.binding.messageItemImageviewProfile)

                 */

                FirebaseFirestore.getInstance()?.collection("profileImages")?.document(destinationUid!!)?.get()?.addOnCompleteListener {
                        task ->
                    if (task.isSuccessful)
                    {
                        var url = task.result!!["image"]
                        Glide.with(holder.binding.root.context).load(url).apply(RequestOptions().circleCrop()).into(holder.binding.messageItemImageviewProfile)

                    }
                }

                //유저 닉네임 가져오기
                FirebaseFirestore.getInstance()?.collection("userInfo")?.document("userData")?.collection(destinationUid!!)?.document("accountInfo")
                    ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                        if (documentSnapshot != null)
                        {
                            userNickName = documentSnapshot.get("userName")?.toString()

                            println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
                            //아이디 초기화
                            //binding.activityDetailNormalViewTextviewNickname.text = userNickName
                            holder.binding.messageItemTextviewName.text = userNickName
                        }

                    }


                holder.binding.messageItemLinearlayoutDestination.visibility = View.VISIBLE
                holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.background_round_gray)
                holder.binding.messageItemTextViewMessage.text = comments[position].message
                holder.binding.messageItemTextViewMessage.textSize = 18F
                holder.binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
            }
            /*
            var unixTime = comments[position].timestamp
            var data = Date(unixTime)

            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            var time = simpleDateFormat.format(date)

             */
            holder.binding.messageItemTextviewTimestamp.text = TimeUtil().getTime().toString()

        }

        override fun getItemCount(): Int {
            return comments.size
        }

        inner class MessageViewHolder(var binding: ItemChatBubbleBinding) : RecyclerView.ViewHolder(
            binding.root) {
            fun onBind(data: ChatDTO.Comment) {
                binding.itemchatbubble = data
            }


        }

    }


}