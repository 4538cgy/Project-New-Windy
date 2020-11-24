package com.uos.project_new_windy.model.contentdto

data class ContentBuyDTO(
    //구매 요청글
    //이미지
    var imageUrl : String ? = null,
    //사용자 uid
    var uid : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = null,
    //내용
    var explain : String ? = null,
    //시간
    var time : String ? = null,
    //좋아요 갯수
    var favoriteCount : Int ? = null,
    //유저 닉네임 혹은 이메일 주소
    var userId : String ? = null,
    //가격
    var cost : Int ? = null,
    //제시가격
    var costCustom : Boolean ? = null,
    //카테고리
    var categoryHash: MutableMap<String,Boolean> = HashMap(),
    //핸드폰 번호
    var phoneNumber : String ? = null,

)