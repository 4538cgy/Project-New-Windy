package com.uos.project_new_windy.navigationlobby.detailviewactivity

import android.app.Activity
import android.app.AlertDialog
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
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.databinding.ActivityDetailBuyViewBinding
import com.uos.project_new_windy.model.AlarmDTO
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.util.FcmPush
import kotlinx.android.synthetic.main.item_image_list.view.*

class DetailBuyViewActivity : AppCompatActivity() {
    
    

    lateinit var binding : ActivityDetailBuyViewBinding
    
    var contentUid : String ? = null
    var destinationUid : String ? = null
    var commentCount : String ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null
    var userId : String ? = null
    var sellerAddress : String ? = null
    var contentTime : String ? = null
    var cost : String ? = null
    var category : String ? = null
    var explain : String ? = null
    var imageUrl : String ? = null
    var userNickName : String ? = null


    companion object{
        var activity : Activity? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activity = this
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_buy_view)
        binding.activitydetailviewbuy = this@DetailBuyViewActivity

        firestore = FirebaseFirestore.getInstance()

        uid = intent.getStringExtra("uid")
        System.out.println("가져온 uid" + uid.toString())
        userId = intent.getStringExtra("userId")
        contentUid = intent.getStringExtra("postUid")
        cost = intent.getStringExtra("cost")
        category = intent.getStringExtra("categoryHash")
        imageUrl = intent.getStringExtra("imageUrl")
        contentTime = intent.getStringExtra("contentTime")
        explain = intent.getStringExtra("explain")
        //userNickName = intent.getStringExtra("userNickName")



        //유저 닉네임 가져오기
        firestore?.collection("userInfo")?.document("userData")?.collection(uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()

                    println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
                    //아이디 초기화
                    binding.activityDetailBuyViewTextviewId.text = userNickName
                }

            }
        println("유저 닉네임 가져오기 성고오오오오오옹2" + userNickName)


        //이미지 리사이클러뷰 초기화
        /*
        binding.activityDetailBuyViewRecyclerPhoto.adapter = DetailContentRecyclerViewAdapter()
        binding.activityDetailBuyViewRecyclerPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

         */


        //사진클릭
        binding.activityDetailBuyViewImageviewPhoto.setOnClickListener {
            var intent = Intent(this,PhotoDetailViewActivity::class.java)
            intent.putExtra("photoUrl",imageUrl)
            startActivity(intent)
        }



        //채팅으로 거래
        binding.activityDetailBuyViewButtonChat.setOnClickListener {
            if (uid.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                var builder = AlertDialog.Builder(binding.root.context)

                builder.apply {
                    setMessage("구매를 종료하시겠습니까??")
                    setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                        FirebaseFirestore.getInstance().collection("contents").document("buy")
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

        //추천
        binding.activityDetailBuyViewButtonLike.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                favoriteEvent()
            }
        }
        //이미지
        Glide.with(binding.root.context).load(imageUrl)
            .into(binding.activityDetailBuyViewImageviewPhoto)
        //가격 초기화
        binding.activityDetailSellViewTextviewCost.text = cost

        //카테고리 초기화
        binding.activityDetailBuyViewTextviewCategory.text = category

        //주소 초기화
        binding.activityDetailBuyViewTextviewAddress.text = sellerAddress


        //내용 초기화
        binding.activityDetailBuyViewTextviewExplain.text = explain

        //거래 버튼 초기화
        if(uid == FirebaseAuth.getInstance().currentUser?.uid) {
            binding.activityDetailBuyViewButtonChat.text = "거래 완료"
        }else {
            binding.activityDetailBuyViewButtonChat.text = "거래 요청"
        }



        //프로필 이미지 클릭
        binding.activityDetailBuyViewCircleimageviewProfile.setOnClickListener {
            var fragment = UserFragment()
            var bundle = Bundle()
            bundle.putString("destinationUid",uid)
            bundle.putString("userId",userId)
            fragment.arguments = bundle
            //activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment)?.commit()
        }

        //시간 초기화
        binding.activityDetailBuyViewTextviewTime.text = "게시일 : " + contentTime.toString()

        //프로필 이미지
        firestore?.collection("profileImages")?.document(uid!!)?.get()?.addOnCompleteListener {
                task ->
            if (task.isSuccessful)
            {
                System.out.println("으아아아아아아ㅏ"+  task.result!!["image"].toString())
                var url = task.result!!["image"]
                Glide.with(this).load(url).apply(RequestOptions().circleCrop()).into(binding.activityDetailBuyViewCircleimageviewProfile)

            }
        }
        //옵션
        binding.activityDetailBuyViewOptionButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                val bottomSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
                var bundle = Bundle()
                bundle.putString("destinationUid",destinationUid)
                bundle.putString("userId", userId)
                bundle.putString("postUid",contentUid)
                bundle.putString("uid" , uid)
                bundle.putString("postType", "buy")
                bundle.putString("viewType","activity")
                bottomSheetDialog.arguments = bundle
                bottomSheetDialog.show(supportFragmentManager,"lol")
            }
        }



    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteEvent(){


        System.out.println("좋아요 이벤트 ㅇㅅㅇ")
        var tsDoc = firestore?.collection("contents")?.document("buy")?.collection("data")?.document(contentUid!!)
        firestore?.runTransaction{ transaction ->



            System.out.println("트랜잭션 시작")
            var contentDTO = transaction.get(tsDoc!!).toObject(ContentBuyDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)){
                //When the button is clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                contentDTO?.favorites.remove(uid)
                binding.activityDetailBuyViewButtonLike.text = "추천 취소"

                System.out.println("uid 존재")
            }else{
                System.out.println("uid 미존재")
                //When the button is not clicked
                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                contentDTO?.favorites[uid!!] = true
                favoriteAlarm(uid!!, contentUid!!,explain!!)
                binding.activityDetailBuyViewButtonLike.text = "이 글을 추천"
            }
            transaction.set(tsDoc,contentDTO)


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun favoriteAlarm(destinationUid : String, postUid : String, postExplain : String){

        System.out.println("좋아요 알람 이벤트")



        firestore?.collection("userInfo")?.document("userData")?.collection(uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()

                    println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
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


}