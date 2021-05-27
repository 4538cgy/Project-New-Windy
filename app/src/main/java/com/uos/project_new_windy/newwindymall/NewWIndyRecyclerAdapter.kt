package com.uos.project_new_windy.newwindymall

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.project_new_windy.databinding.ItemNewWindyMallMainBinding
import com.uos.project_new_windy.model.mallmodel.MallMainModel

class NewWIndyRecyclerAdapter(private val context : Context, private val dataList : ArrayList<MallMainModel.Product>, dataUidList : ArrayList<String>): RecyclerView.Adapter<NewWindyMain.MallMainRecyclerViewHolder>() {



    class MainMallRecyclerViewHolder(val binding : ItemNewWindyMallMainBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : MallMainModel.Product){
            binding.itemnewwindymallmain = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewWindyMain.MallMainRecyclerViewHolder {
        val binding = ItemNewWindyMallMainBinding.inflate(LayoutInflater.from(context),parent,false)
        return NewWindyMain.MallMainRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: NewWindyMain.MallMainRecyclerViewHolder, position: Int) {
        holder.onBind(dataList[position])
    }
}