package com.uos.project_new_windy.model.contentdto

data class ContentBuyDTO(
    //구매 요청글
    //이미지
    var imageUrl : String ? = null,
    //사용자 uid
    var uid : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = 0,
    //내용
    var explain : String ? = null,
    //시간
    var time : String ? = null,
    //좋아요 갯수
    var favoriteCount : Int  ? = 0,
    //유저 닉네임 혹은 이메일 주소
    var userId : String ? = null,
    //유저 닉네임
    var userNickName : String ? = null,
    //최소 가격
    var costMin : Int ? = 0,
    //최대 가격
    var costMax : Int ? = 0,
    //카테고리
    var categoryHash: String ? = null,
    //핸드폰 번호
    var phoneNumber : String ? = null,
    //서버 시간
    var timeStamp : Any ? = null,
    //비교 전용 cost
    var costInt : String ? = null,
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