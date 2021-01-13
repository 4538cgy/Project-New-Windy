package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.databinding.ActivitySignUpBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.model.log.PhoneAuthLog
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.policy.PolicyActivity
import com.uos.project_new_windy.util.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    //스피너에서 선택한 작물 종류
    var pickToSpinnerPlant: String? = null

    //파이어베이스 auth
    var mAuth: FirebaseAuth? = null

    val mCallback: OnVerificationStateChangedCallbacks? = null
    val mResendToken: ForceResendingToken? = null
    var phoneVerify: Boolean = false
    var policyAcceptCheck: Boolean = false
    var progressDialog: ProgressDialogLoadingVerifyPhone? = null

    /*
    카카오 지번 검색에서 가져온 데이터들 변수
     */
    var zipCode: String? = null
    var address: String? = null
    var building: String? = null

    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.activitysignup = this@SignUpActivity

        //로딩 초기화
        progressDialog = ProgressDialogLoadingVerifyPhone(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

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
        /*
        //activity_sign_up_spinner.adapter = spinnerAdapter
        binding.activitySignUpSpinner.adapter = spinnerAdapter


         */
        //sms 인증 요청
        /*
        activity_search_address_button_auth_to_phone.setOnClickListener {
            AutoRecieveThePhoneVerifyCode()
        }

         */

        binding.activitySignUpEdittextAddress.isEnabled = false
        binding.activitySignUpEdittextAddress.isClickable = false

        binding.activitySearchAddressButtonAuthToPhone.setOnClickListener {

            binding.activitySignUpEdittextPhonenumber.isClickable = false
            binding.activitySignUpEdittextPhonenumber.isFocusable = false
            binding.activitySignUpEdittextPhonenumber.isFocusableInTouchMode = false
            binding.activitySignUpEdittextPhonenumber.isEnabled = false
            binding.activitySearchAddressButtonAuthToPhone.isEnabled = false



            phoneVerify = true

            if (!ScamerPhoneNumberData().getExistPhoneNumber(binding.activitySignUpEdittextPhonenumber.text.toString())) {
                AutoRecieveThePhoneVerifyCode()
            } else {
                Toast.makeText(binding.root.context,
                    "가입이 제한된 핸드폰 번호입니다. \n 고객센터에 문의해주세요.",
                    Toast.LENGTH_LONG).show()
            }
        }
        //주소 요청
        /*
        activity_search_address_button_address.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }

         */

        binding.activitySearchAddressButtonAddress.setOnClickListener {
            startActivityForResult(Intent(this, SearchAddressActivity::class.java), 100)
        }

        //main으로 이동 - 저장안하고 다음에 회원 정보 입력하기
        /*
        activity_sign_up_button_later.setOnClickListener {
            startActivity(Intent(this,LobbyActivity::class.java))
            finish()
        }

         */


        /*
        binding.activitySignUpButtonLater.setOnClickListener {
            //현재는 테스트로 바로 로비로 이동
            startActivity(Intent(this,LobbyActivity::class.java))
            /*
            FirebaseAuth.getInstance().currentUser?.delete()
            signOut()

            finishAffinity()

             */

        }

         */

        //동의하고 회원 정보 입력하기

        binding.activitySignUpButtonAccept.setOnClickListener {
            if (phoneVerify) {
                if (binding.activitySignUpEdittextName.length() < 2) {
                    Toast.makeText(binding.root.context, "닉네임을 두 글자 이상 입력해주세요.", Toast.LENGTH_LONG)
                        .show()
                } else if (binding.activitySignUpEdittextDetailAddress.text.length < 5) {
                    Toast.makeText(binding.root.context, "주소를 입력해주세요.", Toast.LENGTH_LONG).show()

                } else {
                    saveData()
                }


            } else {
                Toast.makeText(this, "핸드폰 인증을 진행해주세요.", Toast.LENGTH_LONG).show()
            }

        }

        //이용약관 보러가기
        /*
        activity_sign_up_textview.setOnClickListener {
            startActivity(Intent(this,PolicyActivity::class.java))
        }

         */

        binding.activitySignUpTextview.setOnClickListener {
            startActivity(Intent(this, PolicyActivity::class.java))
            policyAcceptCheck = true
        }

        //프로필 이미지 추가 리스너
        /*
        activity_sign_up_circleimageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,
                UserFragment.PICK_PROFILE_FROM_ALBUM
            )
            Log.d(" 회원가입 액티비티" , "인텐트 시작")

        }


         */
         */

        binding.activitySignUpCircleimageview.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,
                UserFragment.PICK_PROFILE_FROM_ALBUM
            )
            Log.d(" 회원가입 액티비티", "인텐트 시작")
        }

    }


    //Open 된 주소 검색 Activity의 결과 반환
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

                    binding.activitySignUpEdittextAddress.setText(data!!.getStringExtra("address_arg1")
                        .toString() +
                            data!!.getStringExtra("address_arg2").toString() +
                            data!!.getStringExtra("address_arg3").toString())
                    zipCode = data!!.getStringExtra("address_arg1").toString()
                    address = data!!.getStringExtra("address_arg2").toString()
                    building = data!!.getStringExtra("address_arg3").toString()
                    binding.activitySearchAddressButtonAddress.isEnabled = false
                    binding.activitySignUpEdittextDetailAddressForm.visibility = View.VISIBLE


                }

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {

                    if (mAuth?.currentUser != null) {
                        binding.activitySignUpCircleimageview.setImageURI(data?.data)
                        var imageUri = data?.data
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
                    }
                }

            }
        }

    }


    //핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {

        progressDialog?.show()

        val phoneNumber = "+82" + binding.activitySignUpEdittextPhonenumber.text.toString()
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
                        "핸드폰 인증에 성공했습니다. \n 나머지 정보를 입력해주세요.",
                        Toast.LENGTH_LONG).show()
                    progressDialog?.dismiss()

                    binding.activitySignUpEdittextPhonenumber.isClickable = false
                    binding.activitySignUpEdittextPhonenumber.isFocusable = false
                    binding.activitySignUpEdittextPhonenumber.isFocusableInTouchMode = false
                    binding.activitySignUpEdittextPhonenumber.isEnabled = false
                    binding.activitySearchAddressButtonAuthToPhone.isEnabled = false
                    phoneVerify = true
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
                    log.uid = binding.activitySignUpEdittextPhonenumber.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패"+ it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }



                    progressDialog?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialog?.dismiss()

                    var log = PhoneAuthLog()
                    log.log = p0.toString()
                    log.serverTimestamp = System.currentTimeMillis()
                    log.uid = binding.activitySignUpEdittextPhonenumber.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패"+ it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }
                    UpdateView()
                }

            }
        )

    }


    //UI 변경 처리
    private fun UpdateView() {
        Toast.makeText(this, "인증에 실패하였습니다. \n 핸드폰 번호를 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show()
    }

    //스피너 ITEM 선택 리스너
    /*
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //pickToSpinnerPlant = activity_sign_up_spinner.selectedItem.toString()
        pickToSpinnerPlant = binding.activitySignUpSpinner.selectedItem.toString()
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

     */

    fun saveData() {
        var userModel = UserModel()

        //주소
        userModel.totalAddress = binding.activitySearchAddressButtonAddress.text.toString()
        //상세주소
        userModel.addressDetail = binding.activitySignUpEdittextDetailAddress.text.toString()

        //세부주소
        userModel.address = address
        userModel.building = building
        userModel.zipCode = zipCode

        //선호하는 작물
        userModel.favoriteCategory = pickToSpinnerPlant.toString()
        //핸드폰 번호
        userModel.phoneNumber = binding.activitySignUpEdittextPhonenumber.text.toString()
        //핸드폰 시간
        userModel.time = TimeUtil().getTime().toString()
        //서버 시간
        userModel.timeStamp = FieldValue.serverTimestamp()
        //유저 uid
        userModel.uid = FirebaseAuth.getInstance().currentUser?.uid
        //유저 닉네임
        userModel.userName = binding.activitySignUpEdittextName.text.toString()
        userModel.point = 100
        userModel.memberRating = "0"
        userModel.policyAccept = policyAcceptCheck

        FirebaseFirestore.getInstance().collection("userInfo").document("userData")
            .collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .document("accountInfo").set(userModel)
            .addOnSuccessListener {
                System.out.println("유저 정보 저장 성공")

                //회원 정보 저장
                SharedData.prefs.setString("userInfo", "yes")

                //메인으로 이동하게 startActivity 작성해주세요.
                startActivity(Intent(this, LobbyActivity::class.java))
                finish()
            }.addOnFailureListener {
                System.out.println("유저 정보 저장 실패")
            }
    }

    fun signOut() {

        var gac: GoogleApiClient? = null

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gac = GoogleApiClient.Builder(binding.root.context)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        gac?.connect()



        gac?.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                FirebaseAuth.getInstance().signOut()
                if (gac!!.isConnected()) {
                    Auth.GoogleSignInApi.signOut(gac).setResultCallback { status ->
                        if (status.isSuccess) {
                            Log.v("알림", "로그아웃 성공")
                            startActivity(Intent(binding.root?.context, SplashActivity::class.java))
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


}