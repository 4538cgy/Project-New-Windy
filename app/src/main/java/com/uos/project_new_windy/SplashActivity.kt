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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.databinding.ActivitySplashBinding
import com.uos.project_new_windy.util.FcmPush
import java.security.MessageDigest

class SplashActivity : AppCompatActivity() {


    lateinit var binding: ActivitySplashBinding
    lateinit var anim: Animation
    internal lateinit var intent : Intent
    private lateinit var appUdateManager : AppUpdateManager

    companion object{
        const val MY_REQUEST_CODE = 800
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.activitysplash = this@SplashActivity





        appUdateManager = AppUpdateManagerFactory.create(binding.root.context)
        val appUpdateManagerInfoTask = appUdateManager.appUpdateInfo

        appUpdateManagerInfoTask.addOnSuccessListener {
            appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                //Listen Success.

                appUdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    this,
                    MY_REQUEST_CODE
                )
            }
        }

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
            finish()
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MY_REQUEST_CODE){
            if (resultCode != RESULT_OK){
                MaterialAlertDialogBuilder(this)
                    .setPositiveButton("예"){
                        _, _ ->
                    }.setMessage("신규 버전이 있습니다. 업데이트가 필요합니다.")
                    .show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

        appUdateManager
            .appUpdateInfo
            .addOnSuccessListener {
                appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    appUdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                }
            }
    }

    override fun onStop() {
        super.onStop()
        //4538cgy@gmail.com UID 값 [ 너무 푸쉬를 많이 보내서 일시적으로 사용 중지 주석 풀지마세요! ]
        //FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi", "bye")
    }


}