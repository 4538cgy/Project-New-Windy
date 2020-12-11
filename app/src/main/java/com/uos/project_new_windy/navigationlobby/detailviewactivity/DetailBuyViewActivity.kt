package com.uos.project_new_windy.navigationlobby.detailviewactivity

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.databinding.ActivityDetailBuyViewBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.navigationlobby.UserFragment
import kotlinx.android.synthetic.main.item_image_list.view.*

class DetailBuyViewActivity : AppCompatActivity() {
    
    

    lateinit var binding : ActivityDetailBuyViewBinding
    
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
    var explain : String ? = null

    companion object{
        var activity : Activity? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activity = this
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_buy_view)
        binding.activitydetailviewbuy = this@DetailBuyViewActivity

        //이미지 리사이클러뷰 초기화
        binding.activityDetailBuyViewRecyclerPhoto.adapter = DetailContentRecyclerViewAdapter()
        binding.activityDetailBuyViewRecyclerPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)


        //아이디 초기화
        binding.activityDetailBuyViewTextviewId.text = userId

        //채팅으로 거래
        binding.activityDetailBuyViewButtonChat.setOnClickListener {

        }

        //추천
        binding.activityDetailBuyViewButtonLike.setOnClickListener {

        }

        //가격 초기화
        binding.activityDetailSellViewTextviewCost.text = cost + "원"

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

    inner class DetailContentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        var contentImageList : ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("contents")?.document("buy")?.collection("data")?.document(contentUid!!)
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
            //Glide.with(holder.itemView.context).load(contentDTOs[0].imageDownLoadUrlList!![position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)


            /*
            Log.d("이미지 리스트 확인", contentDTOs[position].imageDownLoadUrlList!![position])
            Log.d("이미지 리스트 크기 확인", contentDTOs[0].imageDownLoadUrlList?.size.toString())
            Log.d("새로운 이미지 리스트 확인" , contentImageList[position])
            Log.d("새로운 이미지 리스트 크기 확인" , contentImageList.size.toString())

             */
            Glide.with(holder.itemView.context).load(contentImageList[position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)
            viewHolder.item_image_list_imageview.setOnClickListener {
                    i ->

                Log.d("클릭완료",position.toString())
            }


        }

        override fun getItemCount(): Int {
            return contentImageList.size
        }
    }
}