package com.uos.project_new_windy.newwindymall

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.bottomsheet.malloption.BottomSheetDialogMallOption
import com.uos.project_new_windy.databinding.ActivityNewWindyMainBinding
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class NewWindyMain : AppCompatActivity(), BottomSheetDialogMallOption.BottomSheetButtonClickListener {

    lateinit var binding : ActivityNewWindyMainBinding
    private var isOpenFAB = false
    private var recyclerList = arrayListOf<MallMainModel.Product>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_windy_main)
        binding.activitynewwindymain = this

        if(FirebaseAuth.getInstance().currentUser?.uid.toString().equals("6TzkruCHS2YufEURE9vtK4VdFu52")
            || FirebaseAuth.getInstance().currentUser?.uid.toString().equals("jOMoLi0YgZUJUDypzaXSVQ84cEU2")
            || FirebaseAuth.getInstance().currentUser?.email.toString().equals("hG9W4uIR4dOmLweh2XOHMs9HbBE3")
            || FirebaseAuth.getInstance().currentUser?.email.toString().equals("ay72HtBWTWetM9JYE2VKYlmbYqh2")
            || FirebaseAuth.getInstance().currentUser?.uid.toString().equals("dZvFUbfbL9NZ5SYygiFsmSrAmM63"))
        {
            binding.activityNewWindyMainButtonAddProduct.visibility = View.VISIBLE
        }else {
            binding.activityNewWindyMainButtonAddProduct.visibility = View.GONE

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

        binding.activityNewWindyMainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.activityNewWindyMainRecycler.canScrollVertically(1))
                {
                    println("끝에 도달")
                }
            }
        })

        //리사이클러뷰 초기화
        initRecyclerView()
    }

    fun initRecyclerView(){
        binding.activityNewWindyMainRecycler.adapter = MallMainRecyclerViewAdapter()
        binding.activityNewWindyMainRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
    }

    fun clickFab(view : View){
        isOpenFAB = if (!isOpenFAB) {
            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabCart, "translationY", -200f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewCart, "translationY", -200f)
                .apply { start() }

            binding.activityNewWindyMainTextviewCart.visibility = View.VISIBLE
            true
        } else {
            ObjectAnimator.ofFloat(binding.activityNewWindyMainTextviewCart, "translationY", -0f)
                .apply { start() }

            ObjectAnimator.ofFloat(binding.activityNewWindyMainFabCart, "translationY", -0f)
                .apply { start() }

            binding.activityNewWindyMainTextviewCart.visibility = View.INVISIBLE
            false
        }
    }

    override fun onBottomSheetButtonClick(text: String) {
        println("으에에 $text")
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


        }

    }
    class MallMainRecyclerViewHolder(val binding : ItemNewWindyMallMainBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product){
            binding.itemnewwindymallmain = data
        }
    }
}