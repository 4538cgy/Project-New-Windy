package com.uos.project_new_windy.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    fun getTime(): String {

            return SimpleDateFormat("yy-MM-dd hh:mm:ss").format(Date(System.currentTimeMillis())).toString()

    }
}