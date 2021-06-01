package com.uos.project_new_windy.newwindymall

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.bottomsheet.malloption.BottomSheetDialogMallOption
import com.uos.project_new_windy.databinding.ActivityNewWindyMainBinding
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.model.chatmodel.UserModel
import com.uos.project_new_windy.model.mallmodel.MallMainModel
import java.text.DecimalFormat

class NewWindyMain : AppCompatActivity(), BottomSheetDialogMallOption.BottomSheetButtonClickListener {

    lateinit var binding : ActivityNewWindyMainBinding
    private var isOpenFAB = false
    private var recyclerList = arrayListOf<MallMainModel.Product>()
    private var recyclerUidList = arrayListOf<String>()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var lastvisible : Long ? = null
    private var category : String ? = null
    private var checkGetAllData : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_windy_main)
        binding.activitynewwindymain = this

        if(FirebaseAuth.getInstance().currentUser?.uid.toString().equals("6TzkruCHS2YufEURE9vtK4VdFu52")
            || FirebaseAuth.getInstance().currentUser?.uid.toString().equals("jOMoLi0YgZUJUDypzaXSVQ84cEU2")
            || FirebaseAuth.getInstance().currentUser?.email.toString().equals("hG9W4uIR4dOmLweh2XOHMs9HbBE3")
            || FirebaseAuth.getInstance().currentUser?.email.toString().equals("ay72HtBWTWetM9JYE2VKYlmbYqh2")
            || FirebaseAuth.getInstance().currentUser?.uid.toString().equals("szp72PSHHKakvZLyEMMyBQBqMV83"))
        {
            binding.activityNewWindyMainButtonAddProduct.visibility = View.VISIBLE
        }else {
            binding.activityNewWindyMainButtonAddProduct.visibility = View.GONE

        }

        binding.activityNewWindyMainTextviewCategory.setOnClickListener {
            val bottomSheetDialog : BottomSheetDialogMallOption = BottomSheetDialogMallOption()

            bottomSheetDialog.show(supportFragmentManager,"lol")
        }


        binding.activityNewWindyMainImagebuttonClose.setOnClickListener { finish() }
        binding.activityNewWindyMainImagebuttonOption.setOnClickListener {
            val bottomSheetDialog : BottomSheetDialogMallOption = BottomSheetDialogMallOption()

            bottomSheetDialog.show(supportFragmentManager,"lol")
        }

        binding.activityNewWindyMainButtonAddProduct.setOnClickListener {
            var intent = Intent(binding.root.context,AddProductActivity::class.java)
            intent.apply {
                startActivity(intent)
            }
        }

        //리사이클러뷰 연결
        initRecyclerView()

        //초기 데이터 읽어오기
        initProductData()

        binding.activityNewWindyMainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var j = (binding.activityNewWindyMainRecycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                println("마지막 데이터 $j")
                if (!binding.activityNewWindyMainRecycler.canScrollVertically(1))
                {
                    println("추가로 가져오기")
                    if (checkGetAllData) {
                        getMoreData()
                    }else{
                        getMoreCategoryData()
                    }
                }
            }
        })



    }

    fun getMoreData(){
        recyclerUidList.forEach {
            println("으아아아 $it")
        }
        println("라스트 비지블 $lastvisible")
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").orderBy("timestamp",
            Query.Direction.DESCENDING).startAfter(lastvisible).limit(2).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null){
                if (!querySnapshot.isEmpty)
                {
                    println("가져오기 성공")
                    querySnapshot.forEach {
                        recyclerList.add(it.toObject(MallMainModel.Product::class.java))
                        recyclerUidList.add(it.id)
                        lastvisible = recyclerList[recyclerList.size - 1].timestamp
                    }
                    binding.activityNewWindyMainRecycler.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    fun getCategoryData(){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").orderBy("timestamp",Query.Direction.DESCENDING)
            .whereEqualTo("category",category).limit(1).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                println("카테고리 데이터 조회 시작")
                if (querySnapshot != null){
                    if (!querySnapshot.isEmpty){
                        println("아아아아")

                        querySnapshot.forEach {
                            recyclerList.add(it.toObject(MallMainModel.Product::class.java))
                            recyclerUidList.add(it.id)
                            lastvisible = recyclerList[recyclerList.size - 1].timestamp
                        }
                        binding.activityNewWindyMainRecycler.adapter!!.notifyDataSetChanged()
                    }
                }else{
                    println("가져ㅑ올 데이터가음슴")
                    recyclerList.clear()
                    recyclerUidList.clear()
                    binding.activityNewWindyMainRecycler.adapter!!.notifyDataSetChanged()
                }
            }
    }

    fun getMoreCategoryData(){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").orderBy("timestamp",Query.Direction.DESCENDING)
            .whereEqualTo("category",category).startAfter(lastvisible).limit(1).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot != null){
                    if (!querySnapshot.isEmpty){
                        recyclerList.clear()
                        recyclerUidList.clear()
                        querySnapshot.forEach {
                            recyclerList.add(it.toObject(MallMainModel.Product::class.java))
                            recyclerUidList.add(it.id)
                            lastvisible = recyclerList[recyclerList.size - 1].timestamp
                        }
                        binding.activityNewWindyMainRecycler.adapter!!.notifyDataSetChanged()
                    }
                }
            }
    }



    fun initProductData(){
        FirebaseFirestore.getInstance().collection("Mall").document("product").collection("product").orderBy("timestamp",
                Query.Direction.DESCENDING).limit(1).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null){
                if (!querySnapshot.isEmpty)
                {
                    recyclerList.clear()
                    recyclerUidList.clear()
                    querySnapshot.forEach {

                        recyclerList.add(it.toObject(MallMainModel.Product::class.java))
                        recyclerUidList.add(it.id)
                        lastvisible = recyclerList[recyclerList.size - 1].timestamp
                    }
                    binding.activityNewWindyMainRecycler.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    fun initRecyclerView(){
        binding.activityNewWindyMainRecycler.adapter = MallMainRecyclerViewAdapter()
        binding.activityNewWindyMainRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
    }
    fun openCart(view : View){
        startActivity(Intent(binding.root.context,MallCartActivity::class.java))
    }
    fun openOrderInfo(view : View){
        startActivity(Intent(binding.root.context,OrderInfoActivity::class.java))
    }

    fun clickFab(view : View){
        isOpenFAB = if (!isOpenFAB) {
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewOrderInfo, "translationY", -400f)
                .apply { start() }

            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabOrderInfo, "translationY", -400f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabCart, "translationY", -200f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewCart, "translationY", -200f)
                .apply { start() }
            binding.activityNewWindyMainTextviewOrderInfo.visibility = View.VISIBLE
            binding.activityNewWindyMainTextviewCart.visibility = View.VISIBLE
            true
        } else {
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewOrderInfo, "translationY", -0f)
                .apply { start() }

            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabOrderInfo, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewCart, "translationY", -0f)
                .apply { start() }

            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabCart, "translationY", -0f)
                .apply { start() }

            binding.activityNewWindyMainTextviewOrderInfo.visibility = View.INVISIBLE
            binding.activityNewWindyMainTextviewCart.visibility = View.INVISIBLE
            false
        }
    }

    override fun onBottomSheetButtonClick(text: String) {
        category = text
        binding.activityNewWindyMainTextviewCategory.text = category
        if (category == "전체상품"){
            println("전체 상품 조회")
            checkGetAllData = true
            initProductData()
        }else{
            println(category + "상품 조회")
            checkGetAllData = false
            getCategoryData()
        }
    }

    inner class MallMainRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemNewWindyMallMainBinding.inflate(LayoutInflater.from(binding.root.context),parent,false)
            return MallMainRecyclerViewHolder(binding)
        }

        override fun getItemCount(): Int {
            if (recyclerList != null){
                return recyclerList.size
            }else{
                return 0
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as MallMainRecyclerViewHolder).onBind(recyclerList[position])

            holder.binding.itemNewWindyMallMainBookmark.setOnClickListener {
                addOnCart(recyclerUidList[position],holder)
            }
            holder.binding.itemNewWindyMallMainTextviewTitle.text = recyclerList[position].title

            holder.itemView.setOnClickListener {
                var intent = Intent(binding.root.context,MallDetailProductActivity::class.java)
                intent.apply {
                    putExtra("product",recyclerUidList[position])
                    holder.binding.root.context.startActivity(intent)
                }
            }

            var format = DecimalFormat("###,###");

            holder.binding.itemNewWindyMallMainCost.text = "가격 " + format.format(recyclerList[position].cost) + "원"



            Glide.with(holder.binding.root.context)
                .load(recyclerList[position].imageUrlList!![0])
                .centerCrop()
                .thumbnail(0.01f)
                .into(holder.binding.itemNewWindyMallMainImageview)

            if (recyclerList[position].review != null && recyclerList[position].review.size > 0) {
                holder.binding.itemNewWindyMallMainReviewCount.text =
                    recyclerList[position].review.keys.size.toString() + "개의 구매 리뷰 보러가기"
            }else{
                holder.binding.itemNewWindyMallMainReviewCount.text = "리뷰가 아직 없어요. 상품을 구매하고 리뷰를 작성해보세요."
            }

            holder.binding.itemNewWindyMallMainReviewCount.setOnClickListener {
                if (recyclerList[position].review!=null && recyclerList[position].review.size >0){
                    //리뷰 페이지로 이동
                    println("리뷰 클릭")
                    var intent = Intent(binding.root.context,ReviewActivity::class.java)
                    intent.apply {
                        putExtra("product",recyclerUidList[position])
                        startActivity(intent)
                    }
                }
            }

            checkFavorite(recyclerUidList[position],holder,true)

        }

        fun checkFavorite(productId: String,holder: MallMainRecyclerViewHolder, check: Boolean){
            println("체크")
            db.collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser!!.uid).document("cart")
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot!=null){
                        if (documentSnapshot.exists())
                        {
                            var data = documentSnapshot.toObject(MallMainModel.CartDTO::class.java)

                            if (data!!.productId.keys.contains(productId)){
                                println("있음")
                                holder.binding.itemNewWindyMallMainBookmark.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                            }else{
                                println("없음")
                                holder.binding.itemNewWindyMallMainBookmark.setBackgroundResource(R.drawable.ic_baseline_favorite_border_242)
                            }
                            if (!check) {
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
        }

        fun addOnCart(productId : String, holder: MallMainRecyclerViewHolder){
            println(" 장바구니 담기 실행 ")
            val tsDocSubscribing = db.collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser!!.uid).document("cart")

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
                    checkFavorite(productId = productId,holder = holder, check = false)
                    return@runTransaction
                }

                if (cart!!.productId.containsKey(productId)){
                    println("있는데 중복됨")
                    cart.productCount = cart.productCount!! - 1
                    cart.productId.remove(productId)
                    transaction.set(tsDocSubscribing,cart)
                    checkFavorite(productId = productId,holder = holder, check = false)
                    return@runTransaction
                }else{
                    println("없음2")
                    cart.productId.put(productId,true)
                    cart.productCount = cart.productCount!! + 1
                    transaction.set(tsDocSubscribing,cart)
                    checkFavorite(productId = productId,holder = holder, check = false)
                    return@runTransaction
                }

            }.addOnSuccessListener {
                println("완료")
            }.addOnFailureListener {
                println("실패 ${it.toString()}")
            }
        }

    }
    class MallMainRecyclerViewHolder(val binding : ItemNewWindyMallMainBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product){
            binding.itemnewwindymallmain = data
        }
    }
}