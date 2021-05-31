package com.uos.project_new_windy.newwindymall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallCartBinding
import com.uos.project_new_windy.databinding.ItemMallCartBinding
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import java.text.DecimalFormat

class MallCartActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallCartBinding

    private var recyclerDataList = arrayListOf<MallMainModel.Product>()
    private var recyclerIdList = arrayListOf<String>()
    private var orderList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_cart)
        binding.activitymallcart = this

        binding.activityMallCartImagebuttonClose.setOnClickListener { finish() }
        binding.activityMallCartButtonOrder.setOnClickListener {
            var intent = Intent(binding.root.context,BillActivity::class.java)
            intent.apply {
                putExtra("productList",orderList)
                startActivity(intent)
            }

        }
        binding.activityMallCartRecyclerview.adapter = CartRecyclerViewAdapter()
        binding.activityMallCartRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

        //장바구니 데이터 가져오기
        getCartData()
    }

    fun getCartData(){
        FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser!!.uid).document("cart")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null){
                    if (documentSnapshot.exists()){
                        var cartDTO = documentSnapshot.toObject(MallMainModel.CartDTO::class.java)
                        getProductData(cartDTO!!)
                    }
                }
            }
    }

    fun getProductData(data : MallMainModel.CartDTO){
        var count = 0
        data.productId.keys.forEach {
            FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").document(it.toString())
                .get().addOnSuccessListener {
                    recyclerDataList.add(it.toObject(MallMainModel.Product::class.java)!!)
                    recyclerIdList.add(it.id)
                    count ++
                    if (count == data.productId.keys.size){
                        binding.activityMallCartRecyclerview.adapter!!.notifyDataSetChanged()
                    }
                }
        }

    }

    inner class CartRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemMallCartBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return CartRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (recyclerDataList != null){
                return recyclerDataList.size
            }else{
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CartRecyclerViewHolder).onBind(recyclerDataList[position])

            holder.binding.itemMallCartTextviewTitle.text = recyclerDataList[position].title.toString()
            holder.binding.itemMallCartTextviewCost.text = recyclerDataList[position].cost.toString()

            Glide.with(holder.binding.root.context)
                .load(recyclerDataList[position].imageUrlList!![0])
                .centerInside()
                .thumbnail(0.01f)
                .into(holder.binding.itemMallCartImageviewProductImage)

            holder.binding.itemMallCartCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    orderList.add(recyclerIdList[position])
                }else{
                    orderList.remove(recyclerIdList[position])
                }
            }

        }

    }
    class CartRecyclerViewHolder(val binding : ItemMallCartBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product){
            binding.itemmallcart = data
        }
    }
}