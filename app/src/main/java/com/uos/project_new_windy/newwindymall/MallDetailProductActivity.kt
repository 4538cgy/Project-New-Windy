package com.uos.project_new_windy.newwindymall

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.ActivityMallDetailProductBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentSellRecyclerViewAdapter
import com.uos.project_new_windy.util.DisplaySize

class MallDetailProductActivity : AppCompatActivity() {

    lateinit var binding : ActivityMallDetailProductBinding

    var product : MallMainModel.Product ? = null
    var productId : String ?= null

    var addCartCheck : Boolean ? = false
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mall_detail_product)
        binding.activitymalldetailproduct = this
        var displaySize = ObservableField(DisplaySize(this))
        productId = intent.getStringExtra("product").toString()
        getData(productId!!)

        binding.activityMallDetailImagebuttonClose.setOnClickListener { finish() }

        //binding.activityMallDetailProductViewpager.adapter = photoAdapter(product!!.imageUrlList!!)



        binding.activityMallDetailProductButtonAddCart.setOnClickListener { addOnCart(productId.toString()) }
        binding.activityMallDetailProductBuy.setOnClickListener {
            var intent = Intent(binding.root.context, BillActivity::class.java)
            intent.apply {
                putExtra("product", productId)
                putExtra("type","single")
                startActivity(intent)
            } }

        checkFavorite(productId!!,false)
    }

    fun getData(productId : String){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").document(productId)
            .get().addOnSuccessListener {
                if (it != null){
                    if (it.exists()){
                        var data = it.toObject(MallMainModel.Product::class.java)
                        product = data

                        binding.activityMallDetailProductTextviewTitle.text = data!!.title
                        binding.activityMallDetailProductTextviewExplain.text = data!!.explain
                        binding.activityMallDetailProductTextviewCost.text = data!!.cost.toString() + "원"



                        binding.activityMallDetailProductViewpager.adapter = photoAdapter(product!!.imageUrlList!!)


                    }
                }
            }
    }
    fun checkFavorite(productId: String, check: Boolean){
        println("체크")
        db.collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser!!.uid).document("cart")
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot!=null){
                    if (documentSnapshot.exists())
                    {
                        var data = documentSnapshot.toObject(MallMainModel.CartDTO::class.java)

                        if (data!!.productId.keys.contains(productId)){
                            println("있음")
                            binding.activityMallDetailProductButtonAddCart.text = "장바구니에서 제외"
                            addCartCheck = true
                        }else{
                            println("없음")
                            binding.activityMallDetailProductButtonAddCart.text = "장바구니 담기"
                            addCartCheck = false
                        }
                        if (!check) {

                        }
                    }
                }
            }
    }

    fun addOnCart(productId : String){
        println(" 장바구니 담기 실행 ")
        val tsDocSubscribing = db.collection("userInfo").document("userData").collection(
            FirebaseAuth.getInstance().currentUser!!.uid).document("cart")

        db.runTransaction {
                transaction ->
            var cart = transaction.get(tsDocSubscribing).toObject(MallMainModel.CartDTO::class.java)
            println("카트트트트트 ${cart.toString()}")
            println("유저 아이디 ${FirebaseAuth.getInstance().currentUser!!.uid}")
            if (cart == null){
                println("없음")
                cart = MallMainModel.CartDTO()
                cart.productId.put(productId,true)
                cart.productCount = 1
                println("으아아 ${cart.toString()}")
                transaction.set(tsDocSubscribing,cart)
                checkFavorite(productId = productId, check = false)
                return@runTransaction
            }

            if (cart!!.productId.containsKey(productId)){
                println("있는데 중복됨")
                cart.productCount = cart.productCount!! - 1
                cart.productId.remove(productId)
                transaction.set(tsDocSubscribing,cart)
                checkFavorite(productId = productId, check = false)
                return@runTransaction
            }else{
                println("없음2")
                cart.productId.put(productId,true)
                cart.productCount = cart.productCount!! + 1
                transaction.set(tsDocSubscribing,cart)
                checkFavorite(productId = productId, check = false)
                return@runTransaction
            }

        }.addOnSuccessListener {
            println("완료")
        }.addOnFailureListener {
            println("실패 ${it.toString()}")
        }
    }

    inner class photoAdapter(var photoList : ArrayList<String>) : RecyclerView.Adapter<ViewHolder>(){



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content_photo,parent,false))


        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(binding.root.context)
                .load(photoList[position])
                .placeholder(R.drawable.ic_sand_clock)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(
                    Glide.with(binding.root.context).load(photoList[position]).fitCenter()
                )
                .into(holder.imageUrl)

        }

        override fun getItemCount(): Int = photoList?.size!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageUrl : ImageView = view.findViewById(R.id.item_content_photo_imageview)
    }
}