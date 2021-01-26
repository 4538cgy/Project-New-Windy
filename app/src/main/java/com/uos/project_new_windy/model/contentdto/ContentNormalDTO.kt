package com.uos.project_new_windy.model.contentdto

data class ContentNormalDTO(

    //내용
    var explain : String ? = null,
    //이미지 목록
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    //회원 uid
    var uid : String ? = null,
    //회원 닉네임 혹은 이메일
    var userId : String ? = null,
    //게시 시간 [ 서버시간 ]
    var timestamp : Long ? = null,
    //게시 시간
    var time : String ? = null,
    //유저 닉네임
    var userNickName : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = 0,
    //좋아요 갯수
    var favoriteCount : Int ? = 0,
    //조회수
    var viewCount : Int ? = 0,
    //좋아요 누른 사람 uid
    var favorites: MutableMap<String,Boolean> = HashMap(),
    //조회한 사람 uid
    var viewers : MutableMap<String,Boolean> = HashMap()



){
    data class Comment(

        var uid : String ? = null,
        var userId : String ? = null,
        var comment : String ? = null,
        var timestamp : Long ? = null,
        var time : String ? = null

    )

}