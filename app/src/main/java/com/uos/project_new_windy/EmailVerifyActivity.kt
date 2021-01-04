package com.uos.project_new_windy

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
import com.uos.project_new_windy.databinding.ActivityEmailVerifyActvitiyBinding

class EmailVerifyActivity : AppCompatActivity() {

    lateinit var binding : ActivityEmailVerifyActvitiyBinding
    var gac : GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_email_verify_actvitiy)
        binding.activityemailverifyactivity = this@EmailVerifyActivity

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gac = GoogleApiClient.Builder(binding.root.context)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()

        binding.activityEmailVerifyButtonSend.setOnClickListener {


            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful){
                        Toast.makeText(binding.root.context, "이메일 발송 완료 \n 이메일을 확인해주세요.",Toast.LENGTH_LONG).show()

                    }
                }.addOnFailureListener { 
                    println("오류")
                }
        }

        binding.activityEmailVerifyButtonLogout.setOnClickListener {
            startActivity(Intent(binding.root.context,LoginActivity::class.java))
            signOut()
            finish()
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
}