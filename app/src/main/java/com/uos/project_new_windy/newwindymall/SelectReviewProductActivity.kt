package com.uos.project_new_windy.newwindymall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivitySelectReviewProductBinding
import com.uos.project_new_windy.databinding.ItemOrderHistoryBinding
import com.uos.project_new_windy.databinding.ItemSelectProductReviewBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.util.TimeUtil

class SelectReviewProductActivity : AppCompatActivity() {

    lateinit var binding : ActivitySelectReviewProductBinding
    private var recyclerList  = arrayListOf<MallMainModel.Product>()
    private var productIdList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_select_review_product)
        binding.activityselectreviewproduct = this

        productIdList = intent.getStringArrayListExtra("product")

        binding.activitySelectReviewProductImagebuttonClose.setOnClickListener { finish() }

        initRecyclerView()

        getData()
    }

    fun getData(){
        productIdList.forEach {
            FirebaseFirestore.getInstance().collection("Mall").document("product")
                .collection("product").document(it).get().addOnSuccessListener {
                    if(it != null){
                        if (it.exists()){
                            var data = it.toObject(MallMainModel.Product::class.java)
                            recyclerList.add(data!!)
                            binding.activitySelectReviewProductRecycler.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    fun initRecyclerView(){
        binding.activitySelectReviewProductRecycler.adapter = SelectProductRecyclerViewAdapter()
        binding.activitySelectReviewProductRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
    }

    inner class SelectProductRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemSelectProductReviewBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return SelectProductRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (recyclerList != null){
                return recyclerList.size
            }else{
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as SelectProductRecyclerViewHolder).onBind(recyclerList[position])

            holder.binding.itemSelectProductReviewTextviewTitle.text = recyclerList[position].title
            holder.binding.itemSelectProductReviewTextviewCost.text = recyclerList[position].cost.toString()
            holder.binding.itemSelectProductReviewButtonReviewWrite.setOnClickListener {
                
                //이미 리뷰를 작성했는지 검증해야ㅕ함
                if (!recyclerList[position].review.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    var intent = Intent(binding.root.context, AddReviewActivity::class.java)
                    intent.apply {
                        putExtra("product", productIdList[position])
                        startActivity(intent)
                        finish()
                    }
                }else{
                    //이미 리뷰를 작성해서 작성 못함 ㅅㄱ
                }
            }



        }



    }
    class SelectProductRecyclerViewHolder(val binding : ItemSelectProductReviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product){
            binding.itemselectproductreview = data
        }
    }
}