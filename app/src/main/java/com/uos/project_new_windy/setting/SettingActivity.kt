package com.uos.project_new_windy.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySettingBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.util.SharedData

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var userModel = UserModel()

    init {



        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot != null)
            {
                userModel = documentSnapshot.toObject(UserModel::class.java)!!
                println("끼에ㅐ에에에에엙" + userModel.toString())

                //로그인 정보의 이메일
                binding.activitySettingTextviewEmail.text = auth.currentUser?.email.toString()

                //계정 정보의 이메일
                binding.activitySettingTextviewEmailaddress.text = auth.currentUser?.email.toString()

                //계정 정보의 닉네임
                binding.activitySettingTextviewUsernickname.text = userModel.userName.toString()

                //계정 정보의 주소
                binding.activitySettingTextviewAddress.text = userModel.address.toString()

                //계정 정보의 핸드폰 번호
                binding.activitySettingTextviewPhonenumber.text = userModel.phoneNumber.toString()

            }


        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)
        binding.activitysetting = this@SettingActivity

        //체크박스들 최초는 전부 체크된것으로 초기화
        binding.activitySettingSwitchNotice.isChecked = true
        binding.activitySettingSwitchMaketting.isChecked = true
        binding.activitySettingSwitchPush.isChecked = true

        //프로필 이미지
        firestore?.collection("profileImages")?.document(auth.currentUser?.uid!!)
            ?.get()?.addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    var url = task.result!!["image"]
                    Glide.with(binding.root.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(binding.activitySettingCircleimageviewProfile)
                }

            }


        //수정 버튼
        binding.activitySettingButtonEditUserInfo.setOnClickListener {

        }

        //푸쉬 전체 알람 끄기
        binding.activitySettingSwitchPush.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                Toast.makeText(binding.root.context , "푸쉬 알람이 켜졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushAll","on")
            }else{
                Toast.makeText(binding.root.context , "푸쉬 알람이 꺼졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushAll","off")
            }
        }
        
        //마케팅 광고 알람 끄기
        binding.activitySettingSwitchMaketting.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(binding.root.context , "광고 알람이 켜졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushAd","on")
            }else{
                Toast.makeText(binding.root.context , "광고 알람이 꺼졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushAd","off")

            }

        }

        //공지 알람 끄기
        binding.activitySettingSwitchNotice.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(binding.root.context , "공지 알람이 켜졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushNotice","on")
            }else{

                Toast.makeText(binding.root.context , "공지 알람이 꺼졌습니다." , Toast.LENGTH_SHORT).show()
                SharedData.prefs.setString("pushNotice","off")
            }
            
        }

    }
}