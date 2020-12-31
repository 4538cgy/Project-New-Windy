package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.databinding.ActivitySignUpEmailBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.policy.PolicyActivity
import com.uos.project_new_windy.util.*
import java.util.concurrent.TimeUnit

class SignUpActivityEmail : AppCompatActivity() {

    lateinit var binding: ActivitySignUpEmailBinding

    var mAuth = FirebaseAuth.getInstance()
    var imageUri : Uri? = null
    var policyAcceptCheck = false
    var phoneVerify = false
    var progressDialogPhoneVerify: ProgressDialogLoadingVerifyPhone? = null
    var progressSignUpProcess : ProgressDialogLoadingPost ? = null

    /*
    카카오 지번 검색에서 가져온 데이터들 변수
     */
    var zipCode: String? = null
    var address: String? = null
    var building: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_email)
        binding.activitysignupemail = this@SignUpActivityEmail

        //이메일 인증
        //로딩 초기화
        progressDialogPhoneVerify = ProgressDialogLoadingVerifyPhone(binding.root.context)
        progressSignUpProcess = ProgressDialogLoadingPost(binding.root.context)
        //프로그레스 투명하게
        progressDialogPhoneVerify!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressSignUpProcess!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        progressDialogPhoneVerify!!.setCancelable(false)
        progressSignUpProcess!!.setCancelable(false)
        //주소 검색
        binding.activitySignUpEmailButtonSearchAddress.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }


        //핸드폰 인증
        binding.activitySignUpEmailButtonAuthToPhone.setOnClickListener {

            binding.activitySignUpEmailEdittextPhonenumber.isClickable = false
            binding.activitySignUpEmailEdittextPhonenumber.isFocusable = false
            binding.activitySignUpEmailEdittextPhonenumber.isFocusableInTouchMode = false
            binding.activitySignUpEmailEdittextPhonenumber.isEnabled = false
            binding.activitySignUpEmailButtonAuthToPhone.isEnabled = false


            Toast.makeText(binding.root.context, "인증이 일시적으로 제한되었습니다. \n 인증 허용", Toast.LENGTH_LONG)
                .show()
            phoneVerify = true

            if (!ScamerPhoneNumberData().getExistPhoneNumber(binding.activitySignUpEmailEdittextPhonenumber.text.toString())) {
                //AutoRecieveThePhoneVerifyCode()
            } else {
                Toast.makeText(binding.root.context,
                    "가입이 제한된 핸드폰 번호입니다. \n 고객센터에 문의해주세요.",
                    Toast.LENGTH_LONG).show()
            }
        }

        //프로필 이미지 가져오기
        binding.activitySignUpEmailCircleimageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,
                UserFragment.PICK_PROFILE_FROM_ALBUM
            )
        }

        //이용약관
        binding.activitySignUpEmailTextviewPolicy.setOnClickListener {
            startActivity(Intent(this,PolicyActivity::class.java))
            policyAcceptCheck = true
        }

        //회원 가입 완료
        binding.activitySignUpEmailButtonAccept.setOnClickListener {

            //빈칸 다 채웠는지 확인 후 사진 부터 업로드
            progressSignUpProcess?.show()
            createUser()
        }
    }

    //핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {

        progressDialogPhoneVerify?.show()

        val phoneNumber = "+82" + binding.activitySignUpEmailEdittextPhonenumber.text.toString()
        var code: String? = null
        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )

        //auth.useAppLanguage()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //성공시
                    Log.d("credential", p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 성공했습니다. \n 나머지 정보를 입력해주세요.",
                        Toast.LENGTH_LONG).show()
                    progressDialogPhoneVerify?.dismiss()

                    binding.activitySignUpEmailEdittextPhonenumber.isClickable = false
                    binding.activitySignUpEmailEdittextPhonenumber.isFocusable = false
                    binding.activitySignUpEmailEdittextPhonenumber.isFocusableInTouchMode = false
                    binding.activitySignUpEmailEdittextPhonenumber.isEnabled = false
                    binding.activitySignUpEmailButtonAuthToPhone.isEnabled = false
                    phoneVerify = true
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다. \n 올바른 번호를 입력해주세요.",
                        Toast.LENGTH_LONG).show()




                    progressDialogPhoneVerify?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialogPhoneVerify?.dismiss()

                    UpdateView()
                }

            }
        )

    }

    //UI 변경 처리
    private fun UpdateView() {
        Toast.makeText(this, "인증에 실패하였습니다. \n 핸드폰 번호를 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show()
    }

    fun createUser(){
        var email = binding.activitySignUpEmailEdittextEmail.text.toString()
        var password = binding.activitySignUpEmailEdittextPassword.text.toString()

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    //회원 생성 성공
                    mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                //로그인 성공
                                uploadPhoto()
                            }else{
                                //로그인 실패

                            }
                        }

                }else{
                    //회원 생성 실패
                }
            }
    }

    fun uploadPhoto(){
        //사진 저장
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var storageRef =
            FirebaseStorage.getInstance().reference.child("userProfileImages")
                .child(uid!!)
        storageRef.putFile(imageUri!!)
            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages")
                    .document(uid).set(map)
                
                //사진 저장 완료 후 DB에 유저 정보 저장
                userDataSave()

            }
    }

    fun userDataSave(){

        var userModel = UserModel()

        //회원정보 DB에 저장
        //주소
        userModel.totalAddress = binding.activitySignUpEmailEdittextAddress.text.toString()
        //상세주소
        userModel.addressDetail = binding.activitySignUpEmailEdittextDetailAddress.text.toString()

        //세부주소
        userModel.address = address
        userModel.building = building
        userModel.zipCode = zipCode

        //핸드폰 번호
        userModel.phoneNumber = binding.activitySignUpEmailEdittextPhonenumber.text.toString()
        //핸드폰 시간
        userModel.time = TimeUtil().getTime().toString()
        //서버 시간
        userModel.timeStamp = FieldValue.serverTimestamp()
        //유저 uid
        userModel.uid = FirebaseAuth.getInstance().currentUser?.uid
        //유저 닉네임
        userModel.userName = binding.activitySignUpEmailEdittextNickname.text.toString()
        userModel.point = 100
        userModel.memberRating = "0"
        userModel.policyAccept = policyAcceptCheck

        FirebaseFirestore.getInstance().collection("userInfo").document("userData")
            .collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .document("accountInfo").set(userModel)
            .addOnSuccessListener {
                System.out.println("유저 정보 저장 성공")
                progressSignUpProcess?.dismiss()
                //회원 정보 저장
                SharedData.prefs.setString("userInfo", "yes")

                //메인으로 이동하게 startActivity 작성해주세요.
                startActivity(Intent(this, LobbyActivity::class.java))
                finish()
            }.addOnFailureListener {
                System.out.println("유저 정보 저장 실패")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                100 -> {
                    /*
                    activity_search_address_button_address.setText(
                        data!!.getStringExtra("address_arg1").toString() +
                                data!!.getStringExtra("address_arg2").toString() +
                                data!!.getStringExtra("address_arg3").toString()
                    )

                     */

                    binding.activitySignUpEmailEdittextAddress.setText(data!!.getStringExtra("address_arg1")
                        .toString() +
                            data!!.getStringExtra("address_arg2").toString() +
                            data!!.getStringExtra("address_arg3").toString())
                    zipCode = data!!.getStringExtra("address_arg1").toString()
                    address = data!!.getStringExtra("address_arg2").toString()
                    building = data!!.getStringExtra("address_arg3").toString()
                    binding.activitySignUpEmailButtonSearchAddress.isEnabled = false


                }

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {

                    binding.activitySignUpEmailCircleimageview.setImageURI(data?.data)
                    imageUri = data?.data

                    if (mAuth?.currentUser != null) {
                        binding.activitySignUpEmailCircleimageview.setImageURI(data?.data)
                        imageUri = data?.data
                        /*
                        var uid = FirebaseAuth.getInstance().currentUser?.uid
                        var storageRef =
                            FirebaseStorage.getInstance().reference.child("userProfileImages")
                                .child(uid!!)
                        storageRef.putFile(imageUri!!)
                            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                                return@continueWithTask storageRef.downloadUrl
                            }.addOnSuccessListener { uri ->
                                var map = HashMap<String, Any>()
                                map["image"] = uri.toString()
                                FirebaseFirestore.getInstance().collection("profileImages")
                                    .document(uid).set(map)


                            }

                         */
                    }
                }

            }
        }

    }
}