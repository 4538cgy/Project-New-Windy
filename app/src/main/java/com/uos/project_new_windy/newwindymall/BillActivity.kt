package com.uos.project_new_windy.newwindymall

import android.app.Activity
import android.content.Intent
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
import com.uos.project_new_windy.SearchAddressActivity
import com.uos.project_new_windy.databinding.ActivityBillBinding
import com.uos.project_new_windy.databinding.ItemBillProductListBinding
import com.uos.project_new_windy.databinding.ItemMallCartBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.navigationlobby.UserFragment

class BillActivity : AppCompatActivity() {

    lateinit var binding: ActivityBillBinding
    private var orderProductList = arrayListOf<String>()
    private var productList = arrayListOf<MallMainModel.Product>()
    private var totalAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill)


        if (intent.getStringExtra("type") == "single"){

            orderProductList.add(intent.getStringExtra("product"))
        }else{
            orderProductList = intent.getStringArrayListExtra("productList")

        }

        binding.activityBillButtonOrderComplete.setOnClickListener {
            setOrderData()
            startActivity(Intent(binding.root.context, OrderCompleteActivity::class.java))
            finish()
        }
        binding.activityBillImagebuttonClose.setOnClickListener { finish() }
        binding.activityBillRecycler.adapter = BillProductRecyclerViewAdapter()
        binding.activityBillRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        binding.activityBillEdittextAddress.setOnClickListener {
            startActivityForResult(
                Intent(
                    binding.root.context,
                    SearchAddressActivity::class.java
                ), 100
            )
        }

        getData()
    }

    fun setOrderData() {

        var data = MallMainModel.OrderHistory()
        data.address = totalAddress
        data.cost = cost()
        data.deliverOption = binding.activityBillEdittextDeliveryOption.text.toString()
        data.orderUid = FirebaseAuth.getInstance().currentUser!!.uid
        data.detailAddress = binding.activityBillEdittextDetailAddress.text.toString()
        data.phoneNumber
        data.timestamp = System.currentTimeMillis()
        productList.forEachIndexed { index, product ->
            data.productList.put(orderProductList[index], product)
        }


        FirebaseFirestore.getInstance().collection("userInfo").document("userData")
            .collection(FirebaseAuth.getInstance().currentUser!!.uid).document("order")
            .collection("orderProduct")
            .add(data)
            .addOnSuccessListener {
                println("성공")
            }.addOnFailureListener {
                println("실패")
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    binding.activityBillEdittextAddress.setText(
                        data!!.getStringExtra("address_arg1")
                            .toString() +
                                data!!.getStringExtra("address_arg2").toString() +
                                data!!.getStringExtra("address_arg3").toString()
                    )

                    totalAddress = data!!.getStringExtra("address_arg1")
                        .toString() +
                            data!!.getStringExtra("address_arg2").toString() +
                            data!!.getStringExtra("address_arg3").toString()
                }
            }
        }
    }

    fun cost(): Long {
        var totalCost: Long? = 0
        productList.forEach {
            totalCost = totalCost!! + it.cost!!
        }

        return totalCost!!
    }

    fun getData() {
        orderProductList.forEach {
            FirebaseFirestore.getInstance().collection("Mall").document("product")
                .collection("product").document(it).get().addOnSuccessListener {
                    if (it != null) {
                        if (it.exists()) {
                            productList.add(it.toObject(MallMainModel.Product::class.java)!!)
                            binding.activityBillTextviewTotalCost.text =
                                "최종 결제 금액 : " + cost().toString() + "원"
                            binding.activityBillRecycler.adapter!!.notifyDataSetChanged()
                        }
                    }
                }

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