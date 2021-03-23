package com.uos.project_new_windy

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
import com.uos.project_new_windy.databinding.ActivityLobbyBinding
import com.uos.project_new_windy.databinding.ActivityLoginBinding
import com.uos.project_new_windy.model.log.PhoneAuthLog
import com.uos.project_new_windy.policy.PolicyActivity
import com.uos.project_new_windy.util.ProgressDialogLoadingVerifyPhone
import com.uos.project_new_windy.util.SharedData
import com.uos.project_new_windy.util.TimeUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth?= null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var progressDialog: ProgressDialogLoadingVerifyPhone? = null

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.activitylogin = this@LoginActivity

        auth = FirebaseAuth.getInstance()

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

        //로그인
        /*
        email_login_button.setOnClickListener {
            signinEmail()
        }

         */
        binding.emailLoginButton.setOnClickListener {
            signinPhone()
        }

        //구글 로그인
        /*
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

         */
        binding.googleSignInButton.setOnClickListener {
            googleLogin()
        }

        //네이버 로그인


        //회원가입
        /*
        activity_login_sign_up.setOnClickListener {

            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

         */

        binding.activityLoginSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpOnlyPhone::class.java))
            finish()
        }

        //비밀번호 찾기
        /*
        activity_find_password.setOnClickListener {
            //팝업으로 비밀번호 찾기 창 띄워 주기
        }

         */
        binding.activityFindPassword.setOnClickListener {
            startActivity(Intent(binding.root.context,FindPasswordActivity::class.java))
        }


        //이용약관
        /*
        activity_login_policy.setOnClickListener {
            //팝업에 리사이클러뷰나 리스트뷰 하나 꽂아넣고 이용약관 보여주기 기능 추가
        }

         */
        binding.activityLoginPolicy.setOnClickListener {
            startActivity(Intent(this,PolicyActivity::class.java))
        }

    }
    
    
    //자동 로그인 체크
    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
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

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                moveMainPage(task.result?.user)
            } else {
                //show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun signinPhone(){
        //핸드폰 자동인증 처리


            progressDialog?.show()

            val phoneNumber = "+82" + binding.emailEdittext.text.toString()
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
                            "핸드폰 인증에 성공했습니다.",
                            Toast.LENGTH_LONG).show()
                        progressDialog?.dismiss()




                        FirebaseAuth.getInstance().signInWithCredential(p0).addOnFailureListener {

                            var log = PhoneAuthLog()
                            log.log = p0.toString()
                            log.serverTimestamp = System.currentTimeMillis()
                            log.uid = binding.emailEdittext.text.toString()
                            log.timestamp = TimeUtil().getTime()
                            FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("LoginLog").document().set(log)
                                .addOnFailureListener {
                                    println("로그 저장 실패"+ it.toString())
                                }.addOnCompleteListener {
                                    println("로그 저장 성공"+ it.toString())
                                }
                        }.addOnCompleteListener {
                            startActivity(Intent(binding.root.context,LobbyActivity::class.java))
                            finish()

                            var log = PhoneAuthLog()
                            log.log = p0.toString()
                            log.serverTimestamp = System.currentTimeMillis()
                            log.uid = binding.emailEdittext.text.toString()
                            log.timestamp = TimeUtil().getTime()
                            FirebaseFirestore.getInstance().collection("Log").document("SuccessLog").collection("LoginLog").document().set(log)
                                .addOnFailureListener {
                                    println("로그 저장 실패"+ it.toString())
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
                            "핸드폰 인증에 실패했습니다..",
                            Toast.LENGTH_LONG).show()

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.emailEdittext.text.toString()
                        log.timestamp = TimeUtil().getTime()

                        FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                            .addOnFailureListener {
                                println("로그 저장 실패"+ it.toString())
                            }.addOnCompleteListener {
                                println("로그 저장 성공"+ it.toString())
                            }



                        progressDialog?.dismiss()
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(p0, p1)
                        Log.d("코드가 전송됨", p0.toString())
                    }

                    override fun onCodeAutoRetrievalTimeOut(p0: String) {
                        super.onCodeAutoRetrievalTimeOut(p0)
                        progressDialog?.dismiss()

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.emailEdittext.text.toString()
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
    /*
    fun signinEmail() {
        auth?.signInWithEmailAndPassword( binding.emailEdittext.text.toString()
            , binding.passwordEdittext.text.toString()

        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                moveMainPage(task.result?.user)
            } else {
                //show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

     */

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {

            FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .document("accountInfo")
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->


                    if (documentSnapshot!=null) {
                        if (documentSnapshot!!.exists()) {
                            SharedData.prefs.setString("userInfo", "yes")
                        } else {
                            SharedData.prefs.setString("userInfo", "no")
                        }

                        if (SharedData.prefs.getString("userInfo", "no").equals("yes")) {
                            SharedData.prefs.setString("emailVerify","yes")
                            startActivity(Intent(this, LobbyActivity::class.java))

                        } else {
                            SharedData.prefs.setString("emailVerify","yes")
                            startActivity(Intent(this, SignUpActivity::class.java))
                        }
                        finish()
                    }

                }


        }
    }


}