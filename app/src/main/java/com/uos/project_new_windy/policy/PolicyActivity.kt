package com.uos.project_new_windy.policy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uos.project_new_windy.R
import kotlinx.android.synthetic.main.activity_policy.*

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        activity_policy_textview.text =
            " 1. 개인 정보 이용약관" + "\n" +
                    "2. 개인 정보 활용 동의 약관"

        activity_policy_button.setOnClickListener {
            finish()
        }
    }
}