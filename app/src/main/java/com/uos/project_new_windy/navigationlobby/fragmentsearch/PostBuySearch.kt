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
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentPostBuySearchBinding
import com.uos.project_new_windy.databinding.ItemPostBuySearchResultBinding
import com.uos.project_new_windy.databinding.ItemPostSellSearchResultBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostBuySearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostBuySearch : Fragment() {
    // TODO: Rename and change types of parameters


    lateinit var binding : FragmentPostBuySearchBinding
    var categoryData: ArrayList<String> = arrayListOf()

    //원본 데이터
    var contentBuyTO: ArrayList<ContentBuyDTO> = arrayListOf()

    //원본 데이터
    var contentUidList: ArrayList<String> = arrayListOf()



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


        binding.fragmentPostBuySearchRecycler.adapter =PostBuySearchRecyclerViewAdapter(binding.root.context,contentBuyTO,contentUidList)
        binding.fragmentPostBuySearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostBuySearchRecycler.adapter?.notifyDataSetChanged()

        //검색 버튼
        binding.fragmentPostBuySearchImagebuttonSearch.setOnClickListener {
            searchResultContentData.clear()


        }

        return binding.root
    }

    inner class EditWatcher: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchKeyString = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {
            TODO("Not yet implemented")
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

            val binding = ItemPostSellSearchResultBinding.inflate(LayoutInflater.from(context),parent,false)
            return PostBuySearchRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return mlist.size
        }

        override fun onBindViewHolder(holder: PostBuySearchRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(mlist[position])

            //아이템 자체 클릭
            holder.binding.itemPostBuySearchResultConstAll.setOnClickListener {
                var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , mlist[position].uid)
                    putExtra("userId",mlist[position].userId)
                    putExtra("postUid",mpostlist[position])
                    putExtra("cost",mlist[position].cost)
                    //putExtra("category",mlist[position].category)
                    //putExtra("imageList",mlist[position].imageDownLoadUrlList)
                    putExtra("contentTime",mlist[position].time)
                    //putExtra("productExplain",mlist[position].productExplain)
                    putExtra("explain",mlist[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)
            }

            //사진 추가
            /*
            if (mlist[position].imageDownLoadUrlList?.isEmpty() == false) {
                Glide.with(holder.itemView.context)
                    .load(mlist[position].imageDownLoadUrlList?.get(0))
                    .into(holder.binding.itemPostBuySearchResultImageviewPhoto)
            }

             */


        }

        inner class PostBuySearchRecyclerViewAdapterViewHolder(val binding: ItemPostBuySearchResultBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: ContentBuyDTO){
                binding.itempostbuysearchresult = data
            }
        }


    }

}