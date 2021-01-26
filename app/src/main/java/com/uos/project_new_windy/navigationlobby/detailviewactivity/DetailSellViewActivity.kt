package com.uos.project_new_windy.navigationlobby.detailviewactivity

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.chat.ChatActivity
import com.uos.project_new_windy.chat.ChatRoomList
import com.uos.project_new_windy.databinding.ActivityDetailSellViewBinding
import com.uos.project_new_windy.databinding.ActivityDetailSellViewBindingImpl
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_image_list.view.*



class DetailSellViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailSellViewBinding
    var contentUid : String ? = null
    var destinationUid : String ? = null
    var commentCount : String ? = null
    var likeCount : String ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null
    var userId : String ? = null
    var profileImageUrl : Any ? = null
    var sellerAddress : String ? = null
    var contentTime : String ? = null
    var cost : String ? = null
    var category : String ? = null
    var productExplain : String ? = null
    var explain : String ? = null
    var userNickName : String ? = null
    var timeStamp : Long ? = null

    companion object{
        var activity : Activity ? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activity = this

        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_sell_view)
        binding.activitydetailviewsell = this@DetailSellViewActivity

        firestore = FirebaseFirestore.getInstance()


        userId = intent.getStringExtra("userId")
        uid = intent.getStringExtra("uid")
        contentUid = intent.getStringExtra("postUid")
        destinationUid = intent.getStringExtra("destinationUid")
        commentCount = intent.getStringExtra("commentCount")
        likeCount = intent.getStringExtra("likeCount")
        contentTime = intent.getStringExtra("contentTime")
        sellerAddress = intent.getStringExtra("sellerAddress")
        cost = intent.getStringExtra("cost")
        category = intent.getStringExtra("category")
        productExplain = intent.getStringExtra("productExplain")
        explain = intent.getStringExtra("explain")
        userNickName = intent.getStringExtra("userNickName")
        timeStamp = intent.getLongExtra("timeStamp",0)

        //유저 닉네임 가져오기
        firestore?.collection("userInfo")?.document("userData")?.collection(uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()

                    println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
                    //아이디 초기화
                    binding.activityDetailSellViewTextviewId.text = userNickName
                }

            }
        
        
        //본문 복사
        binding.activityDetailSellButtonCopy.setOnClickListener {
            var clipboardManager: ClipboardManager =
                binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("strName",
                binding.activityDetailSellViewTextviewExplain.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(binding.root.context,"내용이 클립보드에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }

        //이미지 리사이클러뷰 초기화
        binding.activityDetailSellViewRecyclerPhoto.adapter = DetailContentRecyclerViewAdapter()
        binding.activityDetailSellViewRecyclerPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)


        //댓글 리사이클러뷰 초기화
        //binding.activityDetailSellViewRecyclerComment.adapter = DetailContentCommentRecyclerViewAdapter()
        //binding.activityDetailSellViewRecyclerComment.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        //각종 버튼 초기화

        //채팅으로 거래
        binding.activityDetailSellViewButtonChat.setOnClickListener {

            if (uid.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                var builder = AlertDialog.Builder(binding.root.context)


                builder.apply {
                    setMessage("판매를 종료하시겠습니까??")
                    setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->

                        FirebaseFirestore.getInstance().collection("contents").document("sell")
                            .collection("data").document(
                                contentUid!!
                            )
                            .delete()
                            .addOnFailureListener {
                                //실패
                                System.out.println("삭제 실패")
                            }.addOnSuccessListener {
                                //성공
                                System.out.println("삭제 성공")
                                /*
                                var intent = Intent(this, LobbyActivity::class.java)

                                //startActivity(Intent(this, LobbyActivity::class.java))
                                startActivity(intent)
                                finish()

                                 */


                            }
                        finishAffinity()
                    })
                    setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }else {


                System.out.println("채팅 보내기를 열었습니다.")
                var intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.apply {
                    putExtra("destinationUid", uid)
                }
                startActivity(intent)
            }
        }

        //찜하기
        binding.activityDetailSellViewButtonPicking.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                favoriteEvent()
            }
        }



        //가격 초기화
        binding.activityDetailSellViewTextviewCost.text = "가격" + cost

        //카테고리 초기화
        binding.activityDetailSellViewTextviewCategory.text = category
        
        //주소 초기화
        binding.activityDetailSellViewTextviewAddress.text = sellerAddress


        //제목 초기화
        binding.activityDetailSellViewTextviewTitle.text = productExplain

        //내용 초기화
        binding.activityDetailSellViewTextviewExplain.text = explain


        //거래 버튼 초기화
        if(uid == FirebaseAuth.getInstance().currentUser?.uid) {
            binding.activityDetailSellViewButtonChat.text = "거래 완료"
        }else {
            binding.activityDetailSellViewButtonChat.text = "거래 요청"
        }
        
        //프로필 이미지 클릭
        binding.activityDetailSellViewCircleimageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",uid)
            bundle.putString("userId",userId)
            bundle.putString("viewType","activity")
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }

        //시간 초기화
        binding.activityDetailSellViewTextviewTime.text = "게시일 : "+ TimeUtil().formatTimeString(timeStamp!!)

        //프로필 이미지
        firestore?.collection("profileImages")?.document(uid!!)?.get()?.addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {

                var url = task.result!!["image"]
                Glide.with(this).load(url).apply(RequestOptions().circleCrop()).into(binding.activityDetailSellViewCircleimageviewProfile)

            }
        }

        //유저 아이디 클릭
        binding.activityDetailSellViewTextviewId.setOnClickListener {

        }

        //옵션
        binding.activityDetailSellViewOptionButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                val bottomSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
                var bundle = Bundle()
                bundle.putString("destinationUid",destinationUid)
                bundle.putString("userId", userId)
                bundle.putString("postUid",contentUid)
                bundle.putString("uid" , uid)
                bundle.putString("postType", "sell")

                bottomSheetDialog.arguments = bundle
                bottomSheetDialog.show(supportFragmentManager,"lol")
            }
        }

        binding.activityDetailSellViewTextviewExplain.setOnLongClickListener {
            var clipboardManager: ClipboardManager =
                binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("strName",
                binding.activityDetailSellViewTextviewExplain.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(binding.root.context,"내용이 클립보드에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(){


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("contents")?.document("sell")?.collection("data")?.document(contentUid!!)
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentSellDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)){
                //When the button is clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)
                binding.activityDetailSellViewButtonPicking.text = "추천 취소"

                System.out.println("uid 존재")
            }else{
                System.out.println("uid 미존재")
                //When the button is not clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(uid!!, contentUid!!,explain!!)
                binding.activityDetailSellViewButtonPicking.text = "이 글을 추천"
            }
            transaction.set(tsDoc,contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid : String, postUid : String, postExplain : String){

        System.out.println("좋아요 알람 이벤트")
        firestore?.collection("userInfo")?.document("userData")?.collection(FirebaseAuth.getInstance().currentUser?.uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()

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
                    FcmPush.instance.sendMessage(destinationUid,"신바람",message)
                }

            }
    }
    
    
    //댓글 리사이클러뷰 어댑터
    inner class DetailContentCommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentCommentList : ArrayList<ContentDTO.Comment> = arrayListOf()


        init {


            FirebaseFirestore.getInstance()
                .collection("contents")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    contentCommentList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents!!){
                        contentCommentList.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()

                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view  = holder.itemView

            view.item_comment_textview_comment.text = contentCommentList[position].comment
            view.item_comment_textview_profile.text = contentCommentList[position].userId
            view.item_comment_textview_time.text = contentCommentList[position].time.toString()

            Glide.with(holder.itemView.context).load(profileImageUrl).apply(RequestOptions().circleCrop()).into(view.item_comment_circleImageview)

        }

        override fun getItemCount(): Int {
            return contentCommentList.size
        }

    }

    //사진 리사이클러뷰 어댑터
    inner class DetailContentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        var contentImageList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("contents")?.document("sell")?.collection("data")?.document(contentUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()

                    if(documentSnapshot == null)
                        return@addSnapshotListener

                    //querySnapshot["imageDownLoadUrlList"]
                    if(documentSnapshot.exists()) {
                        if (documentSnapshot.get("imageDownLoadUrlList") != null) {
                            contentImageList =
                                documentSnapshot.get("imageDownLoadUrlList") as ArrayList<String>
                        }

                        contentDTOs.add(documentSnapshot.toObject(ContentDTO::class.java)!!)
                    }
                }

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_list,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewHolder = (holder as CustomViewHolder).itemView



            contentDTOs[0].imageDownLoadUrlList?.forEach {
                    i ->
                Log.d("이미지 리스트" , i)
            }

            Glide.with(holder.itemView.context).load(contentImageList[position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)
            viewHolder.item_image_list_imageview.setOnClickListener {
                    i ->

                Log.d("클릭완료",position.toString())
                var intent = Intent(viewHolder.context,PhotoDetailViewActivity::class.java)
                System.out.println("우와아아아아아아아앜")
                intent.putExtra("photoUrl",contentImageList[position])
                viewHolder.context.startActivity(intent)
            }



        }

        override fun getItemCount(): Int {
            return contentImageList.size
        }
    }


}