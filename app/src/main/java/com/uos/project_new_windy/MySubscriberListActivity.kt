package com.uos.project_new_windy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityMySubscriberListBinding
import com.uos.project_new_windy.databinding.ItemSubscribeListBinding
import com.uos.project_new_windy.model.FollowDTO
import com.uos.project_new_windy.model.chatmodel.UserModel

class MySubscriberListActivity : AppCompatActivity() {
    
    lateinit var binding : ActivityMySubscriberListBinding
    var userDataList : ArrayList<UserModel> = arrayListOf()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_subscriber_list)



        db.collection("userInfo").document("userData").collection(auth.currentUser!!.uid).document("follow")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null){
                    if (documentSnapshot.exists()){
                        val followDTO = documentSnapshot.toObject(FollowDTO::class.java)

                        println("구독자" + followDTO?.followers?.keys)
                        println("구독한자" + followDTO?.followings?.keys)

                        followDTO?.followers?.keys?.forEach {

                            db.collection("userInfo").document("userData").collection(it).document("accountInfo")
                                .get().addOnSuccessListener {
                                    if (it != null){
                                        if (it.exists())
                                        {
                                            userDataList.add(it.toObject(UserModel::class.java)!!)

                                            binding.activityMySubscriberListRecycler.adapter = MySubscribeRecyclerAdapter()
                                             binding.activityMySubscriberListRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
                                            (binding.activityMySubscriberListRecycler.adapter as MySubscribeRecyclerAdapter).notifyDataSetChanged()

                                        }
                                    }
                                }
                        }
                    }
                }
            }

        //끄기 버튼
        binding.activityMySubscriberListImagebuttonClose.setOnClickListener { 
            finish()
        }


    }


    inner class MySubscribeRecyclerAdapter : RecyclerView.Adapter<MySubscribeRecyclerAdapter.UserListViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
            val binding = ItemSubscribeListBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return UserListViewHolder(binding)
        }

        override fun getItemCount(): Int = userDataList.size

        override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {

            holder.onBind(userDataList[position])
            db?.collection("profileImages")?.document(userDataList[position].uid!!)?.addSnapshotListener{

                    documentSnapshot, firebaseFirestoreException ->


                if (documentSnapshot?.data != null){
                    var url = documentSnapshot?.data!!["image"]

                    Glide.with(binding.root.context)
                        .load(url)
                        .apply(
                            RequestOptions().centerCrop().circleCrop()
                        )
                        .error(R.drawable.btn_signin_google)
                        .into(holder.binding.itemSubscribeListImageviewProfile)

                }
            }

            holder.binding.itemSubscribeListTextviewTitle.text = userDataList[position].userName

        }

        inner class UserListViewHolder(val binding : ItemSubscribeListBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data : UserModel){
                 binding.itemsubscribelist = data
            }
        }

    }
}