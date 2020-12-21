package com.uos.project_new_windy.chat

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityChatRoomListBinding
import com.uos.project_new_windy.databinding.ItemChatRoomListBinding
import com.uos.project_new_windy.model.chatmodel.ChatDTO
import com.uos.project_new_windy.model.chatmodel.UserModel
import java.text.SimpleDateFormat
import java.util.*


class ChatRoomList : AppCompatActivity() {

    lateinit var binding : ActivityChatRoomListBinding
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room_list)
        binding.activitychatroomlist = this@ChatRoomList

        
        //뒤로가기
        binding.activityChatRoomListImagebuttonBack.setOnClickListener {
            finish()
        }
        binding.activityChatRoomListRecyclerChatList.addItemDecoration(DividerItemDecoration(binding.root.context,DividerItemDecoration.VERTICAL))
        binding.activityChatRoomListRecyclerChatList.adapter = ChatRoomListRecyclerAdapter()
        binding.activityChatRoomListRecyclerChatList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,
            false)




    }

    inner class ChatRoomListRecyclerAdapter : RecyclerView.Adapter<ChatRoomListRecyclerAdapter.ChatListViewHolder>(){

        var chat : ArrayList<ChatDTO> = arrayListOf()
        var destinationUsers : ArrayList<String> = arrayListOf()
        var uid : String ? = null

        init {

            uid = FirebaseAuth.getInstance().uid.toString()
            // 챗룸 데이터 가져오기

            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid")
                .equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        chat.clear()
                        for (item in dataSnapshot.children) {
                            chat.add(item.getValue(ChatDTO::class.java)!!)
                        }
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {


            val binding = ItemChatRoomListBinding.inflate(LayoutInflater.from(binding.root.context),
                parent,
                false)


            return ChatListViewHolder(binding)
        }
        inner class ChatListViewHolder(var binding: ItemChatRoomListBinding) : RecyclerView.ViewHolder(
            binding.root) {
            fun onBind(data: ChatDTO) {
                binding.listchatroom = data
            }


        }

        override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
            holder.onBind(chat[position])
            var destinationUid: String? = null



            // 일일 챗방에 있는 유저를 체크

            for (user in chat.get(position).users.keys) {
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            /*

            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userModel = dataSnapshot.getValue(UserModel::class.java)
                        Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)
                            .apply(RequestOptions().circleCrop())
                            .into(customViewHolder.imageView)
                        customViewHolder.textView_title.setText(userModel!!.userName)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })


             */


            //프로필 이미지
            FirebaseFirestore.getInstance()?.collection("profileImages")?.document(destinationUid!!)
                ?.get()?.addOnCompleteListener { task ->

                    if (task.isSuccessful)
                    {


                        FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(destinationUid!!).document("accountInfo")
                            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                                if (documentSnapshot != null){

                                    var userModel = documentSnapshot.toObject(UserModel::class.java)
                                    holder.binding.itemChatRoomListTextviewTitle.text = userModel?.userName


                                    System.out.println("userModel 은? " + userModel.toString())
                                    System.out.println("destinationUid는?" + destinationUid!!)

                                }
                            }





                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(holder.binding.itemChatRoomListCircleimageview)


                    }

                }

            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴

            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            val commentMap: MutableMap<String, ChatDTO.Comment> =
                TreeMap(Collections.reverseOrder())
            commentMap.putAll(chat[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.binding.itemChatRoomListTextviewLastMessage.setText(chat[position].comments.get(
                lastMessageKey)?.message)


            holder.itemView.setOnClickListener {
                val intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                var activityOptions: ActivityOptions? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityOptions = ActivityOptions.makeCustomAnimation(binding.root.context,
                        R.anim.fromright,
                        R.anim.toleft)
                    startActivity(intent, activityOptions.toBundle())
                }
            }





            //TimeStamp


            //TimeStamp
            holder.binding.itemChatRoomListTextviewTimestamp.text = chat[position].comments[lastMessageKey]?.timestamp.toString()


        }

        override fun getItemCount(): Int = chat.size

    }



}