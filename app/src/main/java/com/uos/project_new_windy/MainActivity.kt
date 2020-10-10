package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.project_new_windy.Fragment.UserFragment
import com.uos.project_new_windy.Util.FcmPush
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        push_test_button.setOnClickListener {

            FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1","hello","bye")

        }

        registerPushToken()
    }


    fun registerPushToken(){

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                task ->
            val token = task.result?.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String,Any>()
            map["pushToken"] = token!!
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }

    }

    override fun onStop() {
        super.onStop()
        //4538cgy@gmail.com UID 값 [ 너무 푸쉬를 많이 보내서 일시적으로 사용 중지 주석 풀지마세요! ]
        FcmPush.instance.sendMessage("1XTFiOeUFTcK4J8vzqnfctCiC1h1", "hi", "bye")
    }

    //UserFragment의 AlbumPick 이벤트에서 가져온 사진정보를 스토리지에 업로드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK)
        {
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
}