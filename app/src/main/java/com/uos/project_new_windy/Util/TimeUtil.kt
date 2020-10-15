package com.uos.project_new_windy.Util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    fun getTime(): String {

            return SimpleDateFormat("yy-MM-dd hh:mm:ss").format(Date(System.currentTimeMillis())).toString()

    }
}