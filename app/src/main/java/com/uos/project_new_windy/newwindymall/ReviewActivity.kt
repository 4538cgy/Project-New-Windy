package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityReviewBinding
import com.uos.project_new_windy.databinding.ItemOrderHistoryBinding
import com.uos.project_new_windy.databinding.ItemProductReviewBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.util.TimeUtil

class ReviewActivity : AppCompatActivity() {

    lateinit var binding : ActivityReviewBinding

    private var reviewList = arrayListOf<MallMainModel.Product.Review>()
    private var destinationProductId : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_review)
        binding.activityreview = this

        destinationProductId = intent.getStringExtra("product")

        binding.activityReviewImagebuttonClose.setOnClickListener { finish() }

        initRecyclerViewAdapter()

        getReviewData()
    }

    fun getReviewData(){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").document(
            destinationProductId.toString()).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            if (documentSnapshot != null){
                if (documentSnapshot.exists()){
                    var data = documentSnapshot.toObject(MallMainModel.Product::class.java)
                    data!!.review.forEach {
                        reviewList.add(it.value)
                    }

                    binding.activityReviewRecycler.adapter!!.notifyDataSetChanged()

                }
            }
        }
    }

    fun initRecyclerViewAdapter(){
        binding.activityReviewRecycler.adapter = ReviewRecyclerViewAdapter()
        binding.activityReviewRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
    }

    inner class ReviewRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemProductReviewBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return ReviewRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (reviewList != null){
                return reviewList.size
            }else{
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ReviewRecyclerViewHolder).onBind(reviewList[position])
        }
    }
    class ReviewRecyclerViewHolder(val binding : ItemProductReviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product.Review){
            binding.itemproductreview = data
        }
    }
}