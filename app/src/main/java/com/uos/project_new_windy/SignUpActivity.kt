package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.uos.project_new_windy.Navigation_Lobby.UserFragment
import com.uos.project_new_windy.Policy.PolicyActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //스피너에서 선택한 작물 종류
    var pickToSpinnerPlant: String? = null

    //파이어베이스 auth
    var mAuth: FirebaseAuth? = null

    val mCallback: OnVerificationStateChangedCallbacks? = null
    val mResendToken: ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //파이어베이스 auth 초기화
        mAuth = FirebaseAuth.getInstance()

        //스피너에 삽입될 아이템 초기화
        val items = resources.getStringArray(R.array.plant)
        //스피너에 사용될 아이템 삽입
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            items
        )



        //스피너에 어댑터 세팅
        activity_sign_up_spinner.adapter = spinnerAdapter


        //sms 인증 요청
        activity_search_address_button_auth_to_phone.setOnClickListener {
            AutoRecieveThePhoneVerifyCode()
        }
        //주소 요청
        activity_search_address_button_address.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }

        //main으로 이동
        activity_sign_up_button_later.setOnClickListener {
            startActivity(Intent(this,LobbyActivity::class.java))
            finish()
        }

        //이용약관 보러가기
        activity_sign_up_textview.setOnClickListener {
            startActivity(Intent(this,PolicyActivity::class.java))
        }

        //프로필 이미지 추가 리스너
        activity_sign_up_circleimageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,
                UserFragment.PICK_PROFILE_FROM_ALBUM
            )
            Log.d(" 회원가입 액티비티" , "인텐트 시작")

        }

    }


    //Open 된 주소 검색 Activity의 결과 반환
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                100 -> {

                    activity_search_address_button_address.setText(
                        data!!.getStringExtra("address_arg1").toString() +
                                data!!.getStringExtra("address_arg2").toString() +
                                data!!.getStringExtra("address_arg3").toString()
                    )

                }

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {
                    activity_sign_up_circleimageview.setImageURI(data?.data)
                }

            }
        }

    }


    //핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {


        val phoneNumber = "+82" + activity_sign_up_edittext_phonenumber.text.toString()
        var code: String? = null

        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //성공시
                    Log.d("credential",p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception",p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                }

                override fun onCodeSent(p0: String, p1: ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                }

            }
        )

    }
    
    //UI 변경 처리
    private fun UpdateView(){

    }

    //스피너 ITEM 선택 리스너
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        pickToSpinnerPlant = activity_sign_up_spinner.selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}