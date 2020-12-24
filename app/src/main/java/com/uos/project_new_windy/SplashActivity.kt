package com.uos.project_new_windy

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.uos.project_new_windy.databinding.ActivitySplashBinding
import com.uos.project_new_windy.util.FcmPush
import java.security.MessageDigest

class SplashActivity : AppCompatActivity() {


    lateinit var binding: ActivitySplashBinding
    lateinit var anim: Animation
    internal lateinit var intent : Intent

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.activitysplash = this@SplashActivity

        anim = AnimationUtils.loadAnimation(binding.root.context, R.anim.alpha)
        anim.setAnimationListener(AlphaAnimationListener())

        //binding.activitySplashImageviewLogo.startAnimation(AnimationUtils.loadAnimation(binding.root.context, R.anim.alpha))
        binding.activitySplashImageviewLogo.startAnimation(anim)

       intent =  Intent(this, LoginActivity::class.java)


    }

    inner class AlphaAnimationListener : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            println(" 애니메이션 시작 ")
        }

        override fun onAnimationEnd(animation: Animation?) {
            println(" 애니메이션 종료 ")
            binding.root.context.startActivity(intent)
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    }


    override fun onStop() {
        super.onStop()
        //4538cgy@gmail.com UID 값 [ 너무 푸쉬를 많이 보내서 일시적으로 사용 중지 주석 풀지마세요! ]
        //FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi", "bye")
    }


}