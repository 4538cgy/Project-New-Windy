package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.app.AlertDialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailNormalViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.activity_comment.*

class ContentNormalRecyclerViewAdapter(
    private val context: Context,
    var fragmentManager: FragmentManager,
    dataList: ArrayList<ContentNormalDTO>,
    dataUidList: ArrayList<String>,
) : RecyclerView.Adapter<ContentNormalRecyclerViewAdapter.ContentNormalRecyclerViewAdapterViewHolder>() {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentNormalDTO: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentCommentSize: ArrayList<Int> = arrayListOf()
    var uid: String? = null
    var data = listOf<ContentNormalDTO>()
    var auth = FirebaseAuth.getInstance()

    init {

        uid = FirebaseAuth.getInstance().currentUser?.uid
        contentNormalDTO = dataList
        contentUidList = dataUidList
        data = contentNormalDTO


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContentNormalRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerNormalBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContentNormalRecyclerViewAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = contentNormalDTO.size

    override fun onBindViewHolder(
        holder: ContentNormalRecyclerViewAdapterViewHolder,
        position: Int,
    ) {
        holder.onBind(contentNormalDTO[position])

        /*
        holder.binding.itemRecyclerNormalAdview.loadAd(AdRequest.Builder().build())

        if(position%7 == 0){
            holder.binding.itemRecyclerNormalAdview.visibility =View.VISIBLE
        }else
        {
            holder.binding.itemRecyclerNormalAdview.visibility = View.GONE
        }

         */


        //뷰페이저 초기화
        holder.binding.itemRecyclerNormalViewpager.adapter = photoAdapter(data[position].imageDownLoadUrlList!!)

        //인디케이터 초기화
        holder.binding.activityPhotoDetailSlideViewIndicator.setViewPager(holder.binding.itemRecyclerNormalViewpager)

        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentNormalDTO[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop())
                        .into(holder.binding.itemRecyclerNormalImageviewProfile)
                }

            }

        //사진
        /*
        Glide.with(holder.itemView.context)
            .load(contentNormalDTO!![position].imageDownLoadUrlList?.get(0))
            .into(holder.binding.itemRecyclerNormalImageviewImage)

         */

        //좋아요 버튼 클릭
        holder.binding.itemDetailImagebuttonLike.setOnClickListener {

            if(auth.currentUser != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    favoriteEvent(position)
                }
            }else{
                var builder = AlertDialog.Builder(context)


                builder.apply {
                    setMessage("비회원은 좋아요를 누를 수 없습니다.")

                    setNegativeButton("닫기" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }

        }

        //댓글 버튼 클릭
        holder.binding.itemDetailImagebuttonComment.setOnClickListener {
            var intent = Intent(holder.itemView.context, CommentActivity::class.java)
            intent.apply {
                putExtra("contentUid", contentUidList[position])
                putExtra("destinationUid", contentNormalDTO[position].uid)
                putExtra("postType", "normal")
            }
            context.startActivity(intent)
        }

        //닉네임 클릭
        //유저 닉네임 클릭
        holder.binding.itemRecyclerNormalTextviewUsername.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid", contentNormalDTO[position].uid)
            bundle.putString("userId", contentNormalDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content, fragment)?.commit()
        }
        //아이템 자체 클릭
        holder.binding.itemRecyclerNormalConstAll.setOnClickListener {
            var intent = Intent(holder.itemView.context, DetailNormalViewActivity::class.java)
            intent.apply {
                putExtra("uid", contentNormalDTO[position].uid)
                putExtra("userId", contentNormalDTO[position].userId)
                putExtra("postUid", contentUidList[position])
                putExtra("imageList", contentNormalDTO[position].imageDownLoadUrlList)
                putExtra("contentTime", contentNormalDTO[position].time)
                putExtra("explain", contentNormalDTO[position].explain)
                putExtra("likeCount", contentNormalDTO[position].favoriteCount)
                putExtra("userNickName", contentNormalDTO[position].userNickName)
                putExtra("timeStamp", contentNormalDTO[position].timestamp)

                System.out.println("입력된 uid으아아아아앙아" + uid.toString())
                println("으아아아아앙" + contentUidList[position].toString())

            }
            context.startActivity(intent)
            viewCountIncrease(position)
        }

        //프로필 이미지 클릭
        holder.binding.itemRecyclerNormalImageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid", contentNormalDTO[position].uid)
            bundle.putString("userId", contentNormalDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content, fragment)?.commit()
        }

        //시간 표시
        holder.binding.itemRecyclerNormalTextviewTime.text = TimeUtil().formatTimeString(
            contentNormalDTO[position].timestamp!!)

        //옵션 버튼 클릭
        holder.binding.itemRecyclerNormalImagebuttonOption.setOnClickListener {


            if(auth.currentUser != null) {
                val bottomeSheetDialog: BottomSheetDialogContentOption =
                    BottomSheetDialogContentOption()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentNormalDTO[position].uid)
                bundle.putString("userId", contentNormalDTO[position].userId)
                bundle.putString("postUid", contentUidList[position])
                bundle.putString("uid", FirebaseAuth.getInstance().currentUser?.uid)
                bundle.putString("postType", "normal")
                bundle.putString("viewType", "fragment")
                bundle.putString("boardType","normal")
                bundle.putLong("contentUploadTime", contentNormalDTO[position].timestamp!!)
                bottomeSheetDialog.arguments = bundle
                bottomeSheetDialog.show(fragmentManager, "dd")
            }else{
                var builder = AlertDialog.Builder(context)


                builder.apply {
                    setMessage("비로그인 이용자는 이용할 수 없습니다. \n로그인 후 이용해주세요")

                    setNegativeButton("닫기" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }
        }




    }


    class ContentNormalRecyclerViewAdapterViewHolder(val binding: ItemRecyclerNormalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ContentNormalDTO) {
            binding.contentnormal = data
        }
    }

    fun copyToClipboard(context: Context, str: String) {
        /*
        val clipboardManager : ClipboardManager = context.getSystemService() as ClipboardManager

        val clipData = ClipData.newPlainText("strName",str)
        clipboardManager.primaryClip = clipData

         */
    }

    inner class photoAdapter(var photoList : ArrayList<String>) : RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content_photo,parent,false))


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //Glide.with(context).load(photoList[position]).thumbnail().centerInside().into(holder.imageUrl)

            Glide.with(context)
                .load(photoList[position])
                .placeholder(R.drawable.ic_sand_clock)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(
                    Glide.with(context).load(photoList[position]).fitCenter()
                )
                .into(holder.imageUrl)

            println("photoAdapter의 photoUri = " + photoList[position].toString() )
        }

        override fun getItemCount(): Int = photoList?.size!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageUrl : ImageView = view.findViewById(R.id.item_content_photo_imageview)
    }

    //조회수 증가
    fun viewCountIncrease(position: Int) {
        var tsDoc = firestore?.collection("contents")?.document("normal").collection("data")
            ?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->


            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentNormalDTO::class.java)


            if (contentDTO!!.viewers.containsKey(uid)) {

            } else {
                contentDTO?.viewCount = contentDTO?.viewCount!! + 1
                contentDTO.viewers[uid!!] = true
            }

            transaction.set(tsDoc, contentDTO)


        }.addOnFailureListener {
            println("viewCountIncreaseFail ${it.toString()}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(position: Int) {


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("contents")?.document("normal").collection("data")
            ?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->


            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentNormalDTO::class.java)

            println("트랙재애애애애액션 " + contentDTO.toString())
            if (contentDTO!!.favorites.containsKey(uid)) {
                //When the button is clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)

                System.out.println("uid 존재")
            } else {
                System.out.println("uid 미존재")
                //When the button is not clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(contentNormalDTO[position].uid!!,
                    contentNormalDTO[position].userNickName.toString())
            }
            transaction.set(tsDoc, contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid: String, userNickName: String) {

        System.out.println("좋아요 알람 이벤트")
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 0
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.localTimestamp = TimeUtil().getTime()
        alarmDTO.userNickName = userNickName
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = userNickName + (R.string.alarm_favorite)
        FcmPush.instance.sendMessage(destinationUid, "신바람 네트워크", message)
    }
}