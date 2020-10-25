package com.uos.project_new_windy.Navigation_Lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.DetailContentActivity
import com.uos.project_new_windy.Model.AlarmDTO
import com.uos.project_new_windy.Model.ContentDTO
import com.uos.project_new_windy.R
import com.uos.project_new_windy.Util.FcmPush
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {

    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.fragment_detail_recycler.adapter = DetailViewRecyclerViewAdapter()
        view.fragment_detail_recycler.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()
        var contentCommentSize : ArrayList<Int> = arrayListOf()

        init {
            firestore?.collection("contents")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    if (querySnapshot == null)
                        return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)







                    }

                    notifyDataSetChanged()
                }






        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewholder = (holder as CustomViewHolder).itemView

            //프로필 이미지
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->

                    if (task.isSuccessful)
                    {
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(viewholder.item_detail_circleImageview_profile)
                    }

                }

            //유저 아이디 [ 닉네임으로 교체 요망 ]
            viewholder.item_detail_textview_user_name.text = contentDTOs!![position].userId

            viewholder.setOnClickListener {

                i ->


                var intent = Intent(i.context, DetailContentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("commentCount", contentDTOs!![position].commentCount.toString())
                intent.putExtra("likeCount",contentDTOs!![position].favoriteCount.toString())
                intent.putExtra("contentTime",contentDTOs!![position].time)

                startActivity(intent)

            }

            //리스트의 첫번째 사진
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageDownLoadUrlList?.get(0))
                .into(viewholder.item_detail_imageview_content)


            //제목
            viewholder.item_detail_textview_title.text = contentDTOs!![position].title

            //내용
            viewholder.item_detail_textview_explain_content.text = contentDTOs!![position].explain

            //좋아요 갯수
            viewholder.item_detail_textview_like_count.text =
                "Likes" + contentDTOs!![position].favoriteCount


            //시간
            viewholder.item_detail_textview_time.text = contentDTOs!![position].time


            //댓글 갯수
            viewholder.item_detail_textview_comment_count.text = contentDTOs!![position].commentCount.toString()



            //좋아요 버튼 이벤트 처리
            viewholder.item_detail_imagebutton_like.setOnClickListener {
                favoriteEvent(position)
            }


            //this code is when the page is loaded
            if (contentDTOs!![position].favorites.containsKey(uid)) {
                //this is like status
                viewholder.item_detail_imagebutton_like.setImageResource(R.drawable.ic_account)

            } else {
                //this is unlike status
                viewholder.item_detail_imagebutton_like.setImageResource(R.drawable.ic_favorite_border)
            }

            //프로필 이미지 클릭시
            viewholder.item_detail_circleImageview_profile.setOnClickListener {

                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_content, fragment)?.commit()

            }

            viewholder.item_detail_imagebutton_comment.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)
            }

        }





        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("contents")?.document(contentUidList[position])
            firestore?.runTransaction {

                transition ->

                var contentDTO = transition.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid))
                {
                    //when the button is clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else{
                    //when the button is not clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true

                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transition.set(tsDoc,contentDTO)

            }
        }

        fun favoriteAlarm(destinationUid : String){

        }

    }
}