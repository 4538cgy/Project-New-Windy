package com.uos.project_new_windy.Model

import android.net.Uri

data class ContentDTO(

    var explain : String ? = null,
    var imageUrl : String ? = null,
    var imageUriList : ArrayList<Uri> ?= null,
    var userId : String ? = null,
    var timestamp : Long ? = null,
    var favoriteCount : Int = 0,
    var favorites: MutableMap<String,Boolean> = HashMap()

){
    data class Comment(

        var uid : String ? = null,
        var userId : String ? = null,
        var comment : String ? = null,
        var timestamp : Long ? = null

    )

}