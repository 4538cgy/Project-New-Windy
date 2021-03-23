package com.uos.project_new_windy

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.databinding.ActivityDeleteUserDataBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.model.log.PhoneAuthLog
import com.uos.project_new_windy.util.ProgressDialogLoadingVerifyPhone
import com.uos.project_new_windy.util.TimeUtil
import java.util.concurrent.TimeUnit

class DeleteUserDataActivity : AppCompatActivity() {

    lateinit var binding : ActivityDeleteUserDataBinding
    var auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var progressDialog: ProgressDialogLoadingVerifyPhone? = null
    var userModel = UserModel()

    init {



        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot != null)
            {
                userModel = documentSnapshot.toObject(UserModel::class.java)!!
                println("끼에ㅐ에에에에엙" + userModel.toString())

                /*
                if (userModel.email != null){
                    binding.activitySettingTextviewGotoinfo.text = auth.currentUser!!.email.toString()
                }else{
                    binding.activitySettingTextviewGotoinfo.text = userModel.phoneNumber
                }

                //로그인 정보의 이메일
                if (userModel.email == null) {
                    binding.activitySettingTextviewEmail.text = "핸드폰 번호 로그인 정보"
                }else{
                    binding.activitySettingTextviewEmail.text = "이메일 로그인 정보"
                }

                //계정 정보의 이메일
                if (userModel.email == null) {
                    binding.activitySettingTextviewEmailaddress.text = "핸드폰 번호로 로그인 중입니다."
                }else{
                    binding.activitySettingTextviewEmailaddress.text =
                        auth.currentUser?.email.toString()
                }

                 */



            }


        }


    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delete_user_data)
        binding.deleteuserdataactivity = this

        //로딩 초기화
        progressDialog = ProgressDialogLoadingVerifyPhone(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        //구글 로그인 옵션 활성화
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        println("이메일 ${auth.currentUser?.email.toString()}")
        println("핸드폰 번호 ${auth.currentUser?.phoneNumber.toString()}")
        println("유저정보 ${auth.currentUser?.uid.toString()}")
        println("프로바이더 데이터 ${auth.currentUser?.providerData} + ${auth.currentUser?.providerId}")

        //탈퇴 버튼 비활성화
        binding.activityDeleteUserDataDeleteButtonDelete.isEnabled = false
        binding.activityDeleteUserDataDeleteButtonDelete.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
        binding.activityDeleteUserDataDeleteButtonDelete.setTextColor(R.color.colorBlack)

        if (auth.currentUser?.email != null){
            binding.activityDeleteUserTextviewId.text = auth.currentUser!!.email.toString()
        }
        if( auth.currentUser?.phoneNumber!= null) {
            binding.activityDeleteUserTextviewId.text = auth.currentUser!!.phoneNumber.toString()
        }


        //인증 버튼
        binding.activityDeleteUserDataButtonVerify.setOnClickListener {



            /*
            binding.activityDeleteUserDataButtonVerify.isEnabled = false
            if(auth.currentUser?.phoneNumber != null){
                auth.signOut()
                signinPhone()
            }

            if (auth.currentUser?.email != null){
                auth.signOut()
                googleLogin()
            }

             */
        }

        //탈퇴완료
        binding.activityDeleteUserDataDeleteButtonDelete.setOnClickListener {

        }


    }


    fun deleteUserData(){
        Toast.makeText(binding.root.context,"탈퇴 완료",Toast.LENGTH_LONG).show()
    }

    //구글 로그인 시작
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    var account = result.signInAccount
                    //second step
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                //moveMainPage(task.result?.user)
                deleteUserData()
                binding.activityDeleteUserDataDeleteButtonDelete.isEnabled = false
                binding.activityDeleteUserDataDeleteButtonDelete.setBackgroundResource(R.drawable.background_round_windy_green)
                binding.activityDeleteUserDataDeleteButtonDelete.setTextColor(R.color.colorWhite)
            } else {
                //show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun signinPhone(){
        //핸드폰 자동인증 처리


        progressDialog?.show()

        val phoneNumber = "+82" + binding.activityDeleteUserTextviewId.text.toString()
        var code: String? = null
        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )

        //auth.useAppLanguage()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            120,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @SuppressLint("ResourceAsColor")
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //성공시
                    Log.d("credential", p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 성공했습니다.",
                        Toast.LENGTH_LONG).show()
                    progressDialog?.dismiss()

                    FirebaseAuth.getInstance().signInWithCredential(p0).addOnFailureListener {


                    }.addOnCompleteListener {
                        deleteUserData()
                        binding.activityDeleteUserDataDeleteButtonDelete.isEnabled = false
                        binding.activityDeleteUserDataDeleteButtonDelete.setBackgroundResource(R.drawable.background_round_windy_green)
                        binding.activityDeleteUserDataDeleteButtonDelete.setTextColor(R.color.colorWhite)
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다..",
                        Toast.LENGTH_LONG).show()

                    progressDialog?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialog?.dismiss()


                }

            }
        )


    }
}