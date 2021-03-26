package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityMySubscribeListBinding
import com.uos.project_new_windy.databinding.ActivityMySubscriberListBinding
import com.uos.project_new_windy.databinding.ItemSubscribeListBinding
import com.uos.project_new_windy.model.FollowDTO
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.navigationlobby.UserFragment
import kotlinx.android.synthetic.main.activity_setting.view.*

class MySubscribeListActivity : AppCompatActivity() {

    lateinit var binding : ActivityMySubscribeListBinding

    var userDataList : ArrayList<UserModel> = arrayListOf()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_subscribe_list)

        binding.activityMySubscribeListImagebuttonClose.setOnClickListener {
            finish()
        }

        db.collection("userInfo").document("userData").collection(intent.getStringExtra("uid")).document("follow")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null){
                    if (documentSnapshot.exists()){
                        val followDTO = documentSnapshot.toObject(FollowDTO::class.java)

                        userDataList.clear()
                        println("구독자" + followDTO?.followers?.keys)
                        println("구독한자" + followDTO?.followings?.keys)

                        followDTO?.followings?.keys?.forEach {

                            db.collection("userInfo").document("userData").collection(it).document("accountInfo")
                                .get().addOnSuccessListener {
                                    if (it != null){
                                        if (it.exists())
                                        {
                                            userDataList.add(it.toObject(UserModel::class.java)!!)

                                            binding.activityMySubscribeListRecycler.adapter = MySubscribeRecyclerAdapter()
                                            binding.activityMySubscribeListRecycler.layoutManager = LinearLayoutManager(binding.root.context,
                                                LinearLayoutManager.VERTICAL,false)
                                            //(binding.activityMySubscribeListRecycler.adapter as MySubscriberListActivity.MySubscribeRecyclerAdapter).notifyDataSetChanged()

                                        }
                                    }
                                }
                        }
                    }
                }
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

            //언팔로우
            holder.binding.itemSubscribeListButtonUnfollow.setOnClickListener {

                //내꺼 날려주고
                var tsDocFollowing = db?.collection("userInfo")?.document("userData")?.collection(FirebaseAuth.getInstance().currentUser?.uid!!)?.document("follow")
                db?.runTransaction{

                        transaction ->
                    var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)



                        //its remove following third person when a third person follow me
                        followDTO?.followingCount = followDTO?.followingCount!! - 1
                        followDTO?.followings?.remove(userDataList[position].uid!!)



                    transaction.set(tsDocFollowing, followDTO!!)
                    return@runTransaction

                }
                //상대꺼 날려주고
                var tsDocFollower = db?.collection("userInfo")?.document("userData")?.collection(userDataList[position].uid!!)?.document("follow")
                db?.runTransaction {
                        transaction ->
                    var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)


                        //It cancel my follower when I follow a third person
                        followDTO!!.followerCount = followDTO!!.followerCount - 1
                        followDTO!!.followers.remove(FirebaseAuth.getInstance().currentUser?.uid!!)


                    transaction.set(tsDocFollower,followDTO!!)
                    return@runTransaction
                }

                //userDataList.removeAt(position)
            }

            //아이템 클릭시
            holder.itemView.setOnClickListener {
                println("꾸깤꾸까ㅓㅋ")
                println("으애애애애앵")
                var intent = Intent()
                intent.apply {
                    putExtra("uid" , userDataList[position].uid)
                }

                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }

        inner class UserListViewHolder(val binding : ItemSubscribeListBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data : UserModel){
                binding.itemsubscribelist = data
            }
        }

    }
}