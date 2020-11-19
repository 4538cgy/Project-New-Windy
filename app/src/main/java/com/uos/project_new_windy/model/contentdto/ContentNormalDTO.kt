package com.uos.project_new_windy.model.contentdto

data class ContentNormalDTO(
    //제목 - 지워질 예정
    var title : String ? = null,
    //내용
    var explain : String ? = null,
    //게시글 종류 - 지워질 예정
    var postCategory : String ? = null,
    //이미지 목록
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    //이미지 - 지워질 예정
    val imageUrl : String ? = null,
    //회원 uid
    var uid : String ? = null,
    //회원 닉네임 혹은 이메일
    var userId : String ? = null,
    //게시 시간
    var timestamp : Long ? = null,
    // ?
    var time : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = null,
    //좋아요 갯수
    var favoriteCount : Int ? = null,
    //좋아요 누른 사람 uid
    var favorites: MutableMap<String,Boolean> = HashMap(),
    //게시글 uid
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