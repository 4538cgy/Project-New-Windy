package com.uos.project_new_windy.model.log

data class PhoneAuthLog (
    var uid : String ? = null,
    var timestamp : String ? = null,
    var serverTimestamp : Long ? = null,
    var log : String ? = null
)
