package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.os.Build
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
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerBuyBinding
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.util.FcmPush

class ContentSellRecyclerViewAdapter (private val context: Context,var fragmentManager: FragmentManager) : RecyclerView.Adapter<ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentSellDTO>()

    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentSellDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    contentSellDTO.add(item!!)
                    contentUidList.add(snapshot.id)







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
            System.out.println("어어어엌ㅋㅋ")
        }

        //프로필 이미지 클릭
        holder.binding.itemRecyclerSellImageviewProfile.setOnClickListener {

        }


        //좋아요 버튼 클릭
        holder.binding.itemRecyclerSellImagebuttonLike.setOnClickListener {
            favoriteEvent(position)
        }

        
        //옵션 메뉴 클릭
        holder.binding.itemRecyclerSellImagebuttonOption.setOnClickListener {
            val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
            bottomeSheetDialog.show(fragmentManager,"dd")
        }

        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentSellDTO[position].uid!!)
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
        Glide.with(holder.itemView.context).load(contentSellDTO!![position].imageDownLoadUrlList?.get(0))
            .into(holder.binding.itemRecyclerSellImageviewImage)
    }

    override fun getItemCount(): Int = contentSellDTO.size



    class ContentSellRecyclerViewAdapterViewHolder(val binding: ItemRecyclerSellBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentSellDTO){
            binding.contentsell = data
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(position: Int){


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->




            var contentDTO = transaction.get(tsDoc!!).toObject(ContentSellDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)){
                //When the button is clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)


            }else{
                //When the button is not clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(contentSellDTO[position].uid!!)
            }
            transaction.set(tsDoc,contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid : String){

        System.out.println("좋아요 알람 이벤트")
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 0
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = FirebaseAuth.getInstance()?.currentUser?.email + (R.string.alarm_favorite)
        FcmPush.instance.sendMessage(destinationUid,"HowlInstgram",message)
    }

}