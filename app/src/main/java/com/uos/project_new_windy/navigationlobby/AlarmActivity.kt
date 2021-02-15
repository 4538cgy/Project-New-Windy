package com.uos.project_new_windy.navigationlobby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAlarmBinding
import com.uos.project_new_windy.model.AlarmDTO
import kotlinx.android.synthetic.main.item_comment.view.*

class AlarmActivity : AppCompatActivity() {

    lateinit var binding : ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_alarm)
        binding.alarm = this@AlarmActivity

        binding.activityAlarmRecycler.adapter = AlarmRecyclerViewAdapter()
        binding.activityAlarmRecycler.addItemDecoration(
            DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
        binding.activityAlarmRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid).orderBy("timestamp",
                Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    alarmDTOList.clear()
                    if (querySnapshot == null)
                        return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                    }
                    notifyDataSetChanged()

                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView

            FirebaseFirestore.getInstance().collection("profileImages")
                .document(alarmDTOList[position].uid!!).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]
                        Glide.with(view.context).load(url).apply(RequestOptions().circleCrop())
                            .into(view.item_comment_circleImageview)
                    }
                }

            when (alarmDTOList[position].kind) {

                0 -> {
                    val str_0 = alarmDTOList[position].userNickName + getString(R.string.alarm_favorite)
                    view.item_comment_textview_profile.text = str_0
                    view.item_comment_textview_time.text = alarmDTOList[position].localTimestamp
                }

                1 -> {
                    val str_0 =
                        alarmDTOList[position].userNickName + " " + getString(R.string.alarm_comment) + " of " + alarmDTOList[position].message
                    view.item_comment_textview_profile.text = str_0
                    view.item_comment_textview_time.text = alarmDTOList[position].localTimestamp
                }

                2 -> {
                    val str_0 =
                        alarmDTOList[position].userNickName + " " + getString(R.string.alarm_follow)
                    view.item_comment_textview_profile.text = str_0
                    view.item_comment_textview_time.text = alarmDTOList[position].localTimestamp
                }
                3 -> {
                    var str_0 =
                        alarmDTOList[position].userNickName + " " + getString(R.string.alarm_chat)
                    view.item_comment_textview_profile.text = str_0
                    view.item_comment_textview_time.text = alarmDTOList[position].localTimestamp
                }
                4 -> {
                    var str_0 =
                        alarmDTOList[position].userNickName + " " + getString(R.string.alarm_follower_update)
                    view.item_comment_textview_profile.text = str_0
                    view.item_comment_textview_time.text = alarmDTOList[position].localTimestamp
                }
            }

            view.item_comment_textview_comment.visibility = View.INVISIBLE
        }

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

    }
}