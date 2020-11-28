package com.uos.project_new_windy.model.chatmodel

data class UserModel(

    var userName : String ? = null,         //닉네임
    var profileImageUrl : String ? = null,  //프로필 이미지
    var phoneNumber : String ? = null,      //핸드폰 번호
    var address : String ? = null,          //주소
    var favoriteCategory : String ? = null, //좋아하는 농작물 
    var policyAccept : Boolean ? = false,   //정책 동의 정보
    var uid : String ? = null,               //uid
    var timeStamp : Double = 0.0,            //가입 시간 서버기준
    var time : String ? = null               //가입 시간 폰 기준

)