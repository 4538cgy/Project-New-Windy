package com.uos.project_new_windy.model.contentdto

data class ContentNormalDTO(
    var title : String ? = null,
    var explain : String ? = null,
    var postCategory : String ? = null,
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    val imageUrl : String ? = null,
    var uid : String ? = null,
    var userId : String ? = null,
    var timestamp : Long ? = null,
    var time : String ? = null,
    var commentCount : Int ? = null,
    var favoriteCount : Int ? = null,
    var favorites: MutableMap<String,Boolean> = HashMap(),
    var postUid : String ? = null



){
    data class Comment(

        var uid : String ? = null,
        var userId : String ? = null,
        var comment : String ? = null,
        var timestamp : Long ? = null,
        var time : String ? = null

    )

}