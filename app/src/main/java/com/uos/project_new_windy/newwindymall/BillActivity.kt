package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityBillBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class BillActivity : AppCompatActivity() {

    lateinit var binding : ActivityBillBinding
    private var orderProductList = arrayListOf<String>()
    private var productList = arrayListOf<MallMainModel.Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bill)

        orderProductList = intent.getStringArrayListExtra("productList")



        binding.activityBillImagebuttonClose.setOnClickListener { finish() }

        getData()
    }

    fun getData(){
        orderProductList.forEach {
            FirebaseFirestore.getInstance().collection("Mall").document("product")
                .collection("product").document().get().addOnSuccessListener {
                    productList.add(it.toObject(MallMainModel.Product::class.java)!!)
                }

        }
    }
}