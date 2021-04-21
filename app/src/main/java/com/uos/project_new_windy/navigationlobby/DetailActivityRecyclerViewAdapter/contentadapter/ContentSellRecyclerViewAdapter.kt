package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
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
import com.uos.project_new_windy.setting.SettingActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.ProgressDialogLoading
import com.uos.project_new_windy.util.ProgressDialogLoadingPost
import com.uos.project_new_windy.util.TimeUtil

class ContentSellRecyclerViewAdapter (private val context: Context,var fragmentManager: FragmentManager,page : Int,dataList : ArrayList<ContentSellDTO> , dataUidList : ArrayList<String>) : RecyclerView.Adapter<ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentSellDTO>()
    var cost : String ? = null
    var lastVisible : Any ? = null
    var auth = FirebaseAuth.getInstance()
    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        contentSellDTO = dataList
        contentUidList = dataUidList
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

        //holder.binding.itemRecyclerSellAdview.loadAd(AdRequest.Builder().build())


        /*
        if (position % 7 == 0) {
            holder.binding.itemRecyclerSellAdview.visibility = View.VISIBLE
        } else
        {
            holder.binding.itemRecyclerSellAdview.visibility = View.GONE
        }

         */

        //조회수 증가
        viewCountIncrease(position)
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


        //뷰페이저 초기화
        holder.binding.itemRecyclerSellViewpager.adapter = photoAdapter(data[position].imageDownLoadUrlList!!,position)

        //인디케이터 초기화
        holder.binding.activityPhotoDetailSlideViewIndicator.setViewPager(holder.binding.itemRecyclerSellViewpager)



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


        holder.binding.itemRecyclerSellTextviewCost.text = getCost(position) + "원"
        //좋아요 버튼 클릭
        holder.binding.itemRecyclerSellImagebuttonLike.setOnClickListener {

            if(auth.currentUser != null) {
                favoriteEvent(position)
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





        holder.binding.itemRecyclerSellConstAll.setOnClickListener {

            println("판매 게시판의 아이템 클릭 ")
            println("클릭전 가격" + cost.toString())
            var intent = Intent(holder.itemView.context,DetailSellViewActivity::class.java)
            intent.apply {
                putExtra("uid" , contentSellDTO[position].uid)
                putExtra("userId",contentSellDTO[position].userId)
                putExtra("postUid",contentUidList[position])
                putExtra("cost",getCost(position))
                putExtra("category",contentSellDTO[position].category)
                putExtra("imageList",contentSellDTO[position].imageDownLoadUrlList)
                putExtra("contentTime",contentSellDTO[position].time)
                putExtra("productExplain",contentSellDTO[position].productExplain)
                putExtra("explain",contentSellDTO[position].explain)
                putExtra("userNickName",contentSellDTO[position].userNickName)
                putExtra("timeStamp",contentSellDTO[position].timeStamp)
                //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)
                System.out.println("입력된 uid으아아아아앙아" + uid.toString())
            }
            println("판매 게시판의 아이템 클릭 ")
            context.startActivity(intent)

        }


        //옵션 메뉴 클릭
        holder.binding.itemRecyclerSellImagebuttonOption.setOnClickListener {

            if(auth.currentUser != null) {
                val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentSellDTO[position].uid)
                bundle.putString("userId",contentSellDTO[position].userId)
                bundle.putString("postExplain",contentSellDTO[position].productExplain)
                bundle.putString("postUid",contentUidList[position])
                bundle.putString("uid" ,FirebaseAuth.getInstance().currentUser?.uid)
                bundle.putString("postType", "sell")
                bundle.putString("viewType","fragment")
                bundle.putString("boardType","sell")
                bundle.putLong("contentUploadTime", contentSellDTO[position].timeStamp!!)
                bottomeSheetDialog.arguments = bundle
                bottomeSheetDialog.show(fragmentManager,"dd")
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
        //댓글 갯수
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
        //전화걸기
        holder.binding.itemRecyclerSellImagebuttonPhone.setOnClickListener {

            contentSellDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("전화 걸기로 바로 이동됩니다. \n 이동하시겠습니까?")
                setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentSellDTO[position].uid.toString()).document("accountInfo")
                        .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                            if (documentSnapshot != null)
                            {
                                var phoneNumber = documentSnapshot.get("phoneNumber")?.toString()
                                holder.binding.root.context.startActivity(Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber)))
                            }
                        }
                })

                setTitle("안내")
                show()
            }
        }

        //시간 표시
        holder.binding.itemRecyclerSellTextviewTime.text = TimeUtil().formatTimeString(
            contentSellDTO[position].timeStamp!!)

        //문자 보내기
        holder.binding.itemRecyclerSellImagebuttonSms.setOnClickListener {

            contentSellDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("문자 보내기로 바로 이동됩니다. \n 이동하시겠습니까?")

                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentSellDTO[position].uid.toString()).document("accountInfo")
                        .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                            if (documentSnapshot != null)
                            {
                                var phoneNumber = documentSnapshot.get("phoneNumber")?.toString()

                                var smsUri = Uri.parse("sms:"+phoneNumber)
                                var intent = Intent(Intent.ACTION_SENDTO, smsUri)
                                intent.apply {
                                    putExtra("sms_body","신바람 빌리지를 통해 발송된 메세지입니다.")
                                }
                                holder.binding.root.context.startActivity(intent)
                                /*
                                holder.binding.root.context.startActivity(Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:"+phoneNumber)))

                                 */
                            }
                        }
                }

                )

                setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })

                setTitle("안내")
                show()
            }

        }

        //사진
        /*
        if (data[position].imageDownLoadUrlList?.isEmpty() == false) {
            Glide.with(holder.itemView.context)
                .load(data[position].imageDownLoadUrlList?.get(0))
                .into(holder.binding.itemRecyclerSellImageviewImage)
        }

         */

    }

    override fun getItemCount(): Int = data.size

    class ContentSellRecyclerViewAdapterViewHolder(val binding: ItemRecyclerSellBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentSellDTO){
            binding.contentsell = data
        }
    }

    //조회수 증가
    fun viewCountIncrease(position: Int){
        var tsDoc = firestore?.collection("contents")?.document("sell").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentSellDTO::class.java)


            if(contentDTO!!.viewers.containsKey(uid)){

            }else{
                contentDTO?.viewCount = contentDTO?.viewCount!! + 1
                contentDTO.viewers[uid!!] = true
            }

            transaction.set(tsDoc,contentDTO)


        }.addOnFailureListener {
            println("viewCountIncreaseFail ${it.toString()}")
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

        firestore?.collection("userInfo")?.document("userData")?.collection(FirebaseAuth.getInstance().currentUser?.uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    var userNickName = documentSnapshot.get("userName")?.toString()

                    //아이디 초기화


                    var alarmDTO = AlarmDTO()
                    alarmDTO.destinationUid = destinationUid
                    alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
                    alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
                    alarmDTO.kind = 0
                    alarmDTO.userNickName =userNickName
                    alarmDTO.postUid = postUid
                    alarmDTO.postExplain = postExplain
                    alarmDTO.timestamp = System.currentTimeMillis()
                    FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

                    var message = userNickName + (R.string.alarm_favorite)
                    FcmPush.instance.sendMessage(destinationUid,"신바람 네트워크",message)
                }

            }


    }

    fun getCost(position: Int) : String{
        cost = ""
        if(!contentSellDTO[position].costInt?.equals(0)!!) {
            var won = contentSellDTO[position].costInt?.toLong()!! / 10000
            var last = contentSellDTO[position].costInt?.toLong()!! % 10000

            if(won > 0){
                cost = won.toString() + "만"
                if (last >0){
                    cost += last.toString()
                }
            }else{
                cost = contentSellDTO[position].costInt.toString()
            }
        }else{
            var won = 0
            var last = 0

            if (won > 0){
                cost = won.toString() + "만"
                if (last > 0){
                    cost = last.toString()
                }
            }else{
                cost = contentSellDTO[position].costInt.toString()
            }
        }
        return cost as String
    }

    inner class photoAdapter(var photoList : ArrayList<String>, itemPosition : Int) : RecyclerView.Adapter<ViewHolder>(){

        var itemPosition = itemPosition

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content_photo,parent,false))


        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                .load(photoList[position])
                .placeholder(R.drawable.ic_sand_clock)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(
                    Glide.with(context).load(photoList[position]).fitCenter()
                )
                .into(holder.imageUrl)
            
            holder.itemView.setOnClickListener { 
                println("으아아아아아아아아")
                favoriteEvent(itemPosition)
            }

            println("photoAdapter의 photoUri = " + photoList[position].toString() )
        }

        override fun getItemCount(): Int = photoList?.size!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageUrl : ImageView = view.findViewById(R.id.item_content_photo_imageview)
    }



}