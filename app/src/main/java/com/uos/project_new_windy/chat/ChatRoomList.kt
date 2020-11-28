package com.uos.project_new_windy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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


    }

    inner class ChatRoomListRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var comments : List<ChatDTO.Comment> = arrayListOf()

        init {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int = comments.size

    }



}