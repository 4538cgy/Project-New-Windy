package com.uos.project_new_windy.navigationlobby.detailviewactivity

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.LoginActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.databinding.ActivityDetailNormalViewBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.item_detail_view_image_list.view.*
import kotlinx.android.synthetic.main.item_image_list.view.*

class DetailNormalViewActivity : AppCompatActivity() {

    var uid : String ? = null
    var userId : String ? = null
    var postUid : String ? = null
    var imageList : ArrayList<String> ? = null
    var contentTime : String ? = null
    var explain : String ? = null
    var likeCount : Int ? = null
    var userNickName : String ? = null
    var timeStamp : Long ? = null

    lateinit var binding : ActivityDetailNormalViewBinding

    var firestore : FirebaseFirestore ? = null
    var auth : FirebaseAuth? = null

    companion object{
        var activity : Activity? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activity = this

        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_normal_view)
        binding.activitydetailviewnormal = this@DetailNormalViewActivity

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        uid = intent.getStringExtra("uid")
        userId = intent.getStringExtra("userId")
        postUid = intent.getStringExtra("postUid")
        imageList = intent.getStringArrayListExtra("imageList")
        contentTime = intent.getStringExtra("contentTime")
        likeCount = intent.getIntExtra("likeCount",0)
        explain = intent.getStringExtra("explain")
        userNickName = intent.getStringExtra("userNickName")
        timeStamp = intent.getLongExtra("timeStamp",0)


        //본문 복사
        binding.activityDetailNormalButtonCopy.setOnClickListener {
            var clipboardManager: ClipboardManager =
                binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("strName",
                binding.activityDetailNormalViewTextviewContent.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(binding.root.context,"내용이 클립보드에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }

        //유저 닉네임 가져오기
        firestore?.collection("userInfo")?.document("userData")?.collection(uid!!)?.document("accountInfo")
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot != null)
                {
                    userNickName = documentSnapshot.get("userName")?.toString()

                    println("유저 닉네임 가져오기 성고오오오오오옹" + userNickName)
                    //아이디 초기화
                    binding.activityDetailNormalViewTextviewNickname.text = userNickName
                }

            }

        //이미지 리사이클러뷰 초기화
        binding.activityDetailNormalViewRecyclerPhoto.adapter = DetailContentRecyclerViewAdapter()
        binding.activityDetailNormalViewRecyclerPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)



        //좋아요 갯수 초기화
        binding.activityDetailNormalViewTextviewLikeCount.text = "좋아요 "+likeCount+" 개"
        System.out.println("좋아요 갯수는 "  + likeCount)
        //댓글 버튼
        binding.activityDetailNormalViewImagebuttonComment.setOnClickListener {
            var intent = Intent(this, CommentActivity::class.java)
            intent.apply {
                putExtra("contentUid",postUid)
                putExtra("destinationUid",uid)
                putExtra("postType","normal")
            }
            startActivity(intent)
        }

        //좋아요 버튼
        binding.activityDetailNormalViewImagebuttonLike.setOnClickListener {

        }

        //옵션
        binding.activityDetailNormalViewImagebuttonPostOption.setOnClickListener {

            if(auth!!.currentUser != null) {
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    val bottomSheetDialog : BottomSheetDialogContentOption = BottomSheetDialogContentOption()
                    var bundle = Bundle()
                    bundle.putString("destinationUid",uid)
                    bundle.putString("userId", userId)
                    bundle.putString("postUid",postUid)
                    bundle.putString("uid" , auth!!.currentUser?.uid.toString())
                    bundle.putString("postType", "normal")
                    bundle.putString("viewType","activity")
                    bundle.putString("boardType","normal")
                    bottomSheetDialog.arguments = bundle
                    bottomSheetDialog.show(supportFragmentManager,"lol")
                }
            }else{
                var builder = AlertDialog.Builder(binding.root.context)


                builder.apply {
                    setMessage("비로그인 이용자는 이용할 수 없습니다. \n로그인 페이지로 이동하시겠습니까?")
                    setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(binding.root.context, LoginActivity::class.java))
                        finishAffinity()
                    })
                    setNegativeButton("아니요" , DialogInterface.OnClickListener { dialog, which ->
                        return@OnClickListener

                    })
                    setTitle("안내")
                    show()
                }
            }


        }

        //프로필 초기화
        firestore?.collection("profileImages")?.document(uid!!)?.get()?.addOnCompleteListener {
                task ->
            if (task.isSuccessful)
            {
                var url = task.result!!["image"]
                Glide.with(this).load(url).apply(RequestOptions().circleCrop()).into(binding.activityDetailNormalViewCircleimageview)

            }
        }



        //시간 초기화
        binding.activityDetailNormalViewTextviewTime.text = "게시일 : "+ TimeUtil().formatTimeString(timeStamp!!)

        //내용 초기화
        binding.activityDetailNormalViewTextviewContent.text = explain

        binding.activityDetailNormalViewTextviewContent.setOnLongClickListener {
            var clipboardManager: ClipboardManager =
                binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("strName",
                binding.activityDetailNormalViewTextviewContent.text.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(binding.root.context,"내용이 클립보드에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }
    }

    inner class DetailContentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        var contentImageList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("contents")?.document("normal")?.collection("data")?.document(postUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()

                    if(documentSnapshot == null)
                        return@addSnapshotListener

                    //querySnapshot["imageDownLoadUrlList"]
                    if (documentSnapshot.exists()) {
                        contentImageList =
                            documentSnapshot.get("imageDownLoadUrlList") as ArrayList<String>

                        contentDTOs.add(documentSnapshot.toObject(ContentDTO::class.java)!!)
                    }
                }
            /*
            firestore?.collection("contents")?.document(contentUid!!)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()

                    if(querySnapshot == null)
                        return@addSnapshotListener

                    //querySnapshot["imageDownLoadUrlList"]
                    contentImageList = querySnapshot.get("imageDownLoadUrlList") as ArrayList<String>
                    contentDTOs.add(querySnapshot.toObject(ContentDTO::class.java)!!)

                }

             */
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_view_image_list,parent,false)
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
            //Glide.with(holder.itemView.context).load(contentDTOs[0].imageDownLoadUrlList!![position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)


            /*
            Log.d("이미지 리스트 확인", contentDTOs[position].imageDownLoadUrlList!![position])
            Log.d("이미지 리스트 크기 확인", contentDTOs[0].imageDownLoadUrlList?.size.toString())
            Log.d("새로운 이미지 리스트 확인" , contentImageList[position])
            Log.d("새로운 이미지 리스트 크기 확인" , contentImageList.size.toString())

             */
            Glide.with(holder.itemView.context).load(contentImageList[position]).apply(
                RequestOptions().centerCrop()).into(viewHolder.item_detail_view_image_list_imageview)
            viewHolder.item_detail_view_image_list_imageview.setOnClickListener {
                    i ->

                Log.d("클릭완료",position.toString())
                var intent = Intent(viewHolder.context,PhotoDetailSlideViewActivity::class.java)
                System.out.println("우와아아아아아아아앜")
                intent.putExtra("photoUrl",contentImageList[position])
                intent.putExtra("photoList",contentImageList)
                viewHolder.context.startActivity(intent)
            }


        }

        override fun getItemCount(): Int {
            return contentImageList.size
        }
    }

}