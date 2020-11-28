package com.uos.project_new_windy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityChatRoomListBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.chatmodel.ChatDTO
import kotlinx.android.synthetic.main.item_image_list.view.*

class ChatRoomList : AppCompatActivity() {

    lateinit var binding : ActivityChatRoomListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat_room_list)
        binding.activitychatroomlist = this@ChatRoomList

        binding.activityChatRoomListRecyclerChatList.adapter = ChatRoomListRecyclerAdapter()
        binding.activityChatRoomListRecyclerChatList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)




    }

    inner class ChatRoomListRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var chat : List<ChatDTO> = arrayListOf()
        var destinationUsers : ArrayList<String> = arrayListOf()
        var uid : String ? = null

        init {

            uid = FirebaseAuth.getInstance().uid.toString()
            // 챗룸 데이터 가져오기

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room_list,parent,false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int = chat.size

    }



}