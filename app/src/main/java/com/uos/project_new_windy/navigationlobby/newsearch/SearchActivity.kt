package com.uos.project_new_windy.navigationlobby.newsearch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySearchBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO

class SearchActivity : AppCompatActivity() {

    lateinit var binding : ActivitySearchBinding
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    //파이어베이스에서 가져온 데이터 리스트 변수 모음
    var contentSellDataList: ArrayList<ContentSellDTO> = arrayListOf()
    var contentBuyDataList: ArrayList<ContentBuyDTO> = arrayListOf()
    var contentNormalDataList: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentSellDataUidList: ArrayList<String> = arrayListOf()
    var contentBuyDataUidList: ArrayList<String> = arrayListOf()
    var contentNormalDataUidList: ArrayList<String> = arrayListOf()

    //정렬된 후의 데이터 리스트 변수 모음
    var contentSellResultList: ArrayList<ContentSellDTO> = arrayListOf()
    var contentBuyResultList : ArrayList<ContentBuyDTO> = arrayListOf()
    var contentNormalResultList: ArrayList<ContentNormalDTO> = arrayListOf()
    var contentSellResultUidList : ArrayList<String> = arrayListOf()
    var contentBuyResultUidList : ArrayList<String> = arrayListOf()
    var contentNormalResultUidList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search)
        binding.activitysearch = this@SearchActivity

        //액티비티 시작하자마자 필터 선택 액티비티 실행
        startActivityForResult(Intent(binding.root.context,SearchActivity::class.java), 1001)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode.equals(1001))
        {
            if (resultCode.equals(Activity.RESULT_OK))
            {
                when(data?.getStringExtra("dataType")){
                    "sell" -> {
                        getData("sell")
                    }
                    "buy" -> {
                        getData("buy")
                    }
                    "normal" ->{
                        getData("normal")
                    }
                    "shopping" ->{
                        getData("shopping")
                    }
                }

            }
        }
    }

    fun getData(dataType: String){
        when(dataType){
            "sell" -> {
                db.collection("contents").document(dataType).collection("data").orderBy("timeStamp", Query.Direction.DESCENDING)
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
                    }
            }
            "buy" -> {
                db.collection("contents").document(dataType).collection("data").orderBy("timeStamp", Query.Direction.DESCENDING)
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


                        }
                    }
            }
            "normal" ->{
                db.collection("contents").document(dataType).collection("data").orderBy("timestamp", Query.Direction.DESCENDING)
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


                        }
                    }
            }
            "shopping" ->{

            }
        }
    }

    fun sellDataFiltering(datalist: ArrayList<ContentSellDTO>, dataUidList: ArrayList<String> ){

    }

    fun buyDataFiltering(datalist: ArrayList<ContentBuyDTO>, dataUidList: ArrayList<String>){

    }

    fun normalDataFiltering(datalist: ArrayList<ContentNormalDTO>, dataUidList:ArrayList<String>){

    }

    fun backButton(){
        finish()
    }
}