package com.uos.project_new_windy

import android.content.Intent
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
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityFollowListBinding
import com.uos.project_new_windy.databinding.ItemCommentBinding
import com.uos.project_new_windy.databinding.ItemFollowListBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.FollowDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.PhotoDetailViewActivity
import kotlinx.android.synthetic.main.item_image_list.view.*

class FollowListActivity : AppCompatActivity() {

    lateinit var binding : ActivityFollowListBinding
    var firestore = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    var followlist : ArrayList<FollowDTO> = arrayListOf()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_follow_list)
        binding.activityfollowlist = this@FollowListActivity

        binding.activityFollowListImagebuttonBack.setOnClickListener {
            finish()
        }

        binding.activityFollowListRecycler.adapter = FollowListRecyclerViewAdapter()
        binding.activityFollowListRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)


    }

    inner class FollowListRecyclerViewAdapter() : RecyclerView.Adapter<FollowListRecyclerViewAdapter.FollowListRecyclerViewAdapterViewHolder>(){

        var followList : ArrayList<FollowDTO> = arrayListOf()

        init {

            firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid.toString()).document("follow").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if(documentSnapshot == null)
                    return@addSnapshotListener

                //querySnapshot["imageDownLoadUrlList"]
                if (documentSnapshot.exists()) {


                    followlist.add(documentSnapshot.toObject(FollowDTO::class.java)!!)

                    followlist.forEach {
                        println("팔로우 리스트 목록" + it.toString())
                    }
                }
            }
            notifyDataSetChanged()



        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListRecyclerViewAdapterViewHolder {
            /*
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)

             */
            val binding = ItemFollowListBinding.inflate(LayoutInflater.from(this@FollowListActivity),parent,false)
            return FollowListRecyclerViewAdapterViewHolder(binding)
        }


        // private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: FollowListRecyclerViewAdapterViewHolder, position: Int) {

            //var view = holder.itemView
            holder.onBind(followList[position])

            /*
            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(holder.binding.itemFollowListImageviewProfile)

                    }
                }

             */
        }

        override fun getItemCount(): Int {
            return followList.size
        }

        inner class FollowListRecyclerViewAdapterViewHolder(val binding: ItemFollowListBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data : FollowDTO){
                binding.itemfollowlist = data
            }
        }


    }



}