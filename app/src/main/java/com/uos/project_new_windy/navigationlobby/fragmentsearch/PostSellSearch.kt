package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.FragmentPostSellSearchBinding
import com.uos.project_new_windy.databinding.ItemPostSellSearchResultBinding
import com.uos.project_new_windy.databinding.ItemRecyclerSellBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.CommentActivity
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentSellRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.UserFragment
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostSellSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostSellSearch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentPostSellSearchBinding

    var categoryData: ArrayList<String> = arrayListOf()

    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentData : ArrayList<ContentSellDTO> = arrayListOf()

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
                    System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        contentSellDTO.add(item!!)
                        System.out.println("데이터들2" + contentSellDTO.toString())
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





        startActivityForResult(Intent(
            binding.root.context,
            PostSellSearchCategorySetActivity::class.java,
        ), 1234)
        return binding.root
    }

    fun dataCleaning(){
        for(c in contentSellDTO.indices){
            for(d in categoryData.indices)
            if (contentSellDTO[c].category.equals(categoryData[d].toString())){
                    System.out.println("중복됩니다." + categoryData[d] + contentSellDTO[c])
                contentData.add(contentSellDTO[c])
            }
        }

        contentData.forEach {
            System.out.println("카테고리를 포함한 데이터 $it" )
        }

        binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {

            if (resultCode == 1556) {
                System.out.println("데이터 전달 성공적으로 완수3123123123123")
                categoryData = data?.getStringArrayListExtra("categoryList")!!
                categoryData.forEach {
                    System.out.println("카테고리 리스트 목록 = $it")
                }
                dataCleaning()
            }


        }

        binding.fragmentPostSellSearchRecycler.adapter = PostSellSearchRecyclerViewAdapter(binding.root.context)
        //binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        binding.fragmentPostSellSearchRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fragmentPostSellSearchRecycler.adapter?.notifyDataSetChanged()

    }


    inner class PostSellSearchRecyclerViewAdapter(context : Context) : RecyclerView.Adapter<PostSellSearchRecyclerViewAdapter.PostSellSearchRecyclerViewAdapterViewHolder>(){

        var contentSellDTO : ArrayList<ContentSellDTO> = arrayListOf()

        var data  = listOf<ContentSellDTO>()
        init {
            
            System.out.println("리사이클러뷰 초기화아아아아아아아아아아앜")

            contentSellDTO.addAll(contentData)


            data = contentSellDTO

            data.forEach {
                System.out.println("카테고리를 포함한 데이터 datadatadata $it" )
            }

            contentSellDTO.forEach {
                System.out.println("카테고리를 포함한 데이터 dadadadadadadada $it" )
            }

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSellSearchRecyclerViewAdapter.PostSellSearchRecyclerViewAdapterViewHolder {

            val binding = ItemPostSellSearchResultBinding.inflate(LayoutInflater.from(context),parent,false)
            return PostSellSearchRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return contentSellDTO.size
        }

        override fun onBindViewHolder(holder: PostSellSearchRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(contentSellDTO[position])

            //아이템 자체 클릭
            holder.binding.itemPostSellSearchResultConstAll.setOnClickListener {
                var intent = Intent(holder.itemView.context,DetailSellViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , contentSellDTO[position].uid)
                    putExtra("userId",contentSellDTO[position].userId)
                    putExtra("postUid",contentUidList[position])
                    putExtra("cost",contentSellDTO[position].cost)
                    putExtra("category",contentSellDTO[position].category)
                    putExtra("imageList",contentSellDTO[position].imageDownLoadUrlList)
                    putExtra("contentTime",contentSellDTO[position].time)
                    putExtra("productExplain",contentSellDTO[position].productExplain)
                    putExtra("explain",contentSellDTO[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)
            }

            //사진 추가
            if (data[position].imageDownLoadUrlList?.isEmpty() == false) {
                Glide.with(holder.itemView.context)
                    .load(data[position].imageDownLoadUrlList?.get(0))
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