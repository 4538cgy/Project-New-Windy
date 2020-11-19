package com.uos.project_new_windy.model.contentdto

data class ContentSellDTO(
    //중고나라, 당근마켓 참고
    var data : String ? = null,
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    var uid : String ? = null,
    var commentCount : Int ? = null,
    var explain : String ? = null,
    var title : String ? = null,
    var time : String ? = null,
    var favoriteCount : Int ? = null,
    var userId : String ? = null

)