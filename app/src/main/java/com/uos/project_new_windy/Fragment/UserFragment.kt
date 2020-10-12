package com.uos.project_new_windy.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.Model.ContentDTO
import com.uos.project_new_windy.R
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {

    var fragmentView : View ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null
    var auth : FirebaseAuth ? = null
    var currentUserUid : String ? = null


    companion object{
        var PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //프라그먼트에 뷰 연결
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)



        //각종 변수 초기화
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid


        //리사이클러뷰 초기화
        fragmentView?.fragment_user_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.fragment_user_recyclerview?.layoutManager = GridLayoutManager(activity!!,3)

        //프로필 이미지 변경 이벤트
        fragmentView?.fragment_user_circle_imageview?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }

        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        getProfileImage()
    }

    fun getProfileImage(){


        //미리 들고있던 Profile Image Url을 Glide로 먼저 삽입

        
        //프로필 이미지 변경 요청이 이뤄지면 firestore에서 Url 을 가져온뒤 Profile 이미지 변수에 저장 후 Glide에 가져오기


        //AppCompatDialog를 이미지가 변경되는 동안 출력되게 하기

        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener{

                documentSnapshot, firebaseFirestoreException ->


            if (documentSnapshot?.data != null){
                var url = documentSnapshot?.data!!["image"]

                Log.d("프로필 이미지 url = \n",url.toString())
                Glide.with(activity!!.applicationContext)
                    .load(url)
                    .apply(
                        RequestOptions().centerCrop()
                    )
                    .error(R.drawable.btn_signin_google)
                    .into(fragmentView?.fragment_user_circle_imageview!!)
                LobbyActivity.progressDialog?.dismiss()
            }
        }


    }



    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("contents")?.whereEqualTo("uid",uid)?.addSnapshotListener{
                    querySnapshot, firebaseFirestoreException ->
                //Sometimes, this code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var imageView = (holder as CustomViewHolder).imageView

            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }


    }
}