package com.uos.project_new_windy.model.chatmodel

data class ChatDTO(
    var message: String? = null,
    var uid: String? = null,
    var destinationUid: String? = null,
    var time: String? = null,
    var timeStamp: String? = null,
    var userId: String? = null
)