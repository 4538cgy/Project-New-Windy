package com.uos.project_new_windy.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySettingBinding
import com.uos.project_new_windy.model.chatmodel.UserModel

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    init {

        var userModel = UserModel()

        firestore.collection("userInfo").document("userData").collection(auth.currentUser?.uid!!).document("accountInfo").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if(documentSnapshot != null)
            {
                userModel = documentSnapshot.toObject(UserModel::class.java)!!
            }
        }

        
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)
        binding.activitysetting = this@SettingActivity

        
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

    }
}