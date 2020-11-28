package com.uos.project_new_windy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
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

    }
}