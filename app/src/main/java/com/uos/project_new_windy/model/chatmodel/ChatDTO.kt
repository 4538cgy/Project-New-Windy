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

    var users: MutableMap<String,Boolean> = HashMap(),
    var comments : MutableMap<String,Comment> = HashMap()

) {
    data class Comment (
        var uid : String ? = null,
        var message : String ? = null,
        var serverTimestamp : Any ? = null,
        var timestamp: Any? ? = null
    )
}


