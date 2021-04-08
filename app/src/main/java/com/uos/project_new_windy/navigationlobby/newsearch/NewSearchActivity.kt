package com.uos.project_new_windy.navigationlobby.newsearch

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityNewSearchBinding
import com.uos.project_new_windy.model.contentdto.ContentSellDTO

class NewSearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewSearchBinding

    var searchText : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_search)

        //최초 화면 초기화
        buttonChanger("category")

        //화면 끄기
        binding.activityNewSearchImagebuttonClose.setOnClickListener {
            finish()
        }

        //검색어 체크
        binding.activityNewSearchSearchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()
                return false
            }

        })

        //클릭 이벤트
        binding.activityNewSearchTextviewCategory.setOnClickListener { buttonChanger("category") }
        binding.activityNewSearchTextviewAddress.setOnClickListener { buttonChanger("address") }
        binding.activityNewSearchTextviewCost.setOnClickListener { buttonChanger("cost") }

        //클릭 이벤트
        binding.activityNewSearchTextviewSell.setOnClickListener { categoryViewButtonChanger("sell") }
        binding.activityNewSearchTextviewBuy.setOnClickListener { categoryViewButtonChanger("buy") }
        binding.activityNewSearchTextviewNormal.setOnClickListener { categoryViewButtonChanger("normal") }
        binding.activityNewSearchTextviewShop.setOnClickListener { categoryViewButtonChanger("shop") }

    }

    fun buttonChanger(button : String){
        when(button){
            "category" ->{
                binding.activityNewSearchTextviewCategory.setBackgroundResource(R.drawable.background_round_black)
                binding.activityNewSearchTextviewAddress.setBackgroundResource(R.drawable.background_round_white)
                binding.activityNewSearchTextviewCost.setBackgroundResource(R.drawable.background_round_white)

                binding.activityNewSearchTextviewCategory.setTextColor(Color.WHITE)
                binding.activityNewSearchTextviewAddress.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewCost.setTextColor(Color.BLACK)

                binding.activityNewSearchConstCategory.visibility = View.VISIBLE
                binding.activityNewSearchConstAddress.visibility = View.GONE
                binding.activityNewSearchConstCost.visibility = View.GONE

            }
            "address" ->{
                binding.activityNewSearchTextviewCategory.setBackgroundResource(R.drawable.background_round_white)
                binding.activityNewSearchTextviewAddress.setBackgroundResource(R.drawable.background_round_black)
                binding.activityNewSearchTextviewCost.setBackgroundResource(R.drawable.background_round_white)

                binding.activityNewSearchTextviewCategory.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewAddress.setTextColor(Color.WHITE)
                binding.activityNewSearchTextviewCost.setTextColor(Color.BLACK)

                binding.activityNewSearchConstCategory.visibility = View.GONE
                binding.activityNewSearchConstAddress.visibility = View.VISIBLE
                binding.activityNewSearchConstCost.visibility = View.GONE
            }
            "cost" ->{
                binding.activityNewSearchTextviewCategory.setBackgroundResource(R.drawable.background_round_white)
                binding.activityNewSearchTextviewAddress.setBackgroundResource(R.drawable.background_round_white)
                binding.activityNewSearchTextviewCost.setBackgroundResource(R.drawable.background_round_black)

                binding.activityNewSearchTextviewCategory.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewAddress.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewCost.setTextColor(Color.WHITE)

                binding.activityNewSearchConstCategory.visibility = View.GONE
                binding.activityNewSearchConstAddress.visibility = View.GONE
                binding.activityNewSearchConstCost.visibility = View.VISIBLE
            }
        }
    }

    fun categoryViewButtonChanger(button : String){
        when(button) {
            "sell" ->{
                binding.activityNewSearchTextviewSell.setBackgroundResource(R.drawable.background_round_black)
                binding.activityNewSearchTextviewBuy.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewNormal.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewShop.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)

                binding.activityNewSearchTextviewSell.setTextColor(Color.WHITE)
                binding.activityNewSearchTextviewBuy.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewNormal.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewShop.setTextColor(Color.BLACK)
            }
            "buy" ->{
                binding.activityNewSearchTextviewSell.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewBuy.setBackgroundResource(R.drawable.background_round_black)
                binding.activityNewSearchTextviewNormal.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewShop.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)

                binding.activityNewSearchTextviewSell.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewBuy.setTextColor(Color.WHITE)
                binding.activityNewSearchTextviewNormal.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewShop.setTextColor(Color.BLACK)
            }
            "normal" ->{
                binding.activityNewSearchTextviewSell.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewBuy.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewNormal.setBackgroundResource(R.drawable.background_round_black)
                binding.activityNewSearchTextviewShop.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)

                binding.activityNewSearchTextviewSell.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewBuy.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewNormal.setTextColor(Color.WHITE)
                binding.activityNewSearchTextviewShop.setTextColor(Color.BLACK)
            }

            "shop" ->{
                binding.activityNewSearchTextviewSell.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewBuy.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewNormal.setBackgroundResource(R.drawable.background_white_stroke_gray_4dp)
                binding.activityNewSearchTextviewShop.setBackgroundResource(R.drawable.background_round_black)

                binding.activityNewSearchTextviewSell.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewBuy.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewNormal.setTextColor(Color.BLACK)
                binding.activityNewSearchTextviewShop.setTextColor(Color.WHITE)
            }
        }
    }

    fun initRecyclerViewAdapter(
        dataType:String,
        list: ArrayList<Any>,
        uidList : ArrayList<String>
    ){

    }

    fun getData(dataType : String){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001){
            if (resultCode == 1555){

            }
        }
    }

    fun sellDataFiltering(datalist: ArrayList<ContentSellDTO>, dataUidList: ArrayList<String>) {
        /*
        contentSellResultList.clear()
        contentSellResultUidList.clear()
        contentSellData.clear()
        contentSellUidData.clear()
        for(c in  datalist.indices){
            for(d in categoryList.indices){
                if (datalist[c].category.equals(categoryList[d].toString())){
                    contentSellResultList.add(datalist[c])
                    contentSellResultUidList.add(dataUidList[c])
                }
            }
        }






        for (c in contentSellResultList.indices) {
            if (contentSellResultList[c].explain?.contains(searchText.toString())!! || contentSellResultList[c].productExplain?.contains(
                    searchText.toString()
                )!!
            ) {
                if (contentSellResultList[c].costInt?.toLong()!! < searchMaxCost!! && contentSellResultList[c].costInt?.toLong()!! > searchMinCost!!) {
                    contentSellData.add(contentSellResultList[c])
                    contentSellUidData.add(contentSellResultUidList[c])
                }
            }
        }
        println("어댑터 부착완료")

        initRecyclerViewAdapter("sell", contentSellData as ArrayList<Any>, contentSellUidData)

         */

    }
}