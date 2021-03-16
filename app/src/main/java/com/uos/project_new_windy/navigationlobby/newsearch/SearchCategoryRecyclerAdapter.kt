package com.uos.project_new_windy.navigationlobby.newsearch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.databinding.ItemBuySearchCategoryBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.CatgoryBuyPostDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import java.lang.RuntimeException

class SearchCategoryRecyclerAdapter(private val context: Context,private val contentType: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var sellList : ArrayList<CategorySellPostDTO> = arrayListOf()
    var buyList : ArrayList<CatgoryBuyPostDTO> = arrayListOf()

    init {


        when(contentType){
            "sell"->{

            }
            "buy" ->{

            }
            "normal" ->{

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(contentType){
            "sell"->{
                val binding = ItemSellSearchCategoryBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                SellCategoryViewHolder(binding)
            }
            "buy"->{
                val binding = ItemBuySearchCategoryBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                BuyCategoryViewHolder(binding)
            }
            else -> throw RuntimeException("NOPE")
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(hold0er: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class SellCategoryViewHolder(val binding: ItemSellSearchCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : CategorySellPostDTO){
            binding.itemsellsearchcategory = data
        }
    }

    inner class BuyCategoryViewHolder(val binding: ItemBuySearchCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : CatgoryBuyPostDTO){
            binding.itembuysearchcategory = data
        }
    }

}