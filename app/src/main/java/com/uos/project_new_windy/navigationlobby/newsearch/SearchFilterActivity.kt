package com.uos.project_new_windy.navigationlobby.newsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySearchFilterBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO

class SearchFilterActivity : AppCompatActivity() {

    lateinit var binding : ActivitySearchFilterBinding
    var categoryList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_filter)
        binding.activitysearchfilter = this@SearchFilterActivity

        binding.activitySearchFilterRecycler.adapter = PostSellSearchCategoryRecyclerViewAdapter()
        binding.activitySearchFilterRecycler.layoutManager = GridLayoutManager(this,2)

        //종류 선택
        binding.activitySearchFilterTextviewSelectSell.setOnClickListener {
            categoryList.clear()
            initCategoryList()
            binding.activitySearchFilterTextviewOptiontitle.text = "판매 게시판 필터 옵션"
            (binding.activitySearchFilterRecycler.adapter)!!.notifyDataSetChanged()
        }
        binding.activitySearchFilterTextviewSelectBuy.setOnClickListener {
            categoryList.clear()
            initCategoryList()
            binding.activitySearchFilterTextviewOptiontitle.text = "구매 게시판 필터 옵션"
            (binding.activitySearchFilterRecycler.adapter)!!.notifyDataSetChanged()
        }
        binding.activitySearchFilterTextviewSelectNormal.setOnClickListener {
            categoryList.clear()
            binding.activitySearchFilterTextviewOptiontitle.text = "일반 게시판 필터 옵션"
            (binding.activitySearchFilterRecycler.adapter)!!.notifyDataSetChanged()
        }


    }

    fun initCategoryList(){
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

    inner class PostSellSearchCategoryRecyclerViewAdapter :
        RecyclerView.Adapter<PostSellSearchCategoryRecyclerViewAdapter.PostSellSearchCategoryRecyclerViewAdapterViewHolder>() {

        var categorySellPostDTO: ArrayList<CategorySellPostDTO> = arrayListOf()

        var data = listOf<CategorySellPostDTO>()

        init {


            categorySellPostDTO.add(CategorySellPostDTO("농기계", true))
            categorySellPostDTO.add(CategorySellPostDTO("농산물", true))
            categorySellPostDTO.add(CategorySellPostDTO("축산물", true))
            categorySellPostDTO.add(CategorySellPostDTO("소모품", true))
            categorySellPostDTO.add(CategorySellPostDTO("농지", true))
            categorySellPostDTO.add(CategorySellPostDTO("가구", true))
            categorySellPostDTO.add(CategorySellPostDTO("기타", true))
            categorySellPostDTO.add(CategorySellPostDTO("축산자재",true))
            categorySellPostDTO.add(CategorySellPostDTO("농자재",true))
            categorySellPostDTO.add(CategorySellPostDTO("화물트럭",true))
            categorySellPostDTO.add(CategorySellPostDTO("산업기계",true))


            data = categorySellPostDTO
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

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): PostSellSearchCategoryRecyclerViewAdapterViewHolder {

            val binding =
                ItemSellSearchCategoryBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    parent,
                    false)
            return PostSellSearchCategoryRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return categorySellPostDTO.size
        }

        override fun onBindViewHolder(
            holder: PostSellSearchCategoryRecyclerViewAdapterViewHolder,
            position: Int,
        ) {
            holder.onBind(categorySellPostDTO[position])


            //체크 박스를 클릭했을때
            holder.binding.itemSellSearchCategoryCheckbox.setOnClickListener {


                //해당 포지션이 체크되지 않았을 경우
                if (categorySellPostDTO[position].check == false) {
                    //데이터 추가
                    categoryList.add(categorySellPostDTO[position].title.toString())
                    categorySellPostDTO[position].check = true

                    //해당 포지션이 체크되었을 경우
                } else {
                    //리스트를 조회해서 같은 이름을 가진 인덱스 삭제
                    categoryList.remove(categorySellPostDTO[position].title.toString())

                    categorySellPostDTO[position].check = false
                }


                categoryList.forEach {
                    System.out.println("리스트 = $it")
                }

            }
        }

        inner class PostSellSearchCategoryRecyclerViewAdapterViewHolder(val binding: ItemSellSearchCategoryBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun onBind(data: CategorySellPostDTO) {
                binding.itemsellsearchcategory = data
            }
        }


    }
}

