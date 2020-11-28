package com.uos.project_new_windy.model.chatmodel

data class ChatDTO(
    /*
    var message: String? = null,
    var uid: String? = null,
    var destinationUid: String? = null,
    var time: String? = null,
    var timeStamp: String? = null,
    var userId: String? = null

     */

    var users: Map<String,Boolean> = HashMap(),
    var comments : Map<String,Comment> = HashMap()

) {
    data class Comment (
        var uid : String ? = null,
        var message : String ? = null,
        var timestamp : Object ? = null
    )
}


