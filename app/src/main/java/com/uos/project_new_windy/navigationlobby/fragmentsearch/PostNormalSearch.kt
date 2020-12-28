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
import com.uos.project_new_windy.databinding.FragmentPostNormalSearchBinding
import com.uos.project_new_windy.databinding.ItemPostBuySearchResultBinding
import com.uos.project_new_windy.databinding.ItemPostNormalSearchResultBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailBuyViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailNormalViewActivity
import java.util.Arrays.toString

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostNormalSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostNormalSearch : Fragment() {
    // TODO: Rename and change types of parameters


    lateinit var binding : FragmentPostNormalSearchBinding


    //원본 데이터
    var contentNormalDTO: ArrayList<ContentNormalDTO> = arrayListOf()

    //원본 데이터
    var contentUidList: ArrayList<String> = arrayListOf()


    //카테고리 필터링 이후 결과값
    var contentUidListData : ArrayList<String> = arrayListOf()
    // 카테고리 필터링 이후 결과값
    var contentData : ArrayList<ContentNormalDTO> = arrayListOf()



    //검색 버튼 누른뒤 조회 결과
    var searchResultContentData : ArrayList<ContentNormalDTO> = arrayListOf()
    var searchResultPostUidListData : ArrayList<String> = arrayListOf()

    //검색 창에 입력한 스트링 키값
    var searchKeyString : String ? = null

    //리사이클러뷰에 들어갈 데이터
    var mList : ArrayList<ContentNormalDTO> = arrayListOf()
    //리사이클러뷰에 들어갈 postUid데이터
    var mContentUidList : ArrayList<String> = arrayListOf()

    init {

        FirebaseFirestore.getInstance().collection("contents")
            .document("normal")
            .collection("data")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentNormalDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentNormalDTO::class.java)
                    System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌

                    contentNormalDTO.add(item!!)
                    System.out.println("데이터들2" + ContentNormalDTO::class.java)
                    contentUidList.add(snapshot.id)



                }



            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_post_normal_search,container,false)

        binding.fragmentPostNormalSearchEdittextSearch.addTextChangedListener(EditWatcher())
        binding.fragmentPostNormalSearchEdittextSearch.setOnEditorActionListener(EditListener())


        binding.fragmentPostNormalSearchRecycler.adapter =PostNormalSearchRecyclerViewAdapter(binding.root.context,mList,mContentUidList)
        binding.fragmentPostNormalSearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostNormalSearchRecycler.adapter?.notifyDataSetChanged()

        //검색 버튼
        binding.fragmentPostNormalSearchImagebuttonSearch.setOnClickListener {
            searchResultContentData.clear()

            for (c in contentNormalDTO.indices){
                if (contentNormalDTO[c].explain.toString().contains(searchKeyString.toString()))
                {
                    searchResultContentData.add(contentNormalDTO[c])
                    searchResultPostUidListData.add(contentUidList[c])
                }
            }
            mList.clear()
            mContentUidList.clear()
            mList.addAll(searchResultContentData)
            mContentUidList.addAll(searchResultPostUidListData)

            binding.fragmentPostNormalSearchRecycler.adapter?.notifyDataSetChanged()
        }

        return binding.root
    }


    inner class EditWatcher: TextWatcher {
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

    inner class PostNormalSearchRecyclerViewAdapter(context : Context, list : ArrayList<ContentNormalDTO>, postUidList : ArrayList<String>) : RecyclerView.Adapter<PostNormalSearchRecyclerViewAdapter.PostNormalSearchRecyclerViewAdapterViewHolder>(){

        // var contentSellDTO : ArrayList<ContentSellDTO> = arrayListOf()
        var mlist : ArrayList<ContentNormalDTO> = list
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostNormalSearchRecyclerViewAdapter.PostNormalSearchRecyclerViewAdapterViewHolder {

            val binding = ItemPostNormalSearchResultBinding.inflate(LayoutInflater.from(context),parent,false)
            return PostNormalSearchRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return mlist.size
        }

        override fun onBindViewHolder(holder: PostNormalSearchRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(mlist[position])

            //아이템 자체 클릭
            holder.binding.itemPostNormalSearchResultConstAll.setOnClickListener {
                var intent = Intent(holder.itemView.context, DetailNormalViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , mlist[position].uid)
                    putExtra("userId",mlist[position].userId)
                    putExtra("postUid",mpostlist[position])
                    putExtra("imageList",mlist[position].imageDownLoadUrlList)
                    putExtra("contentTime",mlist[position].time)
                    //putExtra("productExplain",mlist[position].productExplain)
                    putExtra("explain",mlist[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)
            }

            //사진 추가
            if (mlist[position].imageDownLoadUrlList?.isEmpty() == false) {
                Glide.with(holder.itemView.context)
                    .load(mlist[position].imageDownLoadUrlList?.get(0))
                    .into(holder.binding.itemPostNormalSearchResultImageviewPhoto)
            }





        }

        inner class PostNormalSearchRecyclerViewAdapterViewHolder(val binding: ItemPostNormalSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: ContentNormalDTO){
                binding.itempostnormalsearchresult = data
            }
        }


    }

}