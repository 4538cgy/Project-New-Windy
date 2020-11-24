package com.uos.project_new_windy.model.contentdto

data class ContentSellDTO(
    //중고나라, 당근마켓 참고
    //이미지 리스트
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    //유저 uid
    var uid : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = null,
    //내용
    var explain : String ? = null,
    //시간
    var time : String ? = null,
    //좋아요 갯수
    var favoriteCount : Int ? = null,
    //유저 닉네임 혹은 이메일주소
    var userId : String ? = null,
    //카테고리
    var categoryHash: MutableMap<String,Boolean> = HashMap(),
    //가격
    var cost : Int ? = null,
    //가격 제시 여부
    var customCost : Boolean ? = null,
    //주소
    var address : String ? = null,
    //핸드폰 번호
    var phoneNumber : String ? = null,
    //좋아요 누른사람
    var favorites: MutableMap<String,Boolean> = HashMap()
)