package com.uos.project_new_windy.navigationlobby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding

class AddBuyContentActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddBuyContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_buy_content)
        binding.buycontent = this@AddBuyContentActivity

    }
}