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
import com.google.firebase.database.*
import com.google.firebase.database.core.utilities.Tree
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityChatRoomListBinding
import com.uos.project_new_windy.databinding.ItemChatRoomListBinding
import com.uos.project_new_windy.model.chatmodel.ChatDTO
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.navigationlobby.AlarmActivity
import com.uos.project_new_windy.navigationlobby.AlarmFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

        binding.activityChatRoomListImagebuttonAlarm.setOnClickListener {

           startActivity(Intent(this,AlarmActivity::class.java))

        }




    }

    inner class ChatRoomListRecyclerAdapter : RecyclerView.Adapter<ChatRoomListRecyclerAdapter.ChatListViewHolder>(){

        var chat : ArrayList<ChatDTO> = arrayListOf()
        var destinationUsers : ArrayList<String> = arrayListOf()
        var chatTimestampList : ArrayList<String> = arrayListOf()
        var resultChat : ArrayList<ChatDTO> = arrayListOf()
        var uid : String ? = null


        init {

            uid = FirebaseAuth.getInstance().uid.toString()
            // 챗룸 데이터 가져오기
            println("데이터 가져오기")
            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid")
                .equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        chat.clear()
                        for (item in dataSnapshot.children) {
                            chat.add(item.getValue(ChatDTO::class.java)!!)
                        }



                        notifyDataSetChanged()


                        println("데이터 정렬1")
                        chat.forEachIndexed{
                                index, chatDTO ->
                            println("chat 의 값 = $chatDTO" )
                            val commentMap: MutableMap<String, ChatDTO.Comment> =
                                TreeMap(Collections.reverseOrder())
                            commentMap.putAll(chat[index].comments)
                            val lastMessageKey = commentMap.keys.toTypedArray()[0]
                            val timeStamp = commentMap[lastMessageKey]?.serverTimestamp
                            chatTimestampList.add(timeStamp.toString())

                            chatTimestampList.forEach {
                                println("chatTimestampList 의 값 = $it")
                            }
                        }

                        println("데이터 정렬2")

                        chatTimestampList.sortDescending()


                        chatTimestampList.forEach {
                            println("chatTimestampList 의 값 = $it")
                        }

                        println("데이터 정렬3")
                        chatTimestampList.forEachIndexed { index,it ->

                            chat.forEachIndexed { chatindex, chatDTO ->
                                val commentMap: MutableMap<String, ChatDTO.Comment> =
                                    TreeMap(Collections.reverseOrder())
                                commentMap.putAll(chatDTO.comments)
                                val lastMessageKey = commentMap.keys.toTypedArray()[0]
                                if (it.equals(commentMap[lastMessageKey]?.serverTimestamp.toString())){
                                    println("데이터 추가함" + commentMap[lastMessageKey]?.serverTimestamp.toString() + " 으앜 "  + it.toString())
                                    resultChat.add(index,chatDTO)
                                }else{
                                    println("데이터 추가안함"+ commentMap[lastMessageKey]?.serverTimestamp.toString() + " 으앜 "  + it.toString())
                                    return@forEachIndexed
                                }
                            }
                        }

                        println("데이터 정렬4")
                        resultChat.forEachIndexed {index,it ->

                            val commentMap: MutableMap<String, ChatDTO.Comment> =
                                TreeMap(Collections.reverseOrder())
                            commentMap.putAll(it.comments)
                            val lastMessageKey = commentMap.keys.toTypedArray()[0]
                            println("최종값 ${commentMap[lastMessageKey]?.serverTimestamp.toString()}")
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })



            /*
            val commentMap: MutableMap<String, ChatDTO.Comment> =
                TreeMap(Collections.reverseOrder())
            commentMap.putAll(chat[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.binding.itemChatRoomListTextviewLastMessage.setText(chat[position].comments.get(
                lastMessageKey)?.message)

            holder.binding.itemChatRoomListTextviewTimestamp.text = chat[position].comments[lastMessageKey]?.timestamp.toString()


             */
            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴


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
            //holder.onBind(chat[position])
            holder.onBind(resultChat[position])
            var destinationUid: String? = null



            // 일일 챗방에 있는 유저를 체크

            for (user in resultChat.get(position).users.keys) {
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }



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

                                    /*
                                    System.out.println("userModel 은? " + userModel.toString())
                                    System.out.println("destinationUid는?" + destinationUid!!)

                                     */

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
            commentMap.putAll(resultChat[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.binding.itemChatRoomListTextviewLastMessage.setText(resultChat[position].comments.get(
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
            holder.binding.itemChatRoomListTextviewTimestamp.text = resultChat[position].comments[lastMessageKey]?.timestamp.toString()


        }

        override fun getItemCount(): Int = resultChat.size

    }



}