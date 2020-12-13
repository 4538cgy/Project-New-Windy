package com.uos.project_new_windy.navigationlobby.fragmentsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityPostSellSearchCategorySetBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity

class PostSellSearchCategorySetActivity : AppCompatActivity() {

    lateinit var binding : ActivityPostSellSearchCategorySetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_post_sell_search_category_set)
        binding.activitypostsellsearchcategoryset = this@PostSellSearchCategorySetActivity

        binding.activityPostSellSearchCategorySetRecycler.adapter = PostSellSearchCategoryRecyclerViewAdapter()
        binding.activityPostSellSearchCategorySetRecycler.layoutManager = GridLayoutManager(this,2)


        binding.activityPostSellSearchCategorySetImagebuttonBack.setOnClickListener {
            finish()
        }
    }


    inner class PostSellSearchCategoryRecyclerViewAdapter : RecyclerView.Adapter<PostSellSearchCategoryRecyclerViewAdapter.PostSellSearchCategoryRecyclerViewAdapterViewHolder>(){

        var categorySellPostDTO : ArrayList<CategorySellPostDTO> = arrayListOf()
        var data  = listOf<CategorySellPostDTO>()
        init {


            categorySellPostDTO.add(CategorySellPostDTO("아아아아",true))

            categorySellPostDTO.add(CategorySellPostDTO("아아아아",true))
            categorySellPostDTO.add(CategorySellPostDTO("아아아아",false))
            categorySellPostDTO.add(CategorySellPostDTO("아아아아",false))
            categorySellPostDTO.add(CategorySellPostDTO("아아아아",false))
            categorySellPostDTO.add(CategorySellPostDTO("아아아아",true))


            data = categorySellPostDTO

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSellSearchCategoryRecyclerViewAdapter.PostSellSearchCategoryRecyclerViewAdapterViewHolder {

            val binding = ItemSellSearchCategoryBinding.inflate(LayoutInflater.from(this@PostSellSearchCategorySetActivity),parent,false)
            return PostSellSearchCategoryRecyclerViewAdapterViewHolder(binding)
        }


        override fun getItemCount(): Int {
            return categorySellPostDTO.size
        }

        override fun onBindViewHolder(holder: PostSellSearchCategoryRecyclerViewAdapterViewHolder, position: Int) {
            holder.onBind(categorySellPostDTO[position])
        }

        inner class PostSellSearchCategoryRecyclerViewAdapterViewHolder(val binding: ItemSellSearchCategoryBinding) : RecyclerView.ViewHolder(binding.root){
            fun onBind(data: CategorySellPostDTO){
                binding.itemsellsearchcategory = data
            }
        }


    }

}