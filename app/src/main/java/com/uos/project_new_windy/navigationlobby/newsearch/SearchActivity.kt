package com.uos.project_new_windy.navigationlobby.newsearch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySearchBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var categoryList: ArrayList<String> = arrayListOf()

    //1 파이어베이스에서 가져온 데이터 리스트 변수 모음
    var contentSellDataList: ArrayList<ContentSellDTO> = arrayListOf()
    var contentBuyDataList: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentNormalDataList: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentSellDataUidList: ArrayList<String> = arrayListOf()
    var contentBuyDataUidList: ArrayList<String> = arrayListOf()
    var contentNormalDataUidList: ArrayList<String> = arrayListOf()

    //2 정렬된 후의 데이터 리스트 변수 모음
    var contentSellResultList: ArrayList<ContentSellDTO> = arrayListOf()
    var contentBuyResultList: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentNormalResultList: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentSellResultUidList: ArrayList<String> = arrayListOf()
    var contentBuyResultUidList: ArrayList<String> = arrayListOf()
    var contentNormalResultUidList: ArrayList<String> = arrayListOf()

    //3 필터링 후의 데이터 리스트 변수 모음
    var contentSellData: ArrayList<ContentSellDTO> = arrayListOf()
    var contentSellUidData: ArrayList<String> = arrayListOf()
    var contentBuyData: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentBuyUidData: ArrayList<String> = arrayListOf()

    //검색 기준 TEXT
    var searchText: String? = null
    var searchMinCost: Long? = null
    var searchMaxCost: Long? = null

    //검색 기준 게시판
    var dataType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.activitysearch = this@SearchActivity

        //액티비티 시작하자마자 필터 선택 액티비티 실행
        startActivityForResult(Intent(binding.root.context, SearchFilterActivity::class.java), 1001)

        binding.activitySearchImagebuttonBack.setOnClickListener {
            finish()
        }

        binding.activitySearchTextviewNotice.setText("상단의 검색 버튼을 눌러 게시글을 검색해보세요.")

        binding.activitySearchImagebuttonOption.setOnClickListener {
            startActivityForResult(Intent(binding.root.context, SearchFilterActivity::class.java), 1001)

        }

        binding.activitySearchSearchview.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("끼에에에에에엨 ${query.toString()}")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()

                when(dataType){
                    "sell" ->{
                        sellDataFiltering(contentSellDataList, contentSellDataUidList)
                        binding.activitySearchTextviewNotice.setText("구매 게시판 조회 결과 입니다.")
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty())
                            binding.activitySearchTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                    "buy" ->{
                        buyDataFiltering(contentBuyDataList, contentBuyDataUidList)
                        binding.activitySearchTextviewNotice.setText("판매 게시판 조회 결과 입니다.")
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty())
                            binding.activitySearchTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                }
                return false
            }

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 1001) {

            if (resultCode == 1555) {

                dataType = data?.getStringExtra("dataType")

                when (data?.getStringExtra("dataType")) {
                    "sell" -> {
                        println("데이터 카테고리 가져오기 성공")
                        getData("sell")
                        categoryList = data?.getStringArrayListExtra("categoryList")
                        searchMinCost = data?.getLongExtra("minCost", 0)
                        searchMaxCost = data?.getLongExtra("maxCost", 0)
                        categoryList.forEach {
                            println(message = "카테고리 리스트 = $it")
                        }
                    }
                    "buy" -> {
                        println("데이터 카테고리 가져오기 성공")
                        getData("buy")
                        categoryList = data?.getStringArrayListExtra("categoryList")
                        categoryList.forEach {
                            println(message = "카테고리 리스트 = $it")
                        }
                    }
                    "normal" -> {
                        getData("normal")
                    }
                    "shopping" -> {
                        getData("shopping")
                    }
                }

            }
        }
    }

    fun initRecyclerViewAdapter(
        dataType: String,
        list: ArrayList<Any>,
        uidList: ArrayList<String>
    ) {
        binding.activitySearchRecycler.adapter =
            SearchRecyclerAdapter(binding.root.context, dataType, list, uidList)
        binding.activitySearchRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        (binding.activitySearchRecycler.adapter as SearchRecyclerAdapter).notifyDataSetChanged()
        println("어댑터 초기화 완료")
    }

    fun getData(dataType: String) {


        when (dataType) {
            "sell" -> {
                db.collection("contents").document(dataType).collection("data")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        contentSellDataList.clear()
                        contentSellDataUidList.clear()

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentSellDTO::class.java)
                            //System.out.println("데이터들 " + item.toString())
                            //거래완료 상품이 아니면 보여줌
                            if (item?.checkSellComplete == false) {
                                contentSellDataList.add(item!!)
                                //System.out.println("데이터들2" + contentSellDTO.toString())
                                contentSellDataUidList.add(snapshot.id)
                            }

                        }


                        //sellDataFiltering(contentSellDataList, contentSellDataUidList)
                    }
            }
            "buy" -> {
                db.collection("contents").document(dataType).collection("data")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        contentBuyDataList.clear()
                        contentBuyDataUidList.clear()

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentBuyDTO::class.java)
                            //System.out.println("데이터들 " + item.toString())
                            //거래완료 상품이 아니면 보여줌
                            contentBuyDataList.add(item!!)
                            //System.out.println("데이터들2" + contentSellDTO.toString())
                            contentBuyDataUidList.add(snapshot.id)

                            //buyDataFiltering(contentBuyDataList, contentBuyDataUidList)
                        }
                    }
            }
            "normal" -> {
                db.collection("contents").document(dataType).collection("data")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        contentNormalDataList.clear()
                        contentNormalDataUidList.clear()

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentNormalDTO::class.java)
                            //System.out.println("데이터들 " + item.toString())
                            //거래완료 상품이 아니면 보여줌
                            contentNormalDataList.add(item!!)
                            //System.out.println("데이터들2" + contentSellDTO.toString())
                            contentNormalDataUidList.add(snapshot.id)

                            normalDataFiltering(contentNormalDataList, contentNormalDataUidList)
                        }
                    }
            }
            "shopping" -> {

            }
        }
    }

    fun sellDataFiltering(datalist: ArrayList<ContentSellDTO>, dataUidList: ArrayList<String>) {

        contentSellResultList.clear()
        contentSellResultUidList.clear()
        contentSellData.clear()
        contentSellUidData.clear()
    /*
        categoryList.forEachIndexed { index, s ->
            datalist.forEachIndexed { index2, contentSellDTO ->
                if (categoryList[index].equals(datalist[index2]))
                {
                    println("중복됩니다아앙")
                    contentSellResultList.add(datalist[d])
                    contentSellResultUidList.add(dataUidList[d])
                }
            }
        }

     */
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

    }

    fun buyDataFiltering(datalist: ArrayList<ContentBuyDTO>, dataUidList: ArrayList<String>) {

        contentBuyResultList.clear()
        contentBuyResultUidList.clear()
        contentBuyData.clear()
        contentBuyUidData.clear()

        for (c in categoryList.indices) {
            for (d in datalist.indices) {
                if (datalist[c].categoryHash.equals(categoryList[c])) {
                    println("중복됩니다아앙")
                    contentBuyResultList.add(datalist[d])
                    contentBuyResultUidList.add(dataUidList[d])
                }
            }
        }

        for (c in contentBuyResultList.indices) {
            if (contentBuyResultList[c].explain?.contains(searchText.toString())!!) {
                    contentBuyData.add(contentBuyResultList[c])
                    contentBuyUidData.add(contentBuyResultUidList[c])
            }
        }
        println("어댑터 부착완료")
        initRecyclerViewAdapter("buy", contentBuyData as ArrayList<Any>, contentBuyUidData)
    }

    fun normalDataFiltering(datalist: ArrayList<ContentNormalDTO>, dataUidList: ArrayList<String>) {

    }

    fun backButton() {
        finish()
    }
}