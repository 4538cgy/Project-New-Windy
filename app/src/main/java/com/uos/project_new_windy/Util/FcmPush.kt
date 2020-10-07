package com.uos.project_new_windy.Util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.okhttp.*
import com.uos.project_new_windy.Model.PushDTO
import java.io.IOException

class FcmPush {

    var JSON = MediaType.parse("application/json; charset = uft-8")
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAA7Ab2fjw:APA91bFnYK2gkTXG-XFNqccMkPAvNHGtAWp2CGxBAMbsP9AWYyiSbtAQvYmZEkUYhg-zDgd2qc_jCBkWmYgUBO0ST2l0o5QwkneeBIQ8GDU9fD-QrKp14dWVhU7HlthDT-hgiSlTkAto"
    var gson : Gson?=null
    var okHttpClient : OkHttpClient? =null

    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid : String, title : String, message : String){

        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {

                task ->
            if (task.isSuccessful) {
                var token = task?.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
                var request = Request.Builder()
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization","key="+serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(request: Request?, e: IOException?) {

                    }

                    override fun onResponse(response: Response?) {
                        println(response?.body()?.string())
                    }


                })
            }
        }

    }

}