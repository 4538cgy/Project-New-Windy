package com.uos.project_new_windy

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.uos.project_new_windy.util.FcmPush
import java.security.MessageDigest

class SplashActivity : AppCompatActivity() {





    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startActivity(Intent(this,LoginActivity::class.java))


    }


    override fun onStop() {
        super.onStop()
        //4538cgy@gmail.com UID 값 [ 너무 푸쉬를 많이 보내서 일시적으로 사용 중지 주석 풀지마세요! ]
        //FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi", "bye")
    }


}