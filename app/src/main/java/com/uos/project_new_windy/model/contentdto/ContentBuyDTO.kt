package com.uos.project_new_windy.model.contentdto

data class ContentBuyDTO(
    var data : String ? = null,
    //이미지 목록
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    var uid : String ? = null,
    var commentCount : Int ? = null,
    var explain : String ? = null,
    var title : String ? = null,
    var time : String ? = null,
    var favoriteCount : Int ? = null,
    var userId : String ? = null

)