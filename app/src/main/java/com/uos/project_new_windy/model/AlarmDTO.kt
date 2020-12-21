package com.uos.project_new_windy.model

data class AlarmDTO(
    var destinationUid : String ? = null,
    var userId : String ? = null,
    var uid : String ? = null,

    //0 : like alarm
    //1 : comment alarm
    //2 : follow alarm
    //3 : chat alarm
    //4 : follower data update alarm
    var kind : Int? = null,
    var message : String ? = null,
    var timestamp : Long ? = null,
    var localTimestamp : String ? = null,
    var postUid : String ? = null,
    var chatMessage : String ? = null

)