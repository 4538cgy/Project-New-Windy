package com.uos.project_new_windy.model.chatmodel

data class UserModel(

    var userName : String ? = null,         //닉네임
    var phoneNumber : String ? = null,      //핸드폰 번호
    var totalAddress : String ? = null,          //전체 주소
    var address : String ? = null,            //주소
    var zipCode : String ? = null,          //우편번호
    var building : String ? = null,         //건물지번
    var addressDetail : String ? = null,    //상세주소
    var favoriteCategory : String ? = null, //좋아하는 농작물 
    var policyAccept : Boolean ? = false,   //정책 동의 정보
    var uid : String ? = null,               //uid
    var timeStamp : Any ? = null,            //가입 시간 서버기준
    var time : String ? = null,               //가입 시간 폰 기준
    var point : Int = 0,
    var memberRating : String ? = null,
    var email : String ? = null,
    var password : String ? = null

)