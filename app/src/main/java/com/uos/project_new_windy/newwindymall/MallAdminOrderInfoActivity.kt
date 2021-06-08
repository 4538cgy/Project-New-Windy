package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallAdminOrderInfoBinding
import com.uos.project_new_windy.databinding.ItemAdminOrderListBinding
import com.uos.project_new_windy.databinding.ItemBillProductListBinding
import com.uos.project_new_windy.databinding.ItemOrderHistoryBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class MallAdminOrderInfoActivity : AppCompatActivity() {
    lateinit var binding : ActivityMallAdminOrderInfoBinding

    var oderInfoList = arrayListOf<MallMainModel.OrderHistory>()
    var productList = arrayListOf<MallMainModel.Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_admin_order_info)
        binding.activityamlladminorderinfo = this
        binding.activityMallAdminOrderInfoImagebuttonClose.setOnClickListener { finish() }

        binding.activityMallAdminOrderInfoRecyclerview.adapter = OrderInfoRecyclerViewAdapter()
        binding.activityMallAdminOrderInfoRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

        getData()
    }

    fun getData(){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("orderList").orderBy("timestamp",Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                if (querySnapshot != null){
                    if (!querySnapshot.isEmpty)
                    {
                        querySnapshot.forEach {
                            oderInfoList.add(it.toObject(MallMainModel.OrderHistory::class.java))
                            (it.toObject(MallMainModel.OrderHistory::class.java).productList).forEach {
                                productList.add(it.value)
                            }
                            binding.activityMallAdminOrderInfoRecyclerview.adapter!!.notifyDataSetChanged()
                        }
                    }
                }

            }
    }


    inner class OrderInfoRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemAdminOrderListBinding.inflate(
                LayoutInflater.from(binding.root.context),
                parent,
                false
            )
            return OrderInfoRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (oderInfoList != null) {
                return oderInfoList.size
            } else {
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as OrderInfoRecyclerViewHolder).onBind(oderInfoList[position])

            holder.binding.itemAdminOrderListButtonComplete.setOnClickListener { println("발송완료") }
            holder.binding.itemAdminOrderListTextviewAddress.text = oderInfoList[position].address + " " + oderInfoList[position].detailAddress
            holder.binding.itemAdminOrderListTextviewNickname.text = oderInfoList[position].orderName
            holder.binding.itemAdminOrderListTextviewTimestamp.text = oderInfoList[position].timestamp.toString()
            holder.binding.itemAdminOrderListTextviewPhonenumber.text = oderInfoList[position].phoneNumber
            holder.binding.itemAdminOrderListTextviewCost.text = oderInfoList[position].cost.toString()
            holder.binding.itemAdminOrderListRecyclerview.adapter = BillProductRecyclerViewAdapter()
            holder.binding.itemAdminOrderListRecyclerview.layoutManager = LinearLayoutManager(holder.binding.root.context,LinearLayoutManager.VERTICAL,false)



        }

    }



    class OrderInfoRecyclerViewHolder(val binding: ItemAdminOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MallMainModel.OrderHistory) {
            binding.itemadminorderlist = data
        }
    }

    inner class BillProductRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemBillProductListBinding.inflate(
                LayoutInflater.from(binding.root.context),
                parent,
                false
            )
            return BillProductRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (productList != null) {
                return productList.size
            } else {
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as BillProductRecyclerViewHolder).onBind(productList[position])

            holder.binding.itemBillProductListTextviewTitle.text = productList[position].title
            holder.binding.itemBillProductListTextviewCost.text =
                productList[position].cost.toString()


        }

    }

    class BillProductRecyclerViewHolder(val binding: ItemBillProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MallMainModel.Product) {
            binding.itembillproductlist = data
        }
    }
}