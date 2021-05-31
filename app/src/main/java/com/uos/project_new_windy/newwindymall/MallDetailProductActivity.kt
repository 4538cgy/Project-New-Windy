package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallDetailProductBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class MallDetailProductActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallDetailProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_detail_product)
        binding.activitymalldetailproduct = this

        getData(intent.getStringExtra("product"))

        binding.activityMallDetailImagebuttonClose.setOnClickListener { finish() }

        binding.activityMallDetailProductButtonAddCart.setOnClickListener {  }
        binding.activityMallDetailProductBuy.setOnClickListener {  }
    }

    fun getData(productId : String){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").document(productId)
            .get().addOnSuccessListener {
                if (it != null){
                    if (it.exists()){
                        var data = it.toObject(MallMainModel.Product::class.java)

                        binding.activityMallDetailProductTextviewTitle.text = data!!.title
                        binding.activityMallDetailProductTextviewExplain.text = data!!.explain
                        binding.activityMallDetailProductTextviewCost.text = data!!.cost.toString() + "원"


                    }
                }
            }
    }

    class ViewpagerAdapter(var items : ArrayList<String> = arrayListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }

    }
}