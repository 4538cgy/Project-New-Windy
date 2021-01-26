package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.uos.project_new_windy.databinding.FragmentPostBuySearchBinding
import com.uos.project_new_windy.databinding.ItemPostBuySearchResultBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailBuyViewActivity
import com.uos.project_new_windy.navigationlobby.fragmentsearch.categoryselectactivity.PostBuySearchCategorySetActivity
import com.uos.project_new_windy.navigationlobby.fragmentsearch.categoryselectactivity.PostSellSearchCategorySetActivity


class PostBuySearch : Fragment() {
    // TODO: Rename and change types of parameters


    lateinit var binding : FragmentPostBuySearchBinding
    var categoryData: ArrayList<String> = arrayListOf()
    var minCost : Long ? = null
    var maxCost : Long ? = null


    //원본 데이터
    var contentBuyTO: ArrayList<ContentBuyDTO> = arrayListOf()

    //원본 데이터
    var contentUidList: ArrayList<String> = arrayListOf()


    //카테고리 필터링 이후 결과값
    var contentUidListData : ArrayList<String> = arrayListOf()
    // 카테고리 필터링 이후 결과값
    var contentData : ArrayList<ContentBuyDTO> = arrayListOf()



    //검색 버튼 누른뒤 조회 결과
    var searchResultContentData : ArrayList<ContentBuyDTO> = arrayListOf()
    var searchResultPostUidListData : ArrayList<String> = arrayListOf()

    //검색 창에 입력한 스트링 키값
    var searchKeyString : String ? = null

    //리사이클러뷰에 들어갈 데이터
    var mList : ArrayList<ContentBuyDTO> = arrayListOf()
    //리사이클러뷰에 들어갈 postUid데이터
    var mContentUidList : ArrayList<String> = arrayListOf()

    init {

        FirebaseFirestore.getInstance().collection("contents")
            .document("buy")
            .collection("data")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentBuyTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentBuyDTO::class.java)
                    //System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌

                        contentBuyTO.add(item!!)
                        //System.out.println("데이터들2" + contentSellDTO.toString())
                        contentUidList.add(snapshot.id)



                }



            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_post_buy_search,container,false)

        binding.fragmentPostBuySearchEdittextSearch.addTextChangedListener(EditWatcher())
        binding.fragmentPostBuySearchEdittextSearch.setOnEditorActionListener(EditListener())


        binding.fragmentPostBuySearchRecycler.adapter =PostBuySearchRecyclerViewAdapter(binding.root.context,mList,mContentUidList)
        binding.fragmentPostBuySearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostBuySearchRecycler.adapter?.notifyDataSetChanged()

        //검색 버튼
        binding.fragmentPostBuySearchImagebuttonSearch.setOnClickListener {
            searchResultContentData.clear()

            for (c in contentBuyTO.indices){
                if (contentBuyTO[c].explain.toString().contains(searchKeyString.toString()))
                {
                    searchResultContentData.add(contentBuyTO[c])
                    searchResultPostUidListData.add(contentUidList[c])
                }
            }
            mList.clear()
            mContentUidList.clear()
            mList.addAll(searchResultContentData)
            mContentUidList.addAll(searchResultPostUidListData)

            binding.fragmentPostBuySearchRecycler.adapter?.notifyDataSetChanged()
        }

        binding.fragmentPostBuyImagebuttonCategoryOption.setOnClickListener {
            startActivityForResult(Intent(
                binding.root.context,
                PostBuySearchCategorySetActivity::class.java,
            ), 1234)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {

            if (resultCode == 1556) {
                System.out.println("데이터 전달 성공적으로 완수3123123123123")
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

        binding.fragmentPostBuySearchRecycler.adapter = PostBuySearchRecyclerViewAdapter(binding.root.context,mList,mContentUidList)
        //binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        binding.fragmentPostBuySearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostBuySearchRecycler.adapter?.notifyDataSetChanged()

    }

    fun dataCleaning(){
        contentData.clear()
        contentUidListData.clear()
        for(c in contentBuyTO.indices){
            for(d in categoryData.indices)
                if (contentBuyTO[c].categoryHash.equals(categoryData[d].toString())){

                    if (contentBuyTO[c].costInt?.toLong()!! < maxCost!!.toLong() && contentBuyTO[c].costInt?.toLong()!! > minCost!!.toLong())
                    {
                        contentData.add(contentBuyTO[c])
                        contentUidListData.add(contentUidList[c])

                    }
                }

        }

        /*
        for(c in contentBuyTO.indices){
            for(d in categoryData.indices)
                if (contentBuyTO[c].categoryHash.equals(categoryData[d].toString())){
                    System.out.println("중복됩니다." + categoryData[d] + contentBuyTO[c])
                    contentData.add(contentBuyTO[c])
                    contentUidListData.add(contentUidList[c])
                }
        }

         */

        mList.clear()
        mContentUidList.clear()
        mList.addAll(contentData)
        mContentUidList.addAll(contentUidListData)
        binding.fragmentPostBuySearchRecycler.adapter?.notifyDataSetChanged()
    }

    inner class EditWatcher: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

    inner class PostBuySearchRecyclerViewAdapter(context : Context, list : ArrayList<ContentBuyDTO>, postUidList : ArrayList<String>) : RecyclerView.Adapter<PostBuySearchRecyclerViewAdapter.PostBuySearchRecyclerViewAdapterViewHolder>(){

        // var contentSellDTO : ArrayList<ContentSellDTO> = arrayListOf()
        var mlist : ArrayList<ContentBuyDTO> = list
        var mpostlist : ArrayList<String> = postUidList
        //var data  = listOf<ContentSellDTO>()
        init {

            System.out.println("리사이클러뷰 초기화아아아아아아아아아아앜")
            /*
            mlist.clear()
            mpostlist.clear()

             */
            //contentSellDTO.addAll(list)

            //  data = list



            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostBuySearchRecyclerViewAdapter.PostBuySearchRecyclerViewAdapterViewHolder {

            val binding = ItemPostBuySearchResultBinding.inflate(LayoutInflater.from(context),parent,false)
            return PostBuySearchRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return mlist.size
        }

        override fun onBindViewHolder(holder: PostBuySearchRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(mlist[position])

            //아이템 자체 클릭
            holder.binding.itemPostBuySearchResultConstAll.setOnClickListener {
                var intent = Intent(holder.itemView.context, DetailBuyViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , mlist[position].uid)
                    putExtra("userId",mlist[position].userId)
                    putExtra("postUid",mpostlist[position])
                    putExtra("costMin",mlist[position].costMin)
                    putExtra("costMax",mlist[position].costMax)
                    putExtra("categoryHash",mlist[position].categoryHash)
                    putExtra("imageUrl",mlist[position].imageUrl)
                    putExtra("contentTime",mlist[position].time)
                    //putExtra("productExplain",mlist[position].productExplain)
                    putExtra("explain",mlist[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)
            }

            //사진 추가

            if (mlist[position].imageUrl?.isEmpty() == false) {
                Glide.with(holder.itemView.context)
                    .load(mlist[position].imageUrl)
                    .into(holder.binding.itemPostBuySearchResultImageviewPhoto)
            }




        }

        inner class PostBuySearchRecyclerViewAdapterViewHolder(val binding: ItemPostBuySearchResultBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: ContentBuyDTO){
                binding.itempostbuysearchresult = data
            }
        }


    }

}