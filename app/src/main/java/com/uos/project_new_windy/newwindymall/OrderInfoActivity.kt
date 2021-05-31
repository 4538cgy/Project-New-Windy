package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityOrderInfoBinding
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.databinding.ItemOrderHistoryBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.util.TimeUtil
import java.text.DecimalFormat

class OrderInfoActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderInfoBinding
    private var recyclerList = arrayListOf<MallMainModel.OrderHistory>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_info)
        binding.activityorderinfo = this

        binding.activityOrderInfoImagebuttonClose.setOnClickListener { finish() }

        binding.activityOrderInfoRecyclerview.adapter = OrderHistoryRecyclerViewAdapter()
        binding.activityOrderInfoRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

        getData()
    }

    fun getData(){
        FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser!!.uid).document("order")
            .collection("orderProduct").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot!=null){
                    if (!querySnapshot.isEmpty){
                        var data = querySnapshot.toObjects(MallMainModel.OrderHistory::class.java)
                        data.forEach {
                            recyclerList.add(it)
                        }
                        binding.activityOrderInfoRecyclerview.adapter!!.notifyDataSetChanged()
                    }
                }
            }
    }

    inner class OrderHistoryRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return OrderHistoryRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (recyclerList != null){
                return recyclerList.size
            }else{
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as OrderHistoryRecyclerViewHolder).onBind(recyclerList[position])

            recyclerList[position].productList.forEach {
                if (recyclerList[position].productList.size > 1) {
                    holder.binding.itemOrderHistoryTextviewTitle.text = it.value.title + " 외 " + recyclerList[position].productList.size + "개"
                }else {
                    holder.binding.itemOrderHistoryTextviewTitle.text = it.value.title
                }
                return@forEach
            }

            holder.binding.itemOrderHistoryTextviewCost.text = "주문 금액 " + recyclerList[position].cost.toString()
            holder.binding.itemOrderHistoryOrderTextviewTimestamp.text = TimeUtil().formatTimeString(
                recyclerList[position].timestamp!!
            )
            


        }



    }
    class OrderHistoryRecyclerViewHolder(val binding : ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.OrderHistory){
            binding.itemorderhistory = data
        }
    }
}