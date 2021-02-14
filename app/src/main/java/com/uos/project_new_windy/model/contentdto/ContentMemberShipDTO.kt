package com.uos.project_new_windy.model.contentdto

data class ContentMemberShipDTO(
    //중고나라, 당근마켓 참고
    //이미지 리스트
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    //유저 uid
    var uid : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = 0,
    //내용
    var explain : String ? = null,
    //제품 설명
    var productExplain : String ? = null,
    //시간
    var time : String ? = null,
    //밀리초
    var timeStamp : Long ? = null,
    //좋아요 갯수
    var favoriteCount : Int ? = 0,
    //유저 이메일주소
    var userId : String ? = null,
    //조회수
    var viewCount : Int ? = 0,
    //유저 닉네임
    var userNickName : String ? = null,

    //카테고리
    var category : String ? = null,
    //가격
    var cost : String ? = null,
    //주소
    var address : String ? = null,
    //핸드폰 번호
    var phoneNumber : String ? = null,
    //판매된 글 체크
    var checkSellComplete : Boolean = false,
    //판매자 주소
    var sellerAddress : String ? = null,

    //좋아요 누른사람
    var favorites: MutableMap<String,Boolean> = HashMap(),
    //비교 전용 cost
    var costInt : String ? = null,
    //조회한 사람 uid
    var viewers : MutableMap<String,Boolean> = HashMap()
) {
    data class Comment(

        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null,
        var time: String? = null

    )
}