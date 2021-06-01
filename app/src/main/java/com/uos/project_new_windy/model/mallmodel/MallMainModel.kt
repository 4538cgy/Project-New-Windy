package com.uos.project_new_windy.model.mallmodel

data class MallMainModel(
    var title: String? = null
) {
    data class CartDTO(
        var productCount: Long? = 0,
        var productId: MutableMap<String, Boolean> = HashMap()
    )
    data class OrderHistory(
        var timestamp: Long? = null,
        var orderUid: String? = null,
        var address: String? = null,
        var detailAddress: String? = null,
        var phoneNumber: String? = null,
        var deliverOption: String? = null,
        var cost: Long? = null,
        var productList : MutableMap<String,Product> = hashMapOf()
    )

    data class Product(
        var title: String? = null,
        var explain: String? = null,
        var cost: Long? = null,
        var salesCost: Long? = null,
        var timestamp: Long? = null,
        var imageUrlList: ArrayList<String>? = null,
        var thumbnailUrl: String? = null,
        var category : String? = null,
        var review: MutableMap<String, Review> = HashMap(),
        var qna: MutableMap<String, Qna> = HashMap(),
        var buyer: MutableMap<String, Boolean> = HashMap()
    ) {
        data class Review(
            var imageUrlList: ArrayList<String>? = null,
            var comment: String? = null,
            var timestamp: Long? = null,
            var rating: Long? = null,
            var commentUid: String? = null
        )

        data class Qna(
            var comment: String? = null,
            var timestamp: Long? = null,
            var commentUid: String? = null
        )


    }

}