package com.uos.project_new_windy.model.mallmodel

data class MallMainModel(
    var title : String ? =null
){
    data class Product(
        var title : String ? = null,
        var cost : Long ? = null,
        var salesCost : Long ? = null,
        var imageUrlList : ArrayList<String> ? = null,
        var thumbnailUrl : String ? = null,
        var review : MutableMap<String,Review> = HashMap(),
        var qna : MutableMap<String,Qna> = HashMap()
    ){
        data class Review(
            var imageUrlList: ArrayList<String>? = null,
            var comment : String ? = null,
            var timestamp : Long ? = null,
            var rating : Long? = null,
            var commentUid : String ? = null
        )
        data class Qna(
            var comment : String ? = null,
            var timestamp: Long? = null,
            var commentUid: String? = null
        )
    }

}