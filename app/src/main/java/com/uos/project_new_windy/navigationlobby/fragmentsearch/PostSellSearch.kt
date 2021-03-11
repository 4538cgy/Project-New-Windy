package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentPostSellSearchBinding
import com.uos.project_new_windy.databinding.ItemPostSellSearchResultBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import com.uos.project_new_windy.navigationlobby.fragmentsearch.categoryselectactivity.PostSellSearchCategorySetActivity


class PostSellSearch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentPostSellSearchBinding

    var minCost : Long ? = 0
    var maxCost : Long ? = 10000000000

    var categoryData: ArrayList<String> = arrayListOf()

    //원본 데이터
    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    
    //원본 데이터
    var contentUidList: ArrayList<String> = arrayListOf()
    
    //카테고리 필터링 이후 결과값
    var contentUidListData : ArrayList<String> = arrayListOf()
    // 카테고리 필터링 이후 결과값
    var contentData : ArrayList<ContentSellDTO> = arrayListOf()
    
    //검색 버튼 누른뒤 조회 결과
    var searchResultContentData : ArrayList<ContentSellDTO> = arrayListOf()
    var searchResultPostUidListData : ArrayList<String> = arrayListOf()
    
    //검색 창에 입력한 스트링 키값
    var searchKeyString : String ? = null

    //리사이클러뷰에 들어갈 데이터
    var mList : ArrayList<ContentSellDTO> = arrayListOf()
    //리사이클러뷰에 들어갈 postUid데이터
    var mContentUidList : ArrayList<String> = arrayListOf()

    init {

        FirebaseFirestore.getInstance().collection("contents")
            .document("sell")
            .collection("data")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentSellDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    //System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        contentSellDTO.add(item!!)
                        //System.out.println("데이터들2" + contentSellDTO.toString())
                        contentUidList.add(snapshot.id)
                    }


                }



            }



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_sell_search, container, false)

        binding.fragmentPostSellSearchEdittextSearch.addTextChangedListener(EditWatcher())
        binding.fragmentPostSellSearchEdittextSearch.setOnEditorActionListener(EditListener())
        binding.fragmentPostSellSearchRecycler.adapter = PostSellSearchRecyclerViewAdapter(binding.root.context,mList,mContentUidList)
        binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()

        //검색 버튼
        binding.fragmentPostSellSearchImagebuttonSearch.setOnClickListener {
            searchResultContentData.clear()
            searchResultPostUidListData.clear()
            for(c in contentData.indices)
            {

                if(contentData[c].explain.toString().contains(searchKeyString.toString())) {




                    searchResultContentData.add(contentData[c])
                    searchResultPostUidListData.add(contentUidListData[c])
                }



            }
            mList.clear()
            mContentUidList.clear()
            mList.addAll(searchResultContentData)
            mContentUidList.addAll(searchResultPostUidListData)
            /*
            contentData.clear()
            contentData.addAll(searchResultContentData)
            */
            println("검색 결과 식별 실행")
            searchResultContentData.forEach {
                System.out.println("검색결과 식별2" + it.toString())
            }
            //binding.fragmentPostSellSearchRecycler.adapter = PostSellSearchRecyclerViewAdapter(binding.root.context)


            binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()
        }

        binding.fragmentPostSellSearchImagebuttonCategoryOption.setOnClickListener {
            startActivityForResult(Intent(
                binding.root.context,
                PostSellSearchCategorySetActivity::class.java,
            ), 1234)
        }

        return binding.root
    }

    fun dataCleaning(){
        contentData.clear()
        contentUidListData.clear()
        for(c in contentSellDTO.indices){
            for(d in categoryData.indices)
                if (contentSellDTO[c].category.equals(categoryData[d].toString())){
                    println("중복됩니다아아아앜." + contentSellDTO[c].productExplain + "중복된 아이템의 가격" + contentSellDTO[c].costInt)

                    if (contentSellDTO[c].costInt?.toLong()!! < maxCost!!.toLong() && contentSellDTO[c].costInt?.toLong()!! > minCost!!.toLong())
                    {
                        println("포함됩니다아아아앜" + contentSellDTO[c].costInt)
                        contentData.add(contentSellDTO[c])
                        contentUidListData.add(contentUidList[c])

                    }
                }

        }







        var costFilteringData : ArrayList<ContentSellDTO> = arrayListOf()



        mList.clear()
        mContentUidList.clear()
        mList.addAll(contentData)
        mContentUidList.addAll(contentUidListData)
        binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {

            if (resultCode == 1556) {
                System.out.println("데이터 전달 성공적으로 완수3123123123123")
                categoryData.clear()
                categoryData = data?.getStringArrayListExtra("categoryList")!!
                minCost = data?.getStringExtra("minCost").toLong()
                maxCost = data?.getStringExtra("maxCost").toLong()
                println("으아아아아아아아아아아아앜" + minCost + " dsad" + maxCost)
                /*
                categoryData.forEach {
                    System.out.println("카테고리 리스트 목록 = $it")
                }

                 */

                dataCleaning()
            }


        }

        binding.fragmentPostSellSearchRecycler.adapter = PostSellSearchRecyclerViewAdapter(binding.root.context,mList,mContentUidList)
        //binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()

    }


    inner class EditWatcher: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        
        //문자 열이 바뀐 후 이벤트
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("TAG","텍스트가 변경되어써오: $s" )
            searchKeyString = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    inner class EditListener : TextView.OnEditorActionListener{
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            var handled = false

            if (actionId == EditorInfo.IME_ACTION_DONE){

            }else if (actionId == EditorInfo.IME_ACTION_NEXT){
                handled = true
            }

            return handled
        }

    }

    inner class PostSellSearchRecyclerViewAdapter(context : Context, list : ArrayList<ContentSellDTO>,postUidList : ArrayList<String>) : RecyclerView.Adapter<PostSellSearchRecyclerViewAdapter.PostSellSearchRecyclerViewAdapterViewHolder>(){

        //var contentSellDTO : ArrayList<ContentSellDTO> = arrayListOf()
        var mlist2 : ArrayList<ContentSellDTO> = arrayListOf()
        var mpostlist : ArrayList<String> = postUidList
        //var data  = listOf<ContentSellDTO>()
        init {

            System.out.println("리사이클러뷰 초기화아아아아아아아아아아앜")
            mlist2.forEach {
                println("리스트1 " + it.toString())
            }
            mlist2.clear()
            mlist2 = list
            list.forEach {
                println("리스트2 " + it.toString())
            }

            mlist2.forEach {
                println("리스트3 " + it.toString())
            }//contentSellDTO.addAll(list)

          //  data = list



            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSellSearchRecyclerViewAdapter.PostSellSearchRecyclerViewAdapterViewHolder {

            val binding = ItemPostSellSearchResultBinding.inflate(LayoutInflater.from(context),parent,false)
            return PostSellSearchRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return mlist2.size
        }

        override fun onBindViewHolder(holder: PostSellSearchRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(mlist2[position])

            //아이템 자체 클릭
            holder.binding.itemPostSellSearchResultConstAll.setOnClickListener {
                var intent = Intent(holder.itemView.context,DetailSellViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , mlist2[position].uid)
                    putExtra("userId",mlist2[position].userId)
                    putExtra("postUid",mpostlist[position])
                    putExtra("cost",mlist2[position].cost)
                    putExtra("category",mlist2[position].category)
                    putExtra("imageList",mlist2[position].imageDownLoadUrlList)
                    putExtra("contentTime",mlist2[position].time)
                    putExtra("productExplain",mlist2[position].productExplain)
                    putExtra("explain",mlist2[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)
            }

            //사진 추가
            if (mlist2[position].imageDownLoadUrlList?.isEmpty() == false) {
                Glide.with(holder.itemView.context)
                    .load(mlist2[position].imageDownLoadUrlList?.get(0))
                    .into(holder.binding.itemPostSellSearchResultImageviewPhoto)
            }


        }

        inner class PostSellSearchRecyclerViewAdapterViewHolder(val binding: ItemPostSellSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: ContentSellDTO){
                binding.itempostsellsearchresult = data
            }
        }


    }
}