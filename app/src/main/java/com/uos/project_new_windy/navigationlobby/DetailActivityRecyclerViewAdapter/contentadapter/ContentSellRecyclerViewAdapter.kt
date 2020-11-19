package com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.databinding.ItemRecyclerNormalBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO

class ContentSellRecyclerViewAdapter (private val context: Context) : RecyclerView.Adapter<ContentSellRecyclerViewAdapter.ContentSellRecyclerViewAdapterViewHolder>() {

    init {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentSellRecyclerViewAdapterViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ContentSellRecyclerViewAdapterViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ContentSellRecyclerViewAdapterViewHolder(val binding: ItemRecyclerNormalBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentBuyDTO){
            binding.contentnormal = data
        }
    }

}