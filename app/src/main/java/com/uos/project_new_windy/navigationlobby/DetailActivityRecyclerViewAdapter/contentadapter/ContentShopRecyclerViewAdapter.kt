package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.LoginActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerShopBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentShopDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil

class ContentShopRecyclerViewAdapter (private val context: Context, var fragmentManager: FragmentManager, page : Int, dataList : ArrayList<ContentShopDTO>, dataUidList : ArrayList<String>) : RecyclerView.Adapter<ContentShopRecyclerViewAdapter.ContentShopRecyclerViewAdapterViewHolder>() {


    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentShopDTO: ArrayList<ContentShopDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentShopDTO>()
    var cost : String ? = null
    var lastVisible : Any ? = null
    var auth = FirebaseAuth.getInstance()

    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        contentShopDTO = dataList
        contentUidList = dataUidList
        data = contentShopDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentShopRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerShopBinding.inflate(LayoutInflater.from(context),parent,false)

        return ContentShopRecyclerViewAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(
        holder: ContentShopRecyclerViewAdapterViewHolder,
        position: Int
    ) {
        holder.onBind(contentShopDTO[position])

        /*
        holder.binding.itemRecyclerShopAdview.loadAd(AdRequest.Builder().build())

        if(position%7 == 0){
            holder.binding.itemRecyclerShopAdview.visibility =View.VISIBLE
        }else
        {
            holder.binding.itemRecyclerShopAdview.visibility = View.GONE
        }

         */
        //조회수 증가
        viewCountIncrease(position)
        //댓글 버튼 클릭
        holder.binding.itemRecyclerShopImagebuttonComment.setOnClickListener {
            var intent = Intent(holder.itemView.context, CommentActivity::class.java)
            intent.apply {
                putExtra("contentUid",contentUidList[position])
                putExtra("destinationUid",contentShopDTO[position].uid)
                putExtra("postType","sell")
            }
            context.startActivity(intent)
        }


        //뷰페이저 초기화
        holder.binding.itemRecyclerShopViewpager.adapter = photoAdapter(data[position].imageDownLoadUrlList!!,position)

        //인디케이터 초기화
        holder.binding.activityPhotoDetailSlideViewIndicator.setViewPager(holder.binding.itemRecyclerShopViewpager)



        //프로필 이미지 클릭
        holder.binding.itemRecyclerShopImageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentShopDTO[position].uid)
            bundle.putString("userId",contentShopDTO[position].userId)

            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }


        holder.binding.itemRecyclerShopTextviewCost.text = getCost(position) + "원"
        //좋아요 버튼 클릭
        holder.binding.itemRecyclerShopImagebuttonLike.setOnClickListener {


            if(auth.currentUser != null) {
                favoriteEvent(position)
            }else{
                var builder = AlertDialog.Builder(context)


                builder.apply {
                    setMessage("비회원은 좋아요를 누를 수 없습니다. \n 로그인 후 이용해주세요")

                    setNegativeButton("닫기" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }

        }
        //유저 닉네임 클릭
        holder.binding.itemRecyclerShopTextviewUsername.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentShopDTO[position].uid)
            bundle.putString("userId",contentShopDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }





        holder.binding.itemRecyclerShopConstAll.setOnClickListener {
            println("판매 게시판의 아이템 클릭 ")
            println("클릭전 가격" + cost.toString())
            var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
            intent.apply {
                putExtra("uid" , contentShopDTO[position].uid)
                putExtra("userId",contentShopDTO[position].userId)
                putExtra("postUid",contentUidList[position])
                putExtra("cost",getCost(position))
                putExtra("category",contentShopDTO[position].category)
                putExtra("imageList",contentShopDTO[position].imageDownLoadUrlList)
                putExtra("contentTime",contentShopDTO[position].time)
                putExtra("productExplain",contentShopDTO[position].productExplain)
                putExtra("explain",contentShopDTO[position].explain)
                putExtra("userNickName",contentShopDTO[position].userNickName)
                putExtra("timeStamp",contentShopDTO[position].timeStamp)
                //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)
                System.out.println("입력된 uid으아아아아앙아" + uid.toString())
            }
            println("판매 게시판의 아이템 클릭 ")
            context.startActivity(intent)

        }


        //옵션 메뉴 클릭
        holder.binding.itemRecyclerShopImagebuttonOption.setOnClickListener {

            if(auth.currentUser != null) {
                val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentShopDTO[position].uid)
                bundle.putString("userId",contentShopDTO[position].userId)
                bundle.putString("postExplain",contentShopDTO[position].productExplain)
                bundle.putString("postUid",contentUidList[position])
                bundle.putString("uid" ,FirebaseAuth.getInstance().currentUser?.uid)
                bundle.putString("postType", "shop")
                bundle.putString("viewType","fragment")
                bundle.putString("boardType","shop")
                bundle.putLong("contentUploadTime", contentShopDTO[position].timeStamp!!)
                bottomeSheetDialog.arguments = bundle
                bottomeSheetDialog.show(fragmentManager,"dd")
            }else{
                var builder = AlertDialog.Builder(context)


                builder.apply {
                    setMessage("비회원은 좋아요를 누를 수 없습니다. \n 로그인 후 이용해주세요")

                    setNegativeButton("닫기" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }
        }
        //댓글 갯수
        holder.binding.itemRecyclerShopTextviewCommentCount.text = data[position].commentCount.toString()

        //프로필 이미지
        firestore?.collection("profileImages")?.document(data[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerShopImageviewProfile)
                }

            }
        //전화걸기
        holder.binding.itemRecyclerShopImagebuttonPhone.setOnClickListener {

            contentShopDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("전화 걸기로 바로 이동됩니다. \n 이동하시겠습니까?")
                setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentShopDTO[position].uid.toString()).document("accountInfo")
                        .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                            if (documentSnapshot != null)
                            {
                                var phoneNumber = documentSnapshot.get("phoneNumber")?.toString()
                                holder.binding.root.context.startActivity(Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:"+phoneNumber)))
                            }
                        }
                })

                setTitle("안내")
                show()
            }
        }

        //시간 표시
        holder.binding.itemRecyclerShopTextviewTime.text = TimeUtil().formatTimeString(
            contentShopDTO[position].timeStamp!!)

        //문자 보내기
        holder.binding.itemRecyclerShopImagebuttonSms.setOnClickListener {

            contentShopDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("문자 보내기로 바로 이동됩니다. \n 이동하시겠습니까?")

                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentShopDTO[position].uid.toString()).document("accountInfo")
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
    }

    class ContentShopRecyclerViewAdapterViewHolder(val binding: ItemRecyclerShopBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentShopDTO){
            binding.contentshop = data
        }
    }


    //조회수 증가
    fun viewCountIncrease(position: Int){
        var tsDoc = firestore?.collection("contents")?.document("shop").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentShopDTO::class.java)

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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(position: Int){


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("contents")?.document("sell").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentShopDTO::class.java)

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
                favoriteAlarm(contentShopDTO[position].uid!!,contentUidList[position],contentShopDTO[position].explain.toString(),contentShopDTO[position].userNickName.toString())
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
        if(!contentShopDTO[position].costInt?.equals(0)!!) {
            var won = contentShopDTO[position].costInt?.toLong()!! / 10000
            var last = contentShopDTO[position].costInt?.toLong()!! % 10000

            if(won > 0){
                cost = won.toString() + "만"
                if (last >0){
                    cost += last.toString()
                }
            }else{
                cost = contentShopDTO[position].costInt.toString()
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
                cost = contentShopDTO[position].costInt.toString()
            }
        }
        return cost as String
    }

}