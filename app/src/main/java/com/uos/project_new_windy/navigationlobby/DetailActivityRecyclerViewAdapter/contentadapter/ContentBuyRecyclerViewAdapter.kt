package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.ItemRecyclerBuyBinding
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailBuyViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil

class ContentBuyRecyclerViewAdapter(private val context: Context,var fragmentManager: FragmentManager) : RecyclerView.Adapter<ContentBuyRecyclerViewAdapter.ContentBuyRecyclerViewAdapterViewHolder>() {

    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var contentBuyDTO: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var uid : String ? = null
    var data = listOf<ContentBuyDTO>()
    var won : Long = 0
    var last : Long = 0
    init {
        Log.d("디테일!" , "교체완료됬습니다.")

        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("contents")?.document("buy").collection("data").orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentBuyDTO.clear()
                contentUidList.clear()
                
                
                System.out.println("구매하기 데이터 가져오기 성공")
                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {

                    var item = snapshot.toObject(ContentBuyDTO::class.java)

                    System.out.println("데이터들 " + item.toString())
                    contentBuyDTO.add(item!!)
                    System.out.println("데이터들2" + contentBuyDTO.toString())
                    contentUidList.add(snapshot.id)







                }

                notifyDataSetChanged()
            }
        data = contentBuyDTO
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentBuyRecyclerViewAdapterViewHolder {
        val binding = ItemRecyclerBuyBinding.inflate(LayoutInflater.from(context),parent,false)
        return ContentBuyRecyclerViewAdapter.ContentBuyRecyclerViewAdapterViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: ContentBuyRecyclerViewAdapterViewHolder, position: Int) {
        holder.onBind(contentBuyDTO[position])



        //좋아요 버튼 클릭
        holder.binding.itemRecyclerBuyImagebuttonLike.setOnClickListener {
            favoriteEvent(position)
        }
        //프로필 이미지
        firestore?.collection("profileImages")?.document(contentBuyDTO[position].uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.binding.itemRecyclerBuyImageviewProfile)
                }

            }

        //댓글 버튼 클릭
        holder.binding.itemRecyclerBuyImagebuttonComment.setOnClickListener {
            var intent = Intent(holder.itemView.context, CommentActivity::class.java)
            intent.apply {
                putExtra("contentUid",contentUidList[position])
                putExtra("destinationUid",contentBuyDTO[position].uid)
                putExtra("postType","buy")
            }
            context.startActivity(intent)
        }

        //프로필 이미지 클릭
        holder.binding.itemRecyclerBuyImageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentBuyDTO[position].uid)
            bundle.putString("userId",contentBuyDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }
        if(contentBuyDTO[position].costInt != "") {
            won = contentBuyDTO[position].costInt?.toLong()!! / 10000
            last = contentBuyDTO[position].costInt?.toLong()!! % 10000
        }else{
            won = 0
            last = 0
        }


        var cost : String ? = null
        if (won > 0){
            cost = won.toString() + "만"
            if (last > 0){
                cost = last.toString()
            }
        }else{
            cost = contentBuyDTO[position].costInt.toString()
        }

        holder.binding.itemRecyclerBuyTextviewCost.text = cost + "원"

        //유저 닉네임 클릭
        holder.binding.itemRecyclerBuyTextviewUsername.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentBuyDTO[position].uid)
            bundle.putString("userId",contentBuyDTO[position].userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            fragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }

        //사진

        Glide.with(holder.itemView.context).load(contentBuyDTO!![position].imageUrl)
            .into(holder.binding.itemRecyclerNormalImageviewImage)

        //옵션 버튼 클릭
        holder.binding.itemRecyclerBuyImagebuttonOption.setOnClickListener {
            val bottomeSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
            var bundle = Bundle()
            bundle.putString("destinationUid",contentBuyDTO[position].uid)
            bundle.putString("userId",contentBuyDTO[position].userId)
            bundle.putString("postUid",contentUidList[position])
            bundle.putString("uid" , contentBuyDTO[position].uid)
            bundle.putString("postType", "buy")
            bundle.putString("viewType","fragment")
            bottomeSheetDialog.arguments = bundle
            bottomeSheetDialog.show(fragmentManager,"dd")
        }
        //전화 걸기
        holder.binding.itemRecyclerBuyImagebuttonPhone.setOnClickListener {
            contentBuyDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("전화 걸기로 바로 이동됩니다. \n 이동하시겠습니까?")
                setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentBuyDTO[position].uid.toString()).document("accountInfo")
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

        //아이템 자체 클릭
        holder.binding.itemRecyclerBuyConstAll.setOnClickListener {
            var intent = Intent(holder.itemView.context, DetailBuyViewActivity::class.java)
            intent.apply {

                putExtra("uid" , contentBuyDTO[position].uid)
                putExtra("userId",contentBuyDTO[position].userId)
                putExtra("postUid",contentUidList[position])
                putExtra("cost",cost)
                putExtra("categoryHash",contentBuyDTO[position].categoryHash)
                putExtra("imageUrl",contentBuyDTO[position].imageUrl)
                putExtra("contentTime",contentBuyDTO[position].time)
                putExtra("explain",contentBuyDTO[position].explain)
                putExtra("userNickName",contentBuyDTO[position].userNickName)
                putExtra("timeStamp",contentBuyDTO[position].timeStamp as Long)

                //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)
                System.out.println("입력된 uid으아아아아앙아" + uid.toString())

            }

            //FirebaseFirestore.getInstance().collection("content").document("buy").collection("data")

            context.startActivity(intent)
            viewCountIncrease(position)
        }
        //꾹 눌러서 클립보드에 내용 복사
        holder.binding.itemRecyclerBuyTextviewExplain.setOnLongClickListener {
            var clipboardManager: ClipboardManager =
                holder.binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("strName",
                holder.binding.itemRecyclerBuyTextviewExplain.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(holder.binding.root.context,"내용이 클립보드에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }

        //시간 표시
        holder.binding.itemRecyclerBuyTextviewTime.text = TimeUtil().formatTimeString(
            contentBuyDTO[position].timeStamp!! as Long)

        //문자 보내기
        holder.binding.itemRecyclerBuyImagebuttonSms.setOnClickListener {

            contentBuyDTO[position].uid

            var builder = AlertDialog.Builder(holder.binding.root.context)

            builder.apply {
                setMessage("문자 보내기로 바로 이동됩니다. \n 이동하시겠습니까?")

                setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                    FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(contentBuyDTO[position].uid.toString()).document("accountInfo")
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

    override fun getItemCount(): Int = data.size

    class ContentBuyRecyclerViewAdapterViewHolder(val binding: ItemRecyclerBuyBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentBuyDTO){
            binding.contentbuy = data
        }
    }
        
    //조회수 증가
    fun viewCountIncrease(position: Int){
        var tsDoc = firestore?.collection("contents")?.document("buy").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentBuyDTO::class.java)


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
        var tsDoc = firestore?.collection("contents")?.document("buy").collection("data")?.document(contentUidList[position])
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentBuyDTO::class.java)

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
                favoriteAlarm(contentBuyDTO[position].uid!!,contentBuyDTO[position].userNickName.toString())
            }
            transaction.set(tsDoc,contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid : String,userNickName : String){


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
                    alarmDTO.timestamp = System.currentTimeMillis()
                    alarmDTO.localTimestamp = TimeUtil().getTime()
                    alarmDTO.userNickName = userNickName
                    FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

                    var message = userNickName + (R.string.alarm_favorite)
                    FcmPush.instance.sendMessage(destinationUid,"신바람",message)
                }

            }


        /*
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

        var message = FirebaseAuth.getInstance()?.currentUser?.email + (R.string.alarm_favorite)
        FcmPush.instance.sendMessage(destinationUid,"신바람",message)

         */
    }


}