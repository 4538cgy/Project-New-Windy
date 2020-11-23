package com.uos.project_new_windy.navigationlobby.detailviewactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityAddBuyContentBinding
import com.uos.project_new_windy.databinding.ActivityDetailBuyViewBinding

class DetailBuyViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailBuyViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_buy_view)
        binding.activitydetailviewbuy = this@DetailBuyViewActivity
    }
}