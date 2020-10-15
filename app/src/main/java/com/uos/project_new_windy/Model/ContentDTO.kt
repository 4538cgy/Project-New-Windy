package com.uos.project_new_windy.Model

import android.net.Uri

data class ContentDTO(
    var title : String ? = null,
    var explain : String ? = null,
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    val imageUrl : String ? = null,
    var uid : String ? = null,
    var userId : String ? = null,
    var timestamp : Long ? = null,
    var time : String ? = null,
    var commentCount : Int ? = null,
    var favoriteCount : Int = 0,
    var favorites: MutableMap<String,Boolean> = HashMap()

){
    data class Comment(

        var uid : String ? = null,
        var userId : String ? = null,
        var comment : String ? = null,
        var timestamp : Long ? = null,
        var time : String ? = null

    )

}