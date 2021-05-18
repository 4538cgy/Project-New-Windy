package com.uos.project_new_windy.newwindymall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.bottomsheet.malloption.BottomSheetDialogMallOption
import com.uos.project_new_windy.databinding.ActivityNewWindyMainBinding
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class NewWindyMain : AppCompatActivity(), BottomSheetDialogMallOption.BottomSheetButtonClickListener {

    lateinit var binding : ActivityNewWindyMainBinding

    private var recyclerList = arrayListOf<MallMainModel.Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_windy_main)
        binding.activitynewwindymain = this

        binding.activityNewWindyMainImagebuttonClose.setOnClickListener { finish() }
        binding.activityNewWindyMainImagebuttonOption.setOnClickListener {
            val bottomSheetDialog : BottomSheetDialogMallOption = BottomSheetDialogMallOption()

            bottomSheetDialog.show(supportFragmentManager,"lol")
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