package com.uos.project_new_windy.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    fun getTime(): String {

            return SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ss초").format(Date(System.currentTimeMillis())).toString()

    }
}