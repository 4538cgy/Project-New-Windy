package com.uos.project_new_windy.navigationlobby.newsearch

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityNewSearchBinding
import com.uos.project_new_windy.databinding.ItemGridListAddressBinding
import com.uos.project_new_windy.databinding.ItemGridListCategoryBinding
import com.uos.project_new_windy.model.AddressModel
import com.uos.project_new_windy.model.CategoryDTO
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.model.contentdto.ContentShopDTO

class NewSearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewSearchBinding
    var categoryList: ArrayList<String> = arrayListOf()
    var categoryDTO: ArrayList<CategoryDTO> = arrayListOf()

    var addressList : ArrayList<String> = arrayListOf()
    var addressDTO: ArrayList<AddressModel> = arrayListOf()

    var minCost : Long = 0
    var maxCost : Long = 10000000

    var contentSellDataList : ArrayList<ContentSellDTO> = arrayListOf()
    var contentSellDataUidList : ArrayList<String> = arrayListOf()
    var contentBuyDataList : ArrayList<ContentBuyDTO> = arrayListOf()
    var contentBuyDataUidList : ArrayList<String> = arrayListOf()
    var contentNormalDataList : ArrayList<ContentNormalDTO> = arrayListOf()
    var contentNormalDataUidList : ArrayList<String> = arrayListOf()
    var contentShopDataList : ArrayList<ContentShopDTO> = arrayListOf()
    var contentShopDataUidList : ArrayList<String> = arrayListOf()


    var contentSellData : ArrayList<ContentSellDTO> = arrayListOf()
    var contentSellUidData : ArrayList<String> = arrayListOf()
    var contentBuyData : ArrayList<ContentBuyDTO> = arrayListOf()
    var contentBuyUidData : ArrayList<String> = arrayListOf()
    var contentShopData : ArrayList<ContentShopDTO> = arrayListOf()
    var contentShopUidData : ArrayList<String> = arrayListOf()
    var contentNormalData : ArrayList<ContentNormalDTO> = arrayListOf()
    var contentNormalUidData : ArrayList<String> = arrayListOf()

    var contentBuyResultList : ArrayList<ContentBuyDTO> = arrayListOf()
    var contentBuyResultUidList : ArrayList<String> = arrayListOf()
    var contentSellResultList : ArrayList<ContentSellDTO> = arrayListOf()
    var contentSellResultUidList : ArrayList<String> = arrayListOf()
    var contentShopResultList : ArrayList<ContentShopDTO> = arrayListOf()
    var contentShopResultUidList : ArrayList<String> = arrayListOf()
    var contentNormalResultList : ArrayList<ContentNormalDTO> = arrayListOf()
    var contentNormalResultUidList : ArrayList<String> = arrayListOf()

    val db = FirebaseFirestore.getInstance()


    var searchText : String ? = null

    var dataType : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_search)
        binding.activitynewsearch = this@NewSearchActivity
        //카테고리 초기 초기화
        initCategoryList()
        initAddressList()
        //최초 화면 초기화
        buttonChanger("category")
        dataType = "sell"
        getData("sell")


        //카테고리 어댑터
        binding.activityNewSearchRecyclerCategory.adapter = GridRecyclerAdapter(categoryDTO)
        binding.activityNewSearchRecyclerCategory.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)


        //지역 어댑터
        binding.activityNewSearchRecyclerAddress.adapter = GridAddressRecyclerAdapter(addressDTO)
        binding.activityNewSearchRecyclerAddress.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)



        //검색어 힌트
        binding.activityNewSearchSearchview.queryHint = "검색어를 입력해주세요."

        //화면 끄기
        binding.activityNewSearchImagebuttonClose.setOnClickListener {
            finish()
        }

        //검색어 체크
        binding.activityNewSearchSearchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("끼에에에에에엨 ${query.toString()}")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText.toString()
                when(dataType){
                    "sell" ->{
                        sellDataFiltering(contentSellDataList, contentSellDataUidList)
                        binding.activityNewSeachTextviewNotice.setText("판매 게시판 조회 결과 입니다.")
                        println("datatype ==" + dataType)
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty() && contentNormalData.isEmpty() && contentShopData.isEmpty())
                            binding.activityNewSeachTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                    "buy" ->{
                        buyDataFiltering(contentBuyDataList, contentBuyDataUidList)
                        binding.activityNewSeachTextviewNotice.setText("구매 게시판 조회 결과 입니다.")
                        println("datatype ==" + dataType)
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty() && contentNormalData.isEmpty() && contentShopData.isEmpty())
                            binding.activityNewSeachTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                    "normal" ->{
                        normalDataFiltering(contentNormalDataList,contentNormalDataUidList)
                        binding.activityNewSeachTextviewNotice.setText("자유 홍보 게시판 조회 결과 입니다.")
                        println("datatype ==" + dataType)
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty() && contentNormalData.isEmpty() && contentShopData.isEmpty())
                            binding.activityNewSeachTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                    "shop" ->{
                        shopDataFiltering(contentShopDataList,contentShopDataUidList)
                        //buyDataFiltering(contentShopDataList, contentShopDataUidList)
                        binding.activityNewSeachTextviewNotice.setText("쇼핑 게시판 조회 결과 입니다.")
                        println("datatype ==" + dataType)
                        if (contentSellData.isEmpty() && contentBuyData.isEmpty() && contentNormalData.isEmpty() && contentShopData.isEmpty())
                            binding.activityNewSeachTextviewNotice.setText("검색 결과가 없습니다...")
                    }
                }

                binding.activityNewSearchConstCategory.visibility = View.GONE
                binding.activityNewSearchConstAddress.visibility = View.GONE
                binding.activityNewSearchConstCost.visibility = View.GONE
                binding.activityNewSearchConstRecyclerBar.visibility = View.VISIBLE
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

    fun initRecyclerViewAdapter(
        dataType: String,
        list: ArrayList<Any>,
        uidList: ArrayList<String>
    ) {
        binding.activityNewSaerchRecyclerSearch.adapter =
            SearchRecyclerAdapter(binding.root.context, dataType, list, uidList)
        binding.activityNewSaerchRecyclerSearch.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        (binding.activityNewSaerchRecyclerSearch.adapter as SearchRecyclerAdapter).notifyDataSetChanged()
        println("어댑터 초기화 완료")
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
                binding.activityNewSearchConstRecyclerBar.visibility = View.GONE


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
                binding.activityNewSearchConstRecyclerBar.visibility = View.GONE

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
                binding.activityNewSearchConstRecyclerBar.visibility = View.GONE
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
                dataType = "sell"
                getData("sell")
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
                dataType = "buy"
                getData("buy")
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
                dataType = "normal"
                getData("normal")
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
                dataType = "shop"
                getData("shop")
            }
        }
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
                                //System.out.println("데이터들2" + contentSellDataList.toString())
                                contentSellDataUidList.add(snapshot.id)
                            }

                        }


                        sellDataFiltering(contentSellDataList, contentSellDataUidList)
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

                            //normalDataFiltering(contentNormalDataList, contentNormalDataUidList)
                        }
                    }
            }
            "shop" -> {
                db.collection("contents").document(dataType).collection("data")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        contentShopDataList.clear()
                        contentShopDataUidList.clear()

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentShopDTO::class.java)
                            System.out.println("데이터들 " + item.toString())
                            //거래완료 상품이 아니면 보여줌
                            contentShopDataList.add(item!!)
                            System.out.println("데이터들2" + contentShopDataList.toString())
                            contentShopDataUidList.add(snapshot.id)

                            //shopDataFiltering(contentShopDataList, contentShopDataUidList)
                        }
                    }
            }
        }
    }

    fun initCategoryList(){
        categoryDTO.add(CategoryDTO("농기계", true))
        categoryDTO.add(CategoryDTO("농산물", true))
        categoryDTO.add(CategoryDTO("축산물", true))
        categoryDTO.add(CategoryDTO("소모품", true))
        categoryDTO.add(CategoryDTO("농지", true))
        categoryDTO.add(CategoryDTO("가구", true))
        categoryDTO.add(CategoryDTO("기타", true))
        categoryDTO.add(CategoryDTO("축산자재",true))
        categoryDTO.add(CategoryDTO("농자재",true))
        categoryDTO.add(CategoryDTO("화물트럭",true))
        categoryDTO.add(CategoryDTO("산업기계",true))

        categoryList.add("농기계")
        categoryList.add("농산물")
        categoryList.add("축산물")
        categoryList.add("소모품")
        categoryList.add("농지")
        categoryList.add("가구")
        categoryList.add("기타")
        categoryList.add("축산자재")
        categoryList.add("농자재")
        categoryList.add("화물트럭")
        categoryList.add("산업기계")
    }

    fun initAddressList(){
        addressDTO.add(AddressModel("서울", true))
        addressDTO.add(AddressModel("대전", true))
        addressDTO.add(AddressModel("대구", true))
        addressDTO.add(AddressModel("부산", true))
        addressDTO.add(AddressModel("광주", true))


        addressList.add("서울")
        addressList.add("대전")
        addressList.add("대구")
        addressList.add("부산")
        addressList.add("광주")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001){
            if (resultCode == 1555){

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
                if (contentSellResultList[c].costInt?.toLong()!! < maxCost!! && contentSellResultList[c].costInt?.toLong()!! > minCost!!) {
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

        contentNormalResultList.clear()
        contentNormalResultUidList.clear()
        contentNormalData.clear()
        contentNormalUidData.clear()
        /*
        for (c in categoryList.indices) {
            for (d in datalist.indices) {
                if (datalist[c].categoryHash.equals(categoryList[c])) {
                    println("중복됩니다아앙")
                    contentNormalResultList.add(datalist[d])
                    contentNormalResultUidList.add(dataUidList[d])
                }
            }
        }

         */



        for (c in datalist.indices) {
            if (datalist[c].explain?.contains(searchText.toString())!!) {
                contentNormalData.add(datalist[c])
                contentNormalUidData.add(dataUidList[c])
            }
        }
        println("어댑터 부착완료")
        initRecyclerViewAdapter("normal", contentNormalData as ArrayList<Any>, contentNormalUidData)
    }

    fun shopDataFiltering(datalist: ArrayList<ContentShopDTO>, dataUidList: ArrayList<String>) {

        contentShopResultList.clear()
        contentShopResultUidList.clear()
        contentShopData.clear()
        contentShopUidData.clear()

        dataUidList.forEach {
            println("샵 데이터어어어어어어엌" + it.toString())
        }

        for(c in  datalist.indices){
            for(d in categoryList.indices){
                if (datalist[c].category.equals(categoryList[d].toString())){
                    contentShopResultList.add(datalist[c])
                    contentShopResultUidList.add(dataUidList[c])
                }
            }
        }

        for (c in contentShopResultList.indices) {
            if (contentShopResultList[c].explain?.contains(searchText.toString())!! || contentShopResultList[c].productExplain?.contains(
                    searchText.toString()
                )!!
            ) {
                if (contentShopResultList[c].costInt?.toLong()!! < maxCost!! && contentShopResultList[c].costInt?.toLong()!! > minCost!!) {
                    contentShopData.add(contentShopResultList[c])
                    contentShopUidData.add(contentShopResultUidList[c])
                }
            }
        }

        initRecyclerViewAdapter("shop", contentShopData as ArrayList<Any>, contentShopUidData)

    }

    inner class GridRecyclerAdapter(private val data : ArrayList<CategoryDTO>) : RecyclerView.Adapter<GridRecyclerAdapter.GridRecyclerViewAdapterViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridRecyclerViewAdapterViewHolder {
            val binding = ItemGridListCategoryBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)

            return GridRecyclerViewAdapterViewHolder(binding)
        }

        override fun getItemCount(): Int = categoryDTO.size

        override fun onBindViewHolder(holder: GridRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(categoryDTO[position])

            println("categorrrrrrrr" + categoryDTO.toString())
            //체크 박스를 클릭했을때
            holder.binding.itemGridListCheckbox.setOnClickListener {


                //해당 포지션이 체크되지 않았을 경우
                if (categoryDTO[position].check == false) {
                    //데이터 추가
                    categoryList.add(categoryDTO[position].title.toString())
                    categoryDTO[position].check = true

                    //해당 포지션이 체크되었을 경우
                } else {
                    //리스트를 조회해서 같은 이름을 가진 인덱스 삭제
                    categoryList.remove(categoryDTO[position].title.toString())

                    categoryDTO[position].check = false
                }


                categoryList.forEach {
                    System.out.println("리스트 = $it")
                }

            }
        }

        inner class GridRecyclerViewAdapterViewHolder(val binding: ItemGridListCategoryBinding) :
                RecyclerView.ViewHolder(binding.root){
            fun onBind(data : CategoryDTO){
                binding.itemgridlist = data
            }
        }

    }

    inner class GridAddressRecyclerAdapter(private val data : ArrayList<AddressModel>) : RecyclerView.Adapter<GridAddressRecyclerAdapter.GridAddressRecyclerViewAdapterViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAddressRecyclerViewAdapterViewHolder {
            val binding = ItemGridListAddressBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)

            return GridAddressRecyclerViewAdapterViewHolder(binding)
        }

        override fun getItemCount(): Int = addressDTO.size

        override fun onBindViewHolder(holder: GridAddressRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(addressDTO[position])

            println("addressssssss"  + addressDTO.toString())

            //체크 박스를 클릭했을때
            holder.binding.itemGridListAddressCheckbox.setOnClickListener {


                //해당 포지션이 체크되지 않았을 경우
                if (addressDTO[position].check == false) {
                    //데이터 추가
                    addressList.add(addressDTO[position].title.toString())
                    addressDTO[position].check = true

                    //해당 포지션이 체크되었을 경우
                } else {
                    //리스트를 조회해서 같은 이름을 가진 인덱스 삭제
                    addressList.remove(addressDTO[position].title.toString())

                    addressDTO[position].check = false
                }


                addressList.forEach {
                    System.out.println("리스트 = $it")
                }

            }
        }

        inner class GridAddressRecyclerViewAdapterViewHolder(val binding: ItemGridListAddressBinding) :
            RecyclerView.ViewHolder(binding.root){
            fun onBind(data : AddressModel){
                binding.itemgridlistaddress = data
            }
        }

    }
}