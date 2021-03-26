package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.uos.project_new_windy.databinding.ActivitySignUpOnlyPhoneBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.model.log.PhoneAuthLog
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.policy.PolicyActivity
import com.uos.project_new_windy.util.*
import java.util.concurrent.TimeUnit

class SignUpOnlyPhone : AppCompatActivity() {

    lateinit var binding : ActivitySignUpOnlyPhoneBinding

    //갤러리에서 가져온 사진 정보 저장 변수
    var imageUri : Uri? = null

    var policyAcceptCheck = false

    var mAuth = FirebaseAuth.getInstance()

    var progressDialogPhoneVerify: ProgressDialogLoadingVerifyPhone? = null
    var progressSignUpProcess : ProgressDialogLoadingPost? = null


    /*
카카오 지번 검색에서 가져온 데이터들 변수
 */
    var zipCode: String? = null
    var address: String? = null
    var building: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_only_phone)
        binding.activitysignuponlyphone = this@SignUpOnlyPhone


        //로딩 초기화
        progressDialogPhoneVerify = ProgressDialogLoadingVerifyPhone(binding.root.context)
        progressSignUpProcess = ProgressDialogLoadingPost(binding.root.context)
        //프로그레스 투명하게
        progressDialogPhoneVerify!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressSignUpProcess!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        progressDialogPhoneVerify!!.setCancelable(false)
        progressSignUpProcess!!.setCancelable(false)


        //주소 에딧텍스트 터치방지
        binding.activitySignUpOnlyPhoneEdittextAddress.isEnabled = false
        binding.activitySignUpOnlyPhoneEdittextAddress.setOnClickListener {
            Toast.makeText(binding.root.context,"주소 검색 버튼을 눌러주세요.",Toast.LENGTH_SHORT).show()
        }
        //주소 검색
        binding.activitySignUpOnlyPhoneButtonSearchAddress.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }


        //이용약관
        binding.activitySignUpOnlyPhoneTextviewPolicy.setOnClickListener {
            startActivity(Intent(this, PolicyActivity::class.java))
            policyAcceptCheck = true
        }



        //프로필 이미지 가져오기
        binding.activitySignUpOnlyPhoneCircleimageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,
                UserFragment.PICK_PROFILE_FROM_ALBUM
            )
        }

        //회원 가입 완료
        binding.activitySignUpOnlyPhoneButtonAccept.setOnClickListener {

            println("회원 가입 버튼 클릭")
            //빈칸 다 채웠는지 확인 후 사진 부터 업로드
            if (binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.contains("-"))
            {
                Toast.makeText(binding.root.context, "핸드폰 번호는 숫자만 입력해주세요." ,Toast.LENGTH_LONG).show()
            }else if(binding.activitySignUpOnlyPhoneEdittextNickname.text.length < 2){
                Toast.makeText(binding.root.context, "별명은 두글자 이상으로 입력해주세요." ,Toast.LENGTH_LONG).show()
            }else if(imageUri == null) {
                Toast.makeText(binding.root.context, "프로필 이미지를 넣어주세요.", Toast.LENGTH_LONG).show()

            }else if(binding.activitySignUpOnlyPhoneEdittextAddress.text.length < 2){
                Toast.makeText(binding.root.context, "주소 검색 버튼을 눌러 주소를 입력해주세요." ,Toast.LENGTH_LONG).show()

            }else if (binding.activitySignUpOnlyPhoneEdittextDetailAddress.text.length < 2){
                Toast.makeText(binding.root.context, "상세주소를 5글자 이상 입력해주세요." ,Toast.LENGTH_LONG).show()
            }

            else {
                progressSignUpProcess?.show()
                if (!ScamerPhoneNumberData().getExistPhoneNumber(binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString())) {
                    println("사기꾼 조회 완료료")
                    AutoRecieveThePhoneVerifyCode()
                } else {
                    Toast.makeText(binding.root.context,
                        "가입이 제한된 핸드폰 번호입니다. \n 고객센터에 문의해주세요.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    //핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {

        progressDialogPhoneVerify?.show()
        println("핸드폰 자동 인증 시작")
        val phoneNumber = "+82" + binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
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
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //성공시
                    Log.d("credential", p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 번호 인증에 성공했습니다.",
                        Toast.LENGTH_LONG).show()
                    progressDialogPhoneVerify?.dismiss()

                    binding.activitySignUpOnlyPhoneEdittextPhonenumber.isClickable = false
                    binding.activitySignUpOnlyPhoneEdittextPhonenumber.isFocusable = false
                    binding.activitySignUpOnlyPhoneEdittextPhonenumber.isFocusableInTouchMode = false
                    binding.activitySignUpOnlyPhoneEdittextPhonenumber.isEnabled = false



                    FirebaseAuth.getInstance().signInWithCredential(p0).addOnCompleteListener {

                        Toast.makeText(binding.root.context,"회원가입 성공",Toast.LENGTH_LONG).show()

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
                        log.timestamp = TimeUtil().getTime()

                        FirebaseFirestore.getInstance().collection("Log").document("SuccessLog").collection("SignInLog").document().set(log)
                            .addOnFailureListener {
                                println("로그 저장 실패" + it.toString())
                            }.addOnCompleteListener {
                                println("로그 저장 성공"+ it.toString())
                            }
                        //회원 가입 성공시 회원 정보 저장
                        uploadPhoto()

                    }.addOnFailureListener {
                        Toast.makeText(binding.root.context,"회원가입 실패",Toast.LENGTH_LONG).show()

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
                        log.timestamp = TimeUtil().getTime()

                        FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("SignInLog").document().set(log)
                            .addOnFailureListener {
                                println("로그 저장 실패" + it.toString())
                            }.addOnCompleteListener {
                                println("로그 저장 성공"+ it.toString())
                            }

                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다. \n 올바른 번호를 입력해주세요.",
                        Toast.LENGTH_LONG).show()


                    var log = PhoneAuthLog()
                    log.log = p0.toString()
                    log.serverTimestamp = System.currentTimeMillis()
                    log.uid = binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패" + it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }


                    progressDialogPhoneVerify?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialogPhoneVerify?.dismiss()

                    var log = PhoneAuthLog()
                    log.log = p0.toString()
                    log.serverTimestamp = System.currentTimeMillis()
                    log.uid = binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패"+ it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }


                }

            }
        )

    }

    fun uploadPhoto(){
        println("프로필 사진 업로드 시작")
        //사진 저장
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var storageRef =
            FirebaseStorage.getInstance().reference.child("userProfileImages")
                .child(uid!!)
        storageRef.putFile(imageUri!!)
            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                println("프로필 사진 업로드 성공")
                var map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages")
                    .document(uid).set(map)

                //사진 저장 완료 후 DB에 유저 정보 저장
                userDataSave()

            }
    }


    fun userDataSave(){
        println("DB에 회원정보 저장 시작")
        var userModel = UserModel()

        //회원정보 DB에 저장
        //주소
        userModel.totalAddress = binding.activitySignUpOnlyPhoneEdittextAddress.text.toString()
        //상세주소
        userModel.addressDetail = binding.activitySignUpOnlyPhoneEdittextDetailAddress.text.toString()


        //세부주소
        userModel.address = address
        userModel.building = building
        userModel.zipCode = zipCode

        //핸드폰 번호
        userModel.phoneNumber = binding.activitySignUpOnlyPhoneEdittextPhonenumber.text.toString()
        //핸드폰 시간
        userModel.time = TimeUtil().getTime().toString()
        //서버 시간
        userModel.timeStamp = FieldValue.serverTimestamp()
        //유저 uid
        userModel.uid = FirebaseAuth.getInstance().currentUser?.uid
        //유저 닉네임
        userModel.userName = binding.activitySignUpOnlyPhoneEdittextNickname.text.toString()
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
                SharedData.prefs.setString("emailVerify","yes")
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

                    binding.activitySignUpOnlyPhoneEdittextAddress.setText(data!!.getStringExtra("address_arg1")
                        .toString() +
                            data!!.getStringExtra("address_arg2").toString() +
                            data!!.getStringExtra("address_arg3").toString())
                    zipCode = data!!.getStringExtra("address_arg1").toString()
                    address = data!!.getStringExtra("address_arg2").toString()
                    building = data!!.getStringExtra("address_arg3").toString()
                    binding.activitySignUpOnlyPhoneButtonSearchAddress.isEnabled = false


                }

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {

                    binding.activitySignUpOnlyPhoneCircleimageview.setImageURI(data?.data)
                    imageUri = data?.data

                    if (mAuth?.currentUser != null) {
                        binding.activitySignUpOnlyPhoneCircleimageview.setImageURI(data?.data)
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