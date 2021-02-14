package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerMembershipBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentMemberShipDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil

class ContentMemberShipRecyclerViewAdapter (val context: Context, var fragmentManager: FragmentManager, page : Int, dataList : ArrayList<ContentMemberShipDTO>, dataUidList : ArrayList<String>) : RecyclerView.Adapter<ContentMemberShipRecyclerViewAdapter.ContentMemberShipRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentMemberShipDTO: ArrayList<ContentMemberShipDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentMemberShipDTO>()
    var cost : String ? = null
    var lastVisible : Any ? = null
    init {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        contentMemberShipDTO = dataList
        contentUidList = dataUidList
        data = contentMemberShipDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentMemberShipRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerMembershipBinding.inflate(LayoutInflater.from(context),parent,false)

        return ContentMemberShipRecyclerViewAdapter.ContentMemberShipRecyclerViewAdapterViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: ContentMemberShipRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentMemberShipDTO[position])

        //댓글 버튼 클릭
        holder.binding.itemRecyclerMembershipImagebuttonComment.setOnClickListener {
            var intent = Intent(holder.itemView.context, CommentActivity::class.java)
            intent.apply {
                putExtra("contentUid",contentUidList[position])
                putExtra("destinationUid",contentMemberShipDTO[position].uid)
                putExtra("postType","membership")
            }
            context.startActivity(intent)
        }




        //프로필 이미지 클릭
        holder.binding.itemRecyclerMembershipImageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentMemberShipDTO[position].uid)
            bundle.putString("userId",contentMemberShipDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }



        holder.binding.itemRecyclerMembershipTextviewCost.text = getCost(position) + "원"
        //좋아요 버튼 클릭
        holder.binding.itemRecyclerMembershipImagebuttonLike.setOnClickListener {

            favoriteEvent(position)
        }
        //유저 닉네임 클릭
        holder.binding.itemRecyclerSellTextviewUsername.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentMemberShipDTO[position].uid)
            bundle.putString("userId",contentMemberShipDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }





        holder.binding.itemRecyclerMembershipConstAll.setOnClickListener {
            println("판매 게시판의 아이템 클릭 ")
            println("클릭전 가격" + cost.toString())
            var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
            intent.apply {
                putExtra("uid" , contentMemberShipDTO[position].uid)
                putExtra("userId",contentMemberShipDTO[position].userId)
                putExtra("postUid",contentUidList[position])
                putExtra("cost",getCost(position))
                putExtra("category",contentMemberShipDTO[position].category)
                putExtra("imageList",contentMemberShipDTO[position].imageDownLoadUrlList)
                putExtra("contentTime",contentMemberShipDTO[position].time)
                putExtra("productExplain",contentMemberShipDTO[position].productExplain)
                putExtra("explain",contentMemberShipDTO[position].explain)
                putExtra("userNickName",contentMemberShipDTO[position].userNickName)
                putExtra("timeStamp",contentMemberShipDTO[position].timeStamp)
                //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)
                System.out.println("입력된 uid으아아아아앙아" + uid.toString())
            }
            println("판매 게시판의 아이템 클릭 ")
            context.startActivity(intent)
            viewCountIncrease(position)
        }


        //옵션 메뉴 클릭
        holder.binding.itemRecyclerMembershipImagebuttonOption.setOnClickListener {
            val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentMemberShipDTO[position].uid)
            bundle.putString("userId",contentMemberShipDTO[position].userId)
            bundle.putString("postExplain",contentMemberShipDTO[position].productExplain)
            bundle.putString("postUid",contentUidList[position])
            bundle.putString("uid" ,FirebaseAuth.getInstance().currentUser?.uid)
            bundle.putString("postType", "membership")
            bundle.putString("viewType","fragment")
            bottomeSheetDialog.arguments = bundle
            bottomeSheetDialog.show(fragmentManager,"dd")
        }
        //댓글 갯수
        holder.binding.itemRecyclerMembershipTextviewCommentCount.text = data[position].commentCount.toString()

        //프로필 이미지
        firestore?.collection("profileImages")?.document(data[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerMembershipImageviewProfile)
                }

            }
        //전화걸기
        holder.binding.itemRecyclerMembershipImagebuttonPhone.setOnClickListener {

            contentMemberShipDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("전화 걸기로 바로 이동됩니다. \n 이동하시겠습니까?")
                setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentMemberShipDTO[position].uid.toString()).document("accountInfo")
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
        holder.binding.itemRecyclerMembershipTextviewTime.text = TimeUtil().formatTimeString(
            contentMemberShipDTO[position].timeStamp!!)

        //문자 보내기
        holder.binding.itemRecyclerMembershipImagebuttonSms.setOnClickListener {

            contentMemberShipDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("문자 보내기로 바로 이동됩니다. \n 이동하시겠습니까?")

                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentMemberShipDTO[position].uid.toString()).document("accountInfo")
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
        if (data[position].imageDownLoadUrlList?.isEmpty() == false) {
            Glide.with(holder.itemView.context)
                .load(data[position].imageDownLoadUrlList?.get(0))
                .into(holder.binding.itemRecyclerMembershipImageviewImage)
        }

    }

    override fun getItemCount(): Int = data.size

    class ContentMemberShipRecyclerViewAdapterViewHolder(val binding: ItemRecyclerMembershipBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentMemberShipDTO){
            binding.contentmembership = data
        }
    }

    //조회수 증가
    fun viewCountIncrease(position: Int){
        var tsDoc = firestore?.collection("contents")?.document("membership").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentMemberShipDTO::class.java)


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
        var tsDoc = firestore?.collection("contents")?.document("membership").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentMemberShipDTO::class.java)

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
                favoriteAlarm(contentMemberShipDTO[position].uid!!,contentUidList[position],contentMemberShipDTO[position].explain.toString(),contentMemberShipDTO[position].userNickName.toString())
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
        if(!contentMemberShipDTO[position].costInt?.equals(0)!!) {
            var won = contentMemberShipDTO[position].costInt?.toLong()!! / 10000
            var last = contentMemberShipDTO[position].costInt?.toLong()!! % 10000

            if(won > 0){
                cost = won.toString() + "만"
                if (last >0){
                    cost += last.toString()
                }
            }else{
                cost = contentMemberShipDTO[position].costInt.toString()
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
                cost = contentMemberShipDTO[position].costInt.toString()
            }
        }
        return cost as String
    }
}