package com.uos.project_new_windy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.uos.project_new_windy.util.SharedData
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth : FirebaseAuth?= null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        
        //구글 로그인 옵션 활성화
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //로그인

        email_login_button.setOnClickListener {
            signinEmail()
        }

        //구글 로그인
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

        //네이버 로그인


        //회원가입
        activity_login_sign_up.setOnClickListener {

            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }


        //비밀번호 찾기
        activity_find_password.setOnClickListener {
            //팝업으로 비밀번호 찾기 창 띄워 주기
        }


        //이용약관
        activity_login_policy.setOnClickListener {
            //팝업에 리사이클러뷰나 리스트뷰 하나 꽂아넣고 이용약관 보여주기 기능 추가
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

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
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

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            if (SharedData.prefs.getString("userInfo","no") == "yes") {
                startActivity(Intent(this, SignUpActivity::class.java))
            }else{
                startActivity(Intent(this,LobbyActivity::class.java))
            }
            finish()
        }
    }


}