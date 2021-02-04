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
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentBuyRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentNormalRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentSellRecyclerViewAdapter

class DetailViewFragment : Fragment() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var page = 1
    var lastVisible: Long? = null
    var counter = 0
    var boardCheck = 0 // 0 = normal , 1 = sell , 2 = buy
    var sellDataList: ArrayList<ContentSellDTO> = arrayListOf()
    var sellDataUidList: ArrayList<String> = arrayListOf()
    var normalDataList: ArrayList<ContentNormalDTO> = arrayListOf()
    var normalDataUidList: ArrayList<String> = arrayListOf()
    var buyDataList: ArrayList<ContentBuyDTO> = arrayListOf()
    var buyDataUidList: ArrayList<String> = arrayListOf()

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
        getData("normal", page)

        /*
        binding.fragmentDetailRecycler.adapter = ContentNormalRecyclerViewAdapter(binding.root.context,fragmentManager!!)
        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)

         */


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
        }

        binding.fragmentDetailTextviewSales.setOnClickListener {
            //판매 게시글 리스트로 초기화
            //binding.fragmentDetailRecycler.adapter = SellViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            buttonBackgroundChanger(3)
            boardCheck = 1
            page = 1
            getData("sell", page)

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
        }

        binding.fragmentDetailRecycler.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            var j =
                (binding.fragmentDetailRecycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            println("page =" + page.toString() + " boardCheck" + boardCheck.toString())

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
            }


        }





        return binding.root
    }

    fun pageController(lastPosition: Int, boardType: String) {
        if (((binding.fragmentDetailRecycler.layoutManager as LinearLayoutManager).itemCount % 25) == 0) {
            if ((binding.fragmentDetailRecycler.layoutManager as LinearLayoutManager).itemCount == lastPosition + 1) {

                if (counter == 0) {
                    getData(boardType, page)
                    page++
                    counter = 1
                    Toast.makeText(binding.root.context, "데이터를 추가로 가져옵니다.", Toast.LENGTH_LONG)
                        .show()

                }

            }
        }
    }

    fun getBuyData(page: Int) {
        firestore?.collection("contents")?.document("buy")?.collection("data")
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.limit(25)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                buyDataList.clear()
                buyDataUidList.clear()

                System.out.println("구매하기 데이터 가져오기 성공")
                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {

                    var item = snapshot.toObject(ContentBuyDTO::class.java)

                    buyDataList.add(item!!)
                    buyDataUidList.add(snapshot.id)
                    lastVisible = buyDataList[buyDataList.size - 1].timeStamp as Long
                    setContentBuyRecycler()
                    binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                }
            }
    }

    fun getNormalData(page: Int) {
        firestore?.collection("contents")?.document("normal")?.collection("data")
            ?.orderBy("timestamp",
                Query.Direction.DESCENDING)?.limit(25)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                normalDataList.clear()
                normalDataUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentNormalDTO::class.java)
                    normalDataList.add(item!!)
                    normalDataUidList.add(snapshot.id)
                    lastVisible = normalDataList[normalDataList.size - 1].timestamp
                    setContentNormalRecycler()
                    binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                }


            }
    }

    fun getSellData(page: Int) {
        firestore?.collection("contents")?.document("sell")?.collection("data")?.limit(25)
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                sellDataList.clear()
                sellDataUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    //sell
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        sellDataList.add(item!!)

                        sellDataUidList.add(snapshot.id)

                        lastVisible = sellDataList[sellDataList.size - 1].timeStamp
                        setContentSellRecycler()
                        binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                    }
                }
            }
    }

    fun getData(type: String, page: Int) {
        if (page == 1) {

            when (type) {
                //normal
                "normal" -> {
                    getNormalData(page)
                }
                //sell
                "sell" -> {
                    getSellData(page)
                }
                //buy
                "buy" -> {
                    getBuyData(page)
                }
            }
        } else {
            getMoreData(type, page)
        }
    }

    fun getMoreData(type: String, page: Int) {

        when (type) {
            "sell" -> {
                firestore?.collection("contents")?.document(type)?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)
                    ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->


                        if (querySnapshot == null)
                            return@addSnapshotListener

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
                        counter = 0

                    }
            }
            "buy" -> {
                firestore?.collection("contents")?.document("buy")?.collection("data")
                    ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.startAfter(lastVisible)
                    ?.limit(25)
                    ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->


                        System.out.println("구매하기 데이터 가져오기 성공")
                        if (querySnapshot == null)
                            return@addSnapshotListener

                        for (snapshot in querySnapshot!!.documents) {

                            var item = snapshot.toObject(ContentBuyDTO::class.java)

                            buyDataList.add(item!!)
                            buyDataUidList.add(snapshot.id)
                            lastVisible = buyDataList[buyDataList.size - 1].timeStamp as Long
                            setContentBuyRecycler()
                            binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                            counter = 0
                        }
                    }
            }
            "normal" -> {
                firestore?.collection("contents")?.document("normal")?.collection("data")
                    ?.orderBy("timestamp",
                        Query.Direction.DESCENDING)?.startAfter(lastVisible)?.limit(25)
                    ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (querySnapshot == null)
                            return@addSnapshotListener

                        for (snapshot in querySnapshot!!.documents) {
                            var item = snapshot.toObject(ContentNormalDTO::class.java)
                            normalDataList.add(item!!)
                            normalDataUidList.add(snapshot.id)
                            setContentNormalRecycler()
                            binding.fragmentDetailRecycler.adapter?.notifyDataSetChanged()
                            counter = 0
                        }


                    }
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

                binding.fragmentDetailTextviewAll.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)
            }
            2 -> {
                //buy
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_black)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_white)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.WHITE)
                binding.fragmentDetailTextviewSales.setTextColor(Color.BLACK)

            }
            3 -> {
                //sell
                binding.fragmentDetailTextviewAll.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewBuys.setBackgroundResource(R.drawable.background_round_white)
                binding.fragmentDetailTextviewSales.setBackgroundResource(R.drawable.background_round_black)

                binding.fragmentDetailTextviewAll.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewBuys.setTextColor(Color.BLACK)
                binding.fragmentDetailTextviewSales.setTextColor(Color.WHITE)
            }
        }
    }

    fun setContentBuyRecycler() {
        val contentBuyViewRecyclerViewAdapter = ContentBuyRecyclerViewAdapter(binding.root.context,
            fragmentManager!!,
            buyDataList,
            buyDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentBuyViewRecyclerViewAdapter
        contentBuyViewRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun setContentSellRecycler() {
        val contentSellViewRecyclerViewAdapter =
            ContentSellRecyclerViewAdapter(binding.root.context,
                fragmentManager!!,
                page,
                sellDataList,
                sellDataUidList)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentSellViewRecyclerViewAdapter
        //contentSellViewRecyclerViewAdapter.notifyDataSetChanged()
        println("끼아아아아아아아" + page.toString())
    }

    fun setContentNormalRecycler() {
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