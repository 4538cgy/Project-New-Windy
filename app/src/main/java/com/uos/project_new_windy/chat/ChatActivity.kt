package com.uos.project_new_windy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityChatBinding
import com.uos.project_new_windy.model.chatmodel.ChatDTO
import java.text.SimpleDateFormat

class ChatActivity : AppCompatActivity() {

    lateinit var binding : ActivityChatBinding
    var uid : String ? = null
    var destinationUid : String ? = null
    var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    var chatRoomUid : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.activitychat = this@ChatActivity

        
        //채팅을 요구하는 아이디 [ 단말기에 로그인 된 ]
        uid = FirebaseAuth.getInstance().currentUser?.uid
        //채팅을 당하는 아이디
        destinationUid = intent.getStringExtra("destinationUid")

        binding.activityChatImagebuttonUpload.setOnClickListener {

            var chatDTOs  = ChatDTO()


            chatDTOs.users.put(uid!!,true);
            chatDTOs.users.put(destinationUid!!,true)

            if (chatRoomUid == null){


            }

        }
    }

    fun checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var chatDTOs : ChatDTO = snapshot.getValue(ChatDTO::class.java)!!

                if (chatDTOs.users.containsKey(destinationUid))
                {
                    chatRoomUid = snapshot.key
                    binding.activityChatRecyclerBuble.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
                    binding.activityChatRecyclerBuble.adapter = RecyclerViewAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("checkChatRoom 데이터 조회 실패")
            }


        })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var comments : List<ChatDTO.Comment> = arrayListOf()



        init {
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

    }
}