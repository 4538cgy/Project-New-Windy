package com.uos.project_new_windy.navigationlobby.newsearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uos.project_new_windy.databinding.*
import com.uos.project_new_windy.model.CategorySellPostDTO
import com.uos.project_new_windy.model.CatgoryBuyPostDTO
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.model.contentdto.ContentShopDTO
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailBuyViewActivity
import com.uos.project_new_windy.navigationlobby.detailviewactivity.DetailSellViewActivity
import java.lang.RuntimeException

class SearchRecyclerAdapter(
    private val context: Context,
    private val contentType: String,
    private val list: ArrayList<Any>,
    private val uidList: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var sellList: ArrayList<ContentSellDTO> = arrayListOf()
    var buyList: ArrayList<ContentBuyDTO> = arrayListOf()
    var normalList : ArrayList<ContentNormalDTO> = arrayListOf()
    var shopList : ArrayList<ContentShopDTO> = arrayListOf()
    var listSize = 0

    init {


        when (contentType) {
            "sell" -> {

                sellList = list as ArrayList<ContentSellDTO>
                listSize = sellList.size

                println("어댑터 내부의 데이터입니다. sell ${sellList.toString()} ${uidList.toString()}")
            }
            "buy" -> {

                buyList = list as ArrayList<ContentBuyDTO>
                listSize = buyList.size


                println("어댑터 내부의 데이터입니다. buy ${buyList.toString()}")
            }
            "normal" -> {
                normalList = list as ArrayList<ContentNormalDTO>
                listSize = normalList.size
                println("어댑터 내부의 데이터입니다. normal ${normalList.toString()}")
            }

            "shop" -> {
                shopList = list as ArrayList<ContentShopDTO>
                listSize = shopList.size
                println("어댑터 내부의 데이터입니다. shop ${shopList.toString()}")
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (contentType) {
            "sell" -> {
                val binding = ItemPostSellSearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                SellCategoryViewHolder(binding)
            }
            "buy" -> {
                val binding = ItemPostBuySearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                BuyCategoryViewHolder(binding)
            }
            "normal" -> {
                val binding = ItemPostNormalSearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                NormalCategoryViewHolder(binding)
            }
            "shop" ->{
                val binding = ItemPostShopSearchResultBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                ShopCategoryViewHolder(binding)
            }

            else -> throw RuntimeException("NOPE")
        }
    }

    override fun getItemCount(): Int = listSize

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (contentType) {
            "sell" -> {
                (holder as SellCategoryViewHolder).onBind(sellList[position])


                if (sellList[position].imageDownLoadUrlList?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(sellList[position].imageDownLoadUrlList?.get(0))
                        .into(holder.binding.itemPostSellSearchResultImageviewPhoto)
                }



                holder.binding.itemPostSellSearchResultTextviewCost.text =
                    "가격 : ${sellList[position].cost}\n\n글을 터치하여 자세히 확인하세요."

                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
                    intent.apply {
                        putExtra("uid", sellList[position].uid)
                        putExtra("userId", sellList[position].userId)
                        putExtra("postUid", uidList[position])
                        putExtra("cost", sellList[position].cost)
                        putExtra("category", sellList[position].category)
                        putExtra("imageList", sellList[position].imageDownLoadUrlList)
                        putExtra("contentTime", sellList[position].time)
                        putExtra("productExplain", sellList[position].productExplain)
                        putExtra("explain", sellList[position].explain)
                        //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                    }
                    context?.startActivity(intent)
                }
            }
            "buy" -> {
                (holder as BuyCategoryViewHolder).onBind(buyList[position])

                if (buyList[position].imageUrl?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(buyList[position].imageUrl)
                        .into(holder.binding.itemPostBuySearchResultImageviewPhoto)
                }



                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.itemView.context, DetailBuyViewActivity::class.java)
                    intent.apply {
                        putExtra("uid", buyList[position].uid)
                        putExtra("userId", buyList[position].userId)
                        putExtra("postUid", uidList[position])
                        putExtra("costMin", buyList[position].costMin)
                        putExtra("costMax", buyList[position].costMax)
                        putExtra("categoryHash", buyList[position].categoryHash)
                        putExtra("imageUrl", buyList[position].imageUrl)
                        putExtra("contentTime", buyList[position].time)
                        //putExtra("productExplain",mlist[position].productExplain)
                        putExtra("explain", buyList[position].explain)
                        //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                    }
                    context?.startActivity(intent)
                }




                holder.binding.itemPostBuySearchResultTextviewCostmin.setText("최소 " + buyList[position].costMin.toString() + "원")
                holder.binding.itemPostBuySearchResultTextviewCostmax.setText("최대 " + buyList[position].costMax.toString() + "원")
            }
            "shop" ->{
                (holder as ShopCategoryViewHolder).onBind(shopList[position])

                if (shopList[position].imageDownLoadUrlList?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(shopList[position].imageDownLoadUrlList?.get(0))
                        .into(holder.binding.itemPostShopSearchResultImageviewPhoto)
                }



                holder.binding.itemPostShopSearchResultTextviewCost.text =
                    "가격 : ${shopList[position].cost}\n\n글을 터치하여 자세히 확인하세요."

                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
                    intent.apply {
                        putExtra("uid", shopList[position].uid)
                        putExtra("userId", shopList[position].userId)
                        putExtra("postUid", uidList[position])
                        putExtra("cost", shopList[position].cost)
                        putExtra("category", shopList[position].category)
                        putExtra("imageList", shopList[position].imageDownLoadUrlList)
                        putExtra("contentTime", shopList[position].time)
                        putExtra("productExplain", shopList[position].productExplain)
                        putExtra("explain", shopList[position].explain)
                        //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                    }
                    context?.startActivity(intent)
                }

            }

            "normal" ->{
                (holder as NormalCategoryViewHolder).onBind(normalList[position])

                if (normalList[position].imageDownLoadUrlList?.isEmpty() == false) {
                    Glide.with(holder.itemView.context)
                        .load(normalList[position].imageDownLoadUrlList?.get(0))
                        .into(holder.binding.itemPostNormalSearchResultImageviewPhoto)
                }



                holder.itemView.setOnClickListener {
                    var intent = Intent(holder.itemView.context, DetailSellViewActivity::class.java)
                    intent.apply {
                        putExtra("uid", normalList[position].uid)
                        putExtra("userId", normalList[position].userId)
                        putExtra("postUid", uidList[position])
                        putExtra("imageList", normalList[position].imageDownLoadUrlList)
                        putExtra("contentTime", normalList[position].time)
                        putExtra("explain", normalList[position].explain)
                        //putExtra("sellerAddress",contentSellDTO[position].sellerAddress)


                    }
                    context?.startActivity(intent)
                }
            }
            else -> throw RuntimeException("nope")
        }
    }

    inner class SellCategoryViewHolder(val binding: ItemPostSellSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ContentSellDTO) {
            binding.itempostsellsearchresult = data
        }
    }

    inner class BuyCategoryViewHolder(val binding: ItemPostBuySearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ContentBuyDTO) {
            binding.itempostbuysearchresult = data
        }
    }

    inner class NormalCategoryViewHolder(val binding : ItemPostNormalSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentNormalDTO){
            binding.itempostnormalsearchresult = data
        }
    }

    inner class ShopCategoryViewHolder(val binding : ItemPostShopSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data: ContentShopDTO){
            binding.itempostshopsearchresult = data
        }
    }

}