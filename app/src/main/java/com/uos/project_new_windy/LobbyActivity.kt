package com.uos.project_new_windy

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.chat.ChatRoomList
import com.uos.project_new_windy.databinding.ActivityLobbyBinding
import com.uos.project_new_windy.navigationlobby.*
import com.uos.project_new_windy.navigationlobby.newsearch.NewSearchActivity
import com.uos.project_new_windy.navigationlobby.newsearch.SearchActivity
import com.uos.project_new_windy.util.FcmPush
import com.uos.project_new_windy.util.SharedData
import kotlinx.android.synthetic.main.activity_lobby.*

class LobbyActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var binding : ActivityLobbyBinding
    var auth = FirebaseAuth.getInstance()


    companion object{
        var progressDialog : AppCompatDialog ? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_lobby)
        binding.lobbyactivity = this@LobbyActivity

        MobileAds.initialize(this, "ca-app-pub-4748606502895716~1479121487");


        bottom_navigtaion.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)


        //최초 화면 초기화
        bottom_navigtaion.selectedItemId = R.id.action_home
        registerPushToken()




    }

    fun emailVerifyPage(){
        startActivity(Intent(binding.root.context,EmailVerifyActivity::class.java))

        finish()
    }

    override fun onStop() {
        super.onStop()
        System.out.println("아 로비 액티비티가 꺼진것이에오")
        //4538cgy@gmail.com UID 값 [ 너무 푸쉬를 많이 보내서 일시적으로 사용 중지 주석 풀지마세요! ]
        //FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi", "bye")
    }


    fun registerPushToken(){

        if(FirebaseAuth.getInstance().currentUser != null) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                val token = task.result?.token
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val map = mutableMapOf<String, Any>()
                map["pushToken"] = token!!
                FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
            }
        }

    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.action_home -> {
                var detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                return  true
            }
            R.id.action_search -> {


                //startActivity(Intent(binding.root.context,SearchActivity::class.java))
                startActivity(Intent(binding.root.context,NewSearchActivity::class.java))

            }
            R.id.action_photo -> {

                if(auth.currentUser != null) {
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                        val bottomSheetDialog : BottomSheetDialogWriteCategory = BottomSheetDialogWriteCategory()

                        bottomSheetDialog.show(supportFragmentManager,"lol")

                        //startActivity(Intent(this,AddContentActivity::class.java))
                    }
                }else{
                    var builder = AlertDialog.Builder(binding.root.context)


                    builder.apply {
                        setMessage("글을 작성하실 수 없습니다. \n 로그인 페이지로 이동하시겠습니까?")
                        setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(binding.root.context,LoginActivity::class.java))
                            finishAffinity()
                        })
                        setNegativeButton("아니요" , DialogInterface.OnClickListener { dialog, which ->
                            return@OnClickListener

                        })
                        setTitle("안내")
                        show()
                    }
                }

                return true
            }
            R.id.action_favorite_alarm -> {
                /*
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true

                 */

                if(auth.currentUser != null) {

                    startActivity(Intent(binding.root.context,ChatRoomList::class.java))
                }else{
                    var builder = AlertDialog.Builder(binding.root.context)


                    builder.apply {
                        setMessage("채팅을 이용하실 수 없습니다. \n로그인 페이지로 이동하시겠습니까?")
                        setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(binding.root.context,LoginActivity::class.java))
                            finishAffinity()
                        })
                        setNegativeButton("아니요" , DialogInterface.OnClickListener { dialog, which ->
                            return@OnClickListener

                        })
                        setTitle("안내")
                        show()
                    }
                }
            }
            R.id.action_account -> {

                if(auth.currentUser != null) {
                    var userFragment = UserFragment()
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid",uid)
                    userFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                }else{
                    var builder = AlertDialog.Builder(binding.root.context)


                    builder.apply {
                        setMessage("비로그인 이용자는 회원 정보를 확인하실 수 없습니다. \n로그인 페이지로 이동하시겠습니까?")
                        setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                            startActivity(Intent(binding.root.context,LoginActivity::class.java))
                            finishAffinity()
                        })
                        setNegativeButton("아니요" , DialogInterface.OnClickListener { dialog, which ->
                            return@OnClickListener

                        })
                        setTitle("안내")
                        show()
                    }
                }


                return true
            }

        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1234){
            System.out.println("데이터 전달 성공적으로 완수1234")
        }

        if(resultCode == 1555)
            System.out.println("데이터 전달 성공적으로 완수1666")




        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){



            progressDialog = AppCompatDialog(this)
            progressDialog!!.setTitle("프로필 이미지를 저장 중입니다. 잠시만 기다려주세요.")
            progressDialog!!.show()

            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)


            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        var builder = AlertDialog.Builder(binding.root.context)


        builder.apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                finishAffinity()
            })
            setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                return@OnClickListener

            })
            setTitle("안내")
            show()
        }





    }


}