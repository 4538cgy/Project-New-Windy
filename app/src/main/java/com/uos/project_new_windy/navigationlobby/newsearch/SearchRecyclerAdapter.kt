package com.uos.project_new_windy.navigationlobby.newsearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uos.project_new_windy.databinding.ItemBuySearchCategoryBinding
import com.uos.project_new_windy.databinding.ItemPostBuySearchResultBinding
import com.uos.project_new_windy.databinding.ItemPostSellSearchResultBinding
import com.uos.project_new_windy.databinding.ItemSellSearchCategoryBinding
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.CatgoryBuyPostDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailBuyViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import java.lang.RuntimeException

class SearchRecyclerAdapter(private val context: Context, private val contentType: String, private val list : ArrayList<Any>,private val uidList: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var sellList : ArrayList<ContentSellDTO> = arrayListOf()
    var buyList : ArrayList<ContentBuyDTO> = arrayListOf()
    var listSize = 0

    init {


        when(contentType){
            "sell"->{

                sellList = list as ArrayList<ContentSellDTO>
                listSize = sellList.size

                println("어댑터 내부의 데이터입니다. sell ${sellList.toString()} ${uidList.toString()}" )
            }
            "buy" ->{

                buyList = list as ArrayList<ContentBuyDTO>
                listSize = buyList.size


                println("어댑터 내부의 데이터입니다. buy ${buyList.toString()}" )
            }
            "normal" ->{

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(contentType){
            "sell"->{
                val binding = ItemPostSellSearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                SellCategoryViewHolder(binding)
            }
            "buy"->{
                val binding = ItemPostBuySearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                BuyCategoryViewHolder(binding)
            }
            else -> throw RuntimeException("NOPE")
        }
    }

    override fun getItemCount(): Int = listSize

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(contentType){
            "sell" ->{
                (holder as SellCategoryViewHolder).onBind(sellList[position])
                if (sellList[position].imageDownLoadUrlList?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(sellList[position].imageDownLoadUrlList?.get(0))
                        .into(holder.binding.itemPostSellSearchResultImageviewPhoto)
                }

                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.itemView.context,DetailSellViewActivity::class.java)
                    intent.apply {
                        putExtra("uid" , sellList[position].uid)
                        putExtra("userId",sellList[position].userId)
                        putExtra("postUid",uidList[position])
                        putExtra("cost",sellList[position].cost)
                        putExtra("category",sellList[position].category)
                        putExtra("imageList",sellList[position].imageDownLoadUrlList)
                        putExtra("contentTime",sellList[position].time)
                        putExtra("productExplain",sellList[position].productExplain)
                        putExtra("explain",sellList[position].explain)
                        //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                    }
                    context?.startActivity(intent)
                }
            }
            "buy" ->{
                (holder as BuyCategoryViewHolder).onBind(buyList[position])
                if (buyList[position].imageUrl?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(buyList[position].imageUrl)
                        .into(holder.binding.itemPostBuySearchResultImageviewPhoto)
                }
                var intent = Intent(holder.itemView.context,DetailBuyViewActivity::class.java)
                intent.apply {
                    putExtra("uid" , buyList[position].uid)
                    putExtra("userId",buyList[position].userId)
                    putExtra("postUid",uidList[position])
                    putExtra("costMin",buyList[position].costMin)
                    putExtra("costMax",buyList[position].costMax)
                    putExtra("categoryHash",buyList[position].categoryHash)
                    putExtra("imageUrl",buyList[position].imageUrl)
                    putExtra("contentTime",buyList[position].time)
                    //putExtra("productExplain",mlist[position].productExplain)
                    putExtra("explain",buyList[position].explain)
                    //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                }
                context?.startActivity(intent)

                holder.binding.itemPostBuySearchResultTextviewCostmin.setText("최소 " +buyList[position].costMin.toString() + "원")
                holder.binding.itemPostBuySearchResultTextviewCostmax.setText("최대 " +buyList[position].costMax.toString() + "원")
            }
            else -> throw RuntimeException("nope")
        }
    }

    inner class SellCategoryViewHolder(val binding: ItemPostSellSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentSellDTO){
            binding.itempostsellsearchresult = data
        }
    }

    inner class BuyCategoryViewHolder(val binding: ItemPostBuySearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentBuyDTO){
            binding.itempostbuysearchresult = data
        }
    }

}