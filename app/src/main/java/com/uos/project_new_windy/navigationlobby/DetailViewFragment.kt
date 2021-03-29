package com.uos.project_new_windy.navigationlobby

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.databinding.FragmentDetailBinding
import com.uos.project_new_windy.model.contentdto.*
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.*

class DetailViewFragment : Fragment() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var page = 1
    var lastVisible: Long? = null
    var counter = 0
    var boardCheck = 0 // 0 = normal , 1 = sell , 2 = buy , 3 = memberShip
    var sellDataList: ArrayList<ContentSellDTO> = arrayListOf()
    var sellDataUidList: ArrayList<String> = arrayListOf()
    var normalDataList: ArrayList<ContentNormalDTO> = arrayListOf()
    var normalDataUidList: ArrayList<String> = arrayListOf()
    var buyDataList: ArrayList<ContentBuyDTO> = arrayListOf()
    var buyDataUidList: ArrayList<String> = arrayListOf()
    var memberShipDataList : ArrayList<ContentMemberShipDTO> = arrayListOf()
    var memberShipDataUidList : ArrayList<String> = arrayListOf()
    var shopDataList : ArrayList<ContentShopDTO> = arrayListOf()
    var shopDataUidList : ArrayList<String> = arrayListOf()
    lateinit var binding: FragmentDetailBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)


        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid


        //전체 게시글 초기화
        getData("sell", page)
        setContentSellRecycler()
        boardCheck = 1
        page = 1




        binding.fragmentDetailButttonWrite.setOnClickListener {
            if (ContextCompat.checkSelfPermission(binding.root.context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {

                val bottomSheetDialog: BottomSheetDialogWriteCategory =
                    BottomSheetDialogWriteCategory()

                bottomSheetDialog.show(fragmentManager!!, "lol")

                //startActivity(Intent(this,AddContentActivity::class.java))
            }
        }


        binding.fragmentDetailTextviewBuys.setOnClickListener {
            //구매 게시글 리스트로 초기화
            //binding.fragmentDetailRecycler.adapter = BuyViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            buttonBackgroundChanger(2)
            //setContentBuyRecycler()
            boardCheck = 2
            page = 1
            getData("buy", page)
            setContentBuyRecycler()
        }

        binding.fragmentDetailTextviewSales.setOnClickListener {
            //판매 게시글 리스트로 초기화
            //binding.fragmentDetailRecycler.adapter = SellViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            buttonBackgroundChanger(3)
            boardCheck = 1
            page = 1

            getData("sell", page)
            setContentSellRecycler()
            sellDataUidList.forEach {
                println("판매 게시판 uid 리스트" + it.toString())
            }
        }

        binding.fragmentDetailTextviewAll.setOnClickListener {
            //전체 게시글 출력
            //binding.fragmentDetailRecycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            buttonBackgroundChanger(1)
            //setContentNormalRecycler()
            boardCheck = 0
            page = 1
            getData("normal", page)
            setContentNormalRecycler()
        }

        binding.fragmentDetailTextviewMembership.setOnClickListener {
            //제휴 게시글 출력
            buttonBackgroundChanger(4)
            boardCheck = 3
            page = 1
            getData("membership",page)
            setContentMemberShipRecycler()

        }

        binding.fragmentDetailTextviewShop.setOnClickListener {
            //쇼핑 게시판 출력
            buttonBackgroundChanger(5)
            boardCheck = 4
            page = 1
            getData("shop",page)
            setContentShopRecycler()
        }

        if (counter == 0) {


            binding.fragmentDetailRecycler.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                var j = (binding.fragmentDetailRecycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                println("page =" + page.toString() + " boardCheck" + boardCheck.toString())
                if(!v.canScrollVertically(1)){
                    counter = 1
                    when (boardCheck) {

                        //normal
                        0 -> {
                            pageController(j, "normal")
                        }
                        //sell
                        1 -> {
                            pageController(j, "sell")
                        }
                        //buy
                        2 -> {
                            pageController(j, "buy")
                        }
                        //제휴
                        3 -> {
                            pageController(j,"membership")
                        }
                        //쇼핑
                        4 -> {
                            pageController(j,"shop")
                        }
                    }
                }
            }
        }





        return binding.root
    }

    fun pageController(lastPosition: Int, boardType: String) {

        counter = 1
        page++
        getData(boardType, page)



        Toast.makeText(binding.root.context, "데이터를 추가로 가져옵니다.", Toast.LENGTH_LONG)
            .show()


    }

    fun getBuyData(page: Int) {
        firestore?.collection("contents")?.document("buy")?.collection("data")?.limit(25)
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.get()?.addOnSuccessListener { querySnapshot ->
                buyDataList.clear()
                buyDataUidList.clear()

                System.out.println("구매하기 데이터 가져오기 성공")
                if (querySnapshot == null)
                    return@addOnSuccessListener

                for (snapshot in querySnapshot!!.documents) {

                    var item = snapshot.toObject(ContentBuyDTO::class.java)

                    buyDataList.add(item!!)
                    buyDataUidList.add(snapshot.id)
                    lastVisible = buyDataList[buyDataList.size - 1].timeStamp as Long

                    binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                }


            }
        counter = 0
    }

    fun getNormalData(page: Int) {
        firestore?.collection("contents")?.document("normal")?.collection("data")?.limit(25)
            ?.orderBy("timestamp",
                Query.Direction.DESCENDING)
            ?.get()?.addOnSuccessListener { querySnapshot ->
                normalDataList.clear()
                normalDataUidList.clear()

                if (querySnapshot == null)
                    return@addOnSuccessListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentNormalDTO::class.java)
                    normalDataList.add(item!!)
                    normalDataUidList.add(snapshot.id)
                    lastVisible = normalDataList[normalDataList.size - 1].timestamp

                    binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                    println("데이터 uid 리스트" + normalDataUidList.toString())
                }



            }
        counter = 0
    }

    fun getSellData(page: Int) {

        firestore?.collection("contents")?.document("sell")?.collection("data")?.limit(25)
            ?.orderBy("timeStamp",Query.Direction.DESCENDING)?.get()
            ?.addOnSuccessListener {
                sellDataList.clear()
                sellDataUidList.clear()
                if (it == null) return@addOnSuccessListener

                for (snapshot in it.documents)
                {
                    var item = snapshot.toObject(ContentSellDTO::class.java)

                    if (item?.checkSellComplete == false){
                        sellDataList.add(item!!)
                        sellDataUidList.add(snapshot.id)

                        lastVisible = sellDataList[sellDataList.size-1].timeStamp
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                    }
                    println("데이터 uid 리스트" + sellDataUidList.toString())
                }

            }
        counter = 0
    }

    fun getShopData(page: Int) {

        firestore?.collection("contents")?.document("shop")?.collection("data")?.limit(25)
            ?.orderBy("timeStamp",Query.Direction.DESCENDING)?.get()
            ?.addOnSuccessListener {
                shopDataList.clear()
                shopDataUidList.clear()
                if (it == null) return@addOnSuccessListener

                for (snapshot in it.documents)
                {
                    var item = snapshot.toObject(ContentShopDTO::class.java)

                    if (item?.checkSellComplete == false){
                        shopDataList.add(item!!)
                        shopDataUidList.add(snapshot.id)

                        lastVisible = shopDataList[shopDataList.size-1].timeStamp
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                    }
                    println("데이터 uid 리스트" + shopDataUidList.toString())
                }

            }
        counter = 0
    }

    fun getMemberShipData(page: Int){
        firestore?.collection("contents")?.document("membership")?.collection("data")?.limit(25)
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                memberShipDataList.clear()
                memberShipDataUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentMemberShipDTO::class.java)
                    //sell
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        memberShipDataList.add(item!!)
                        memberShipDataUidList.add(snapshot.id)

                        lastVisible = memberShipDataList[memberShipDataList.size - 1].timeStamp

                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                    }
                    //println("데이터 uid 리스트" + memberShipDataUidList.toString())
                }
                //setContentSellRecycler()

            }
        counter = 0
    }

    fun getData(type: String, page: Int) {
        if (page == 1) {

            when (type) {
                //normal
                "normal" -> {
                    println("데이터를 추가로 불러옵니다.1")
                    getNormalData(page)
                }
                //sell
                "sell" -> {
                    println("데이터를 추가로 불러옵니다.2")
                    getSellData(page)
                }
                //buy
                "buy" -> {
                    println("데이터를 추가로 불러옵니다.3")
                    getBuyData(page)
                }
                //shop
                "shop" ->{
                    getShopData(page)
                }
                "membership" -> {
                    getMemberShipData(page)
                }
            }
        } else {
            println("데이터를 추가로 불러옵니다.2")
            getMoreData(type, page)
        }
    }

    fun getMoreData(type: String, page: Int) {

        when (type) {
            "sell" -> {

                println("마지막 아이템은 ? " + lastVisible)

                firestore?.collection("contents")?.document(type)?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)?.get()
                    ?.addOnSuccessListener { querySnapshot ->


                        if (querySnapshot == null)
                            return@addOnSuccessListener

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentSellDTO::class.java)

                            //거래완료 상품이 아니면 보여줌
                            if (item?.checkSellComplete == false) {
                                sellDataList.add(item!!)

                                sellDataUidList.add(snapshot.id)


                                println("snapshotidddddddddddddddd" + snapshot.id)
                            }
                        }
                        println("갸르르르르르르" + querySnapshot.documents[querySnapshot.size() - 1].id)
                        //lastVisible = querySnapshot.documents[querySnapshot.size()-1].id
                        lastVisible = sellDataList[sellDataList.size - 1].timeStamp
                        println("lastVisible =====" + lastVisible.toString())
                        //setContentSellRecycler()
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()


                        println("판매 게시판 게시글 갯수 " + sellDataList.size.toString() + "판매 게시판 uid 갯수" + sellDataUidList.size.toString())

                    }
                counter = 0
            }
            "shop" -> {

                println("마지막 아이템은 ? " + lastVisible)

                firestore?.collection("contents")?.document(type)?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)?.get()
                    ?.addOnSuccessListener { querySnapshot ->


                        if (querySnapshot == null)
                            return@addOnSuccessListener

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentShopDTO::class.java)

                            //거래완료 상품이 아니면 보여줌
                            if (item?.checkSellComplete == false) {
                                shopDataList.add(item!!)

                                shopDataUidList.add(snapshot.id)


                                println("snapshotidddddddddddddddd" + snapshot.id)
                            }
                        }

                        //lastVisible = querySnapshot.documents[querySnapshot.size()-1].id
                        lastVisible = shopDataList[shopDataList.size - 1].timeStamp
                        println("lastVisible =====" + lastVisible.toString())
                        //setContentSellRecycler()
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()




                    }
                counter = 0
            }
            "buy" -> {
                firestore?.collection("contents")?.document("buy")?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)
                    ?.get()?.addOnSuccessListener { querySnapshot ->


                        System.out.println("구매하기 데이터 가져오기 성공")
                        if (querySnapshot == null)
                            return@addOnSuccessListener

                        for (snapshot in querySnapshot!!.documents) {

                            var item = snapshot.toObject(ContentBuyDTO::class.java)

                            buyDataList.add(item!!)
                            buyDataUidList.add(snapshot.id)
                            lastVisible = buyDataList[buyDataList.size - 1].timeStamp as Long
                            //setContentBuyRecycler()
                            binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()

                        }

                    }
                counter = 0
            }
            "normal" -> {
                firestore?.collection("contents")?.document("normal")?.collection("data")
                    ?.orderBy("timestamp",
                        Query.Direction.DESCENDING)?.startAfter(lastVisible)?.limit(25)
                    ?.get()?.addOnSuccessListener { querySnapshot->
                        if (querySnapshot == null)
                            return@addOnSuccessListener

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentNormalDTO::class.java)
                            normalDataList.add(item!!)
                            normalDataUidList.add(snapshot.id)
                            lastVisible = normalDataList[normalDataList.size - 1].timestamp
                            //setContentNormalRecycler()
                            binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()

                        }

                        println("일반 게시판 게시글 갯수 " + normalDataList.size.toString() + "일반 게시판 uid 갯수" + normalDataUidList.size.toString())
                    }
                counter = 0
            }

            "membership" -> {
                firestore?.collection("contents")?.document(type)?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)
                    ?.get()?.addOnSuccessListener { querySnapshot ->


                        if (querySnapshot == null)
                            return@addOnSuccessListener

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentMemberShipDTO::class.java)

                            //거래완료 상품이 아니면 보여줌
                            if (item?.checkSellComplete == false) {
                                memberShipDataList.add(item!!)

                                memberShipDataUidList.add(snapshot.id)


                                println("snapshotidddddddddddddddd" + snapshot.id)
                            }
                        }
                        println("갸르르르르르르" + querySnapshot.documents[querySnapshot.size() - 1].id)
                        //lastVisible = querySnapshot.documents[querySnapshot.size()-1].id
                        lastVisible = memberShipDataList[memberShipDataList.size - 1].timeStamp
                        println("lastVisible =====" + lastVisible.toString())
                        //setContentSellRecycler()
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()


                        println("판매 게시판 게시글 갯수 " + memberShipDataList.size.toString() + "판매 게시판 uid 갯수" + memberShipDataUidList.size.toString())

                    }
                counter = 0
            }
        }

    }

    fun buttonBackgroundChanger(state: Int) {
        when (state) {
            1 -> {
                //normal
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_black)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewMembership.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewShop.setBackgroundResource(R.drawable.background_round_white)

                binding.fragmentDetailTextviewAll.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewMembership.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewShop.setTextColor(Color.BLACK)
            }
            2 -> {
                //buy
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_black)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewMembership.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewShop.setBackgroundResource(R.drawable.background_round_white)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewMembership.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewShop.setTextColor(Color.BLACK)

            }
            3 -> {
                //sell
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_black)
                binding.fragmentDetailTextviewMembership.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewShop.setBackgroundResource(R.drawable.background_round_white)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewMembership.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewShop.setTextColor(Color.BLACK)
            }

            4 -> {
                //memberShip
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewMembership.setBackgroundResource(R.drawable.background_round_black)
                binding.fragmentDetailTextviewShop.setBackgroundResource(R.drawable.background_round_white)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewMembership.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewShop.setTextColor(Color.BLACK)
            }

            5->{
                //shop
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewMembership.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewShop.setBackgroundResource(R.drawable.background_round_black)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewMembership.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewShop.setTextColor(Color.WHITE)
            }
        }
    }

    private fun setContentBuyRecycler() {
        val contentBuyViewRecyclerViewAdapter = ContentBuyRecyclerViewAdapter(binding.root.context,
            fragmentManager!!,
            buyDataList,
            buyDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentBuyViewRecyclerViewAdapter
        contentBuyViewRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun setContentMemberShipRecycler(){
        val contentMemberShipViewRecyclerViewAdapter = ContentMemberShipRecyclerViewAdapter(binding.root.context,
            fragmentManager!!,
            page,
            memberShipDataList,
            memberShipDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentMemberShipViewRecyclerViewAdapter
    }

    private fun setContentShopRecycler(){
        val contentShopViewRecyclerViewAdapter = ContentShopRecyclerViewAdapter(binding.root.context,
        fragmentManager!!,
        page,
        shopDataList,
        shopDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentShopViewRecyclerViewAdapter
    }

    private fun setContentSellRecycler() {
        val contentSellViewRecyclerViewAdapter =
            ContentSellRecyclerViewAdapter(binding.root.context,
                fragmentManager!!,
                page,
                sellDataList,
                sellDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentSellViewRecyclerViewAdapter
        //contentSellViewRecyclerViewAdapter.notifyDataSetChanged()
        println("아니 이게왜? 끼아아아아아아아" + page.toString())
        println("끼아아아아아아아" + page.toString())
        println("끼아아아아아아아" + page.toString())
        println("끼아아아아아아아" + page.toString())
        println("끼아아아아아아아" + page.toString())
    }

    private fun setContentNormalRecycler() {
        val contentNormalRecyclerViewAdapter =
            ContentNormalRecyclerViewAdapter(binding.root.context,
                fragmentManager!!,
                normalDataList,
                normalDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentNormalRecyclerViewAdapter

        contentNormalRecyclerViewAdapter.notifyDataSetChanged()

    }


}