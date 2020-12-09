package com.uos.project_new_windy.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.DialogProgressLoadingBinding
import com.uos.project_new_windy.databinding.DialogProgressLoadingVerifyPhoneBinding

class ProgressDialogLoadingVerifyPhone(context: Context) : Dialog(context) {
    lateinit var binding: DialogProgressLoadingVerifyPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_progress_loading_verify_phone,null,false)
        setContentView(binding.root)
    }
}