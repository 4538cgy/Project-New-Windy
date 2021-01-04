package com.uos.project_new_windy

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.databinding.ActivityFindPasswordBinding
import com.uos.project_new_windy.model.ContentDTO
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.util.ProgressDialogLoadingPost
import com.uos.project_new_windy.util.ProgressDialogLoadingVerifyPhone
import java.util.concurrent.TimeUnit

class FindPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityFindPasswordBinding

    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()
    var phoneVerify = false
    var progressDialogPhoneVerify: ProgressDialogLoadingVerifyPhone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_password)
        binding.activityfindpassword = this@FindPasswordActivity


        //로딩 초기화
        progressDialogPhoneVerify = ProgressDialogLoadingVerifyPhone(binding.root.context)

        //프로그레스 투명하게
        progressDialogPhoneVerify!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        progressDialogPhoneVerify!!.setCancelable(false)


        binding.activityFindPasswordEdittextPhonenumber.text

        binding.activityFindPasswordButtonSend.setOnClickListener {
            //AutoRecieveThePhoneVerifyCode()
            findPassword()
        }
    }



    fun findPassword(){

        auth.sendPasswordResetEmail( binding.activityFindPasswordEdittextPhonenumber.text.toString())
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    Toast.makeText(binding.root.context, "비밀번호 재설정 이메일이 발송되었습니다. \n 이메일을 확인해주세요.",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(binding.root.context, "이메일을 다시 확인하시거나 고객센터로 연락바랍니다.",Toast.LENGTH_LONG).show()

            }


    }
}