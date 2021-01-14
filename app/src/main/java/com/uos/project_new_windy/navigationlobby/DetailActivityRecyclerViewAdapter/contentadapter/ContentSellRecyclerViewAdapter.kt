package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.chat.ChatActivity
import com.uos.project_new_windy.databinding.ItemRecyclerBuyBinding
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.AddContentActivity
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.ProgressDialogLoadingPost
import com.uos.project_new_windy.util.TimeUtil

class ContentSellRecyclerViewAdapter (private val context: Context,var fragmentManager: FragmentManager) : RecyclerView.Adapter<ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentSellDTO>()





    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid



        firestore?.collection("contents")?.document("sell").collection("data").orderBy("timeStamp",Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentSellDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        contentSellDTO.add(item!!)
                        System.out.println("데이터들2" + contentSellDTO.toString())
                        contentUidList.add(snapshot.id)
                    }








                }

                notifyDataSetChanged()
            }


        data = contentSellDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentSellRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerSellBinding.inflate(LayoutInflater.from(context),parent,false)


        return ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: ContentSellRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentSellDTO[position])

        //댓글 버튼 클릭
        holder.binding.itemRecyclerSellImagebuttonComment.setOnClickListener {
            var intent = Intent(holder.itemView.context,CommentActivity::class.java)
                intent.apply {
                    putExtra("contentUid",contentUidList[position])
                    putExtra("destinationUid",contentSellDTO[position].uid)
                    putExtra("postType","sell")
                }
            context.startActivity(intent)
        }

        //프로필 이미지 클릭
        holder.binding.itemRecyclerSellImageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentSellDTO[position].uid)
            bundle.putString("userId",contentSellDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }

        //holder.binding.itemRecyclerSellTextviewCost.text = contentSellDTO[position].costInt

        var won = contentSellDTO[position].costInt?.toLong()!! / 10000
        var last = contentSellDTO[position].costInt?.toLong()!! % 10000


        var cost : String ? = null
        if (won > 0){
            cost = won.toString() + "만"
            if (last > 0){
                cost = last.toString()
            }
        }else{
            cost = contentSellDTO[position].costInt.toString()
        }


        holder.binding.itemRecyclerSellTextviewCost.text = cost + "원"

        //좋아요 버튼 클릭
        holder.binding.itemRecyclerSellImagebuttonLike.setOnClickListener {
            favoriteEvent(position)
        }

        //유저 닉네임 클릭
        holder.binding.itemRecyclerSellTextviewUsername.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentSellDTO[position].uid)
            bundle.putString("userId",contentSellDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }

        //아이템 자체 클릭
        holder.binding.itemRecyclerSellConstAll.setOnClickListener {
            var intent = Intent(holder.itemView.context,DetailSellViewActivity::class.java)
            intent.apply {
                putExtra("uid" , contentSellDTO[position].uid)
                putExtra("userId",contentSellDTO[position].userId)
                putExtra("postUid",contentUidList[position])
                putExtra("cost",cost)
                putExtra("category",contentSellDTO[position].category)
                putExtra("imageList",contentSellDTO[position].imageDownLoadUrlList)
                putExtra("contentTime",contentSellDTO[position].time)
                putExtra("productExplain",contentSellDTO[position].productExplain)
                putExtra("explain",contentSellDTO[position].explain)
                putExtra("userNickName",contentSellDTO[position].userNickName)
                //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)
                System.out.println("입력된 uid으아아아아앙아" + uid.toString())

            }
            context.startActivity(intent)
        }

        
        //옵션 메뉴 클릭
        holder.binding.itemRecyclerSellImagebuttonOption.setOnClickListener {
            val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentSellDTO[position].uid)
            bundle.putString("userId",contentSellDTO[position].userId)
            bundle.putString("postExplain",contentSellDTO[position].productExplain)
            bundle.putString("postUid",contentUidList[position])
            bundle.putString("uid" ,FirebaseAuth.getInstance().currentUser?.uid)
            bundle.putString("postType", "sell")
            bundle.putString("viewType","fragment")
            bottomeSheetDialog.arguments = bundle
            bottomeSheetDialog.show(fragmentManager,"dd")
        }
        //댓글 갯수
        //viewholder.item_detail_textview_comment_count.text = contentDTOs!![position].commentCount.toString()
        holder.binding.itemRecyclerSellTextviewCommentCount.text = data[position].commentCount.toString()

        //프로필 이미지
        firestore?.collection("profileImages")?.document(data[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerSellImageviewProfile)
                }

            }

        //사진
        if (data[position].imageDownLoadUrlList?.isEmpty() == false) {
            Glide.with(holder.itemView.context)
                .load(data[position].imageDownLoadUrlList?.get(0))
                .into(holder.binding.itemRecyclerSellImageviewImage)
        }



    }

    override fun getItemCount(): Int = data.size



    class ContentSellRecyclerViewAdapterViewHolder(val binding: ItemRecyclerSellBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentSellDTO){
            binding.contentsell = data
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(position: Int){


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("contents")?.document("sell").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentSellDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)){
                //When the button is clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)

                System.out.println("uid 존재")
            }else{
                System.out.println("uid 미존재")
                //When the button is not clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(contentSellDTO[position].uid!!,contentUidList[position],contentSellDTO[position].explain.toString(),contentSellDTO[position].userNickName.toString())
            }
            transaction.set(tsDoc,contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid : String, postUid : String, postExplain : String, userNickName : String){

        System.out.println("좋아요 알람 이벤트")
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 0
        alarmDTO.postUid = postUid
        alarmDTO.postExplain = postExplain
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.localTimestamp = TimeUtil().getTime()
        alarmDTO.userNickName = userNickName
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = FirebaseAuth.getInstance()?.currentUser?.email + (R.string.alarm_favorite)
        FcmPush.instance.sendMessage(destinationUid,"신바람",message)
    }



}