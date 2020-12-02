package com.uos.project_new_windy.navigationlobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.DetailContentActivity
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.R
import com.uos.project_new_windy.SplashActivity
import com.uos.project_new_windy.databinding.FragmentUserBinding
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {

    var fragmentView : View ? = null
    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null
    var auth : FirebaseAuth ? = null
    var currentUserUid : String ? = null

    var gac : GoogleApiClient ? = null

    lateinit var binding : FragmentUserBinding

    companion object{
        var PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //프라그먼트에 뷰 연결
        //fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user,container,false)


        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gac = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()

        //각종 변수 초기화
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid


        //리사이클러뷰 초기화
        /*
        fragmentView?.fragment_user_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.fragment_user_recyclerview?.layoutManager = GridLayoutManager(activity!!,3)

         */

        binding.fragmentUserRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        binding.fragmentUserRecyclerview.layoutManager = GridLayoutManager(activity!!,3)

        //프로필 이미지 변경 이벤트
        /*
        fragmentView?.fragment_user_circle_imageview?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }
         */
         */
        binding.fragmentUserCircleImageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }


        //fragment_user_textview_history_content 눌렀을때 이벤트
        binding.fragmentUserTextviewHistoryContent.setOnClickListener {

        }

        //fragment_user_textview_history_view 눌렀을때 이벤트
        binding.fragmentUserTextviewHistoryView.setOnClickListener {

        }

        
        //현재 보고있는 화면의 유저가 내가 아닌지를 판단
        if(!uid.equals(FirebaseAuth.getInstance().currentUser?.uid)){
             binding.accountBtnFollowSignout.text = "친구 맺기"
        }else if (uid.equals(FirebaseAuth.getInstance().currentUser?.uid))
        {
            binding.accountBtnFollowSignout.text = "로그아웃"

        }


        //팔로우&로그아웃 버튼을 눌렀을때의 이벤트
        binding.accountBtnFollowSignout.setOnClickListener {
            if (uid.equals(FirebaseAuth.getInstance().currentUser?.uid))
            {
                //로그아웃
                FirebaseAuth.getInstance().signOut()
                signOut()
            }else
            {
                //팔로우 이벤트
            }
        }

        //메세지 버튼 클릭했을 때
        binding.fragmentUserButtonMessage.setOnClickListener {

        }

        //신고하기  버튼 클릭했을 때
        binding.fragmentUserButtonReport.setOnClickListener {

        }

        return binding.root
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
                /*
                Glide.with(activity!!.applicationContext)
                    .load(url)
                    .apply(
                        RequestOptions().centerCrop()
                    )
                    .error(R.drawable.btn_signin_google)
                    .into(fragmentView?.fragment_user_circle_imageview!!)

                 */


                Glide.with(activity!!.applicationContext)
                    .load(url)
                    .apply(
                        RequestOptions().centerCrop()
                    )
                    .error(R.drawable.btn_signin_google)
                    .into(binding.fragmentUserCircleImageview)
                LobbyActivity.progressDialog?.dismiss()
            }
        }


    }



    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("contents")?.whereEqualTo("uid",uid)?.addSnapshotListener{
                    querySnapshot, firebaseFirestoreException ->
                //Sometimes, this code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    contentUidList.add(snapshot.id)

                }

                System.out.println("내가 올린 게시글의 갯수" + contentDTOs.size)
                //fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                binding.accountTvPostCount.text = contentDTOs.size.toString()
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
             Glide.with(holder.itemView.context).load(contentDTOs[position].imageDownLoadUrlList?.get(0)).apply(RequestOptions().centerCrop()).into(imageView)
           // System.out.println("이미지의 url" + contentDTOs[position].imageDownLoadUrlList)
           // Glide.with(holder.itemView.context).load(contentImageList[position]).apply(RequestOptions().centerCrop()).into(viewHolder.item_image_list_imageview)


            imageView.setOnClickListener {
                     i ->
                var intent = Intent(i.context, DetailContentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("commentCount", contentDTOs!![position].commentCount.toString())
                intent.putExtra("likeCount",contentDTOs!![position].favoriteCount.toString())
                intent.putExtra("contentTime",contentDTOs!![position].time)

                startActivity(intent)

            }
        }


    }

    fun signOut(){
        gac?.connect()



        gac?.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                FirebaseAuth.getInstance().signOut()
                if (gac!!.isConnected()) {
                    Auth.GoogleSignInApi.signOut(gac).setResultCallback { status ->
                        if (status.isSuccess) {
                            Log.v("알림", "로그아웃 성공")
                            startActivity(Intent(activity?.applicationContext,SplashActivity::class.java))
                            activity?.finish()
                            activity?.setResult(1)
                        } else {
                            activity?.setResult(0)
                        }
                    }
                }
            }

            override fun onConnectionSuspended(i: Int) {
                Log.v("알림", "Google API Client Connection Suspended")
                activity?.setResult(-1)
            }
        })


    }
}