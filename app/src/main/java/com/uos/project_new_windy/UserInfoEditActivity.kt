package com.uos.project_new_windy

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityUserInfoEditBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.util.SharedData

class UserInfoEditActivity : AppCompatActivity() {

    lateinit var binding : ActivityUserInfoEditBinding
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var userModel = UserModel()
    var zipCode : String ? = null
    var address : String ? = null
    var building : String ? = null

    var gac : GoogleApiClient? = null

    init {
        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot != null)
            {
                userModel = documentSnapshot.toObject(UserModel::class.java)!!
                println("끼에ㅐ에에에에엙" + userModel.toString())
                //닉네임란 초기화
                binding.activityUserInfoEditEdittextNickname.hint = "현재 닉네임 : " + userModel.userName
                //주소 초기화
                binding.activityUserInfoEditTextviewAddress.text = "현재 주소 : " + userModel.totalAddress
                //상세 주소 초기화
                binding.activityUserInfoEditTextviewDetailAddress.hint = "현재 상세주소 : " + userModel.addressDetail
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_info_edit)
        binding.activityuserinfoedit = this@UserInfoEditActivity


        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gac = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()
        println(userModel.toString() + " 꿍애애애애애애애애애애애ㅐㅋ")
        //뒤로가기
        binding.activityUserInfoEditImagebuttonClose.setOnClickListener {
            finish()
        }



        //주소 검색
        binding.activityUserInfoEditButtonAddressSearch.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }

        //회원 탈퇴
        binding.activityUserInfoEditDeleteMyInfo.setOnClickListener {

            var builder = AlertDialog.Builder(binding.root.context)


            builder.apply {
                setMessage("회원 탈퇴시 복구가 불가능합니다. \n탈퇴하시겠습니까?")
                setPositiveButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })
                setNegativeButton("예" , DialogInterface.OnClickListener { dialog, which ->
                    deleteUser()
                })
                setTitle("안내")
                show()
            }

        }

        //정보 수정 요청
        binding.activityUserInfoEditUpdateMyInfo.setOnClickListener {
            updateUser()
        }
    }

    fun deleteUser(){
        //startActivity(Intent(binding.root.context,DeleteUserDataActivity::class.java))
        val user = FirebaseAuth.getInstance().currentUser;


        user?.delete()?.addOnCompleteListener {
            Toast.makeText(binding.root.context, "회원 정보가 모두 삭제되었습니다.",Toast.LENGTH_LONG).show()
            SharedData.prefs.setString("userInfo","no")
            SharedData.prefs.setString("emailVeryify","no")
            startActivity(Intent(binding.root.context,SplashActivity::class.java))
            finish()
        }?.addOnFailureListener {
            Toast.makeText(binding.root.context, "로그인 정보가 너무 오래되었습니다. \n다시 로그인하여 시도해주세요.\n혹은 고객센터로 문의바랍니다.",Toast.LENGTH_LONG).show()
            signOut()
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
                            startActivity(Intent(binding.root.context,SplashActivity::class.java))
                            finish()
                            setResult(1)
                        } else {
                            setResult(0)
                        }
                    }
                }
            }

            override fun onConnectionSuspended(i: Int) {
                Log.v("알림", "Google API Client Connection Suspended")
                setResult(-1)
            }
        })


    }

    fun updateUser(){
        var tsDoc = firestore?.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo")

        firestore.runTransaction {
            transaction ->

            var userDTO = transaction.get(tsDoc).toObject(UserModel::class.java)
            userDTO?.address = binding.activityUserInfoEditTextviewAddress.text.toString()
            userDTO?.addressDetail = binding.activityUserInfoEditTextviewDetailAddress.text.toString()
            userDTO?.userName = binding.activityUserInfoEditEdittextNickname.text.toString()



            transaction.set(tsDoc, userDTO!!)
        }.addOnFailureListener {
            println("userData Update Fail${it.toString()}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                100 -> {

                    binding.activityUserInfoEditTextviewAddress.setText(data!!.getStringExtra("address_arg1")
                        .toString() +
                            data!!.getStringExtra("address_arg2").toString() +
                            data!!.getStringExtra("address_arg3").toString())
                    zipCode = data!!.getStringExtra("address_arg1").toString()
                    address = data!!.getStringExtra("address_arg2").toString()
                    building = data!!.getStringExtra("address_arg3").toString()

                }

            }
        }

    }


}