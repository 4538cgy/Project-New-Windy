package com.uos.project_new_windy.navigationlobby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddSellContentBinding

class AddSellContentActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddSellContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_sell_content)
        binding.sellcontent = this@AddSellContentActivity
    }
}