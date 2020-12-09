package com.uos.project_new_windy.util

import android.app.Application
import com.google.android.gms.common.util.SharedPreferencesUtils

class SharedData : Application() {
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}