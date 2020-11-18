package com.uos.project_new_windy.bottomsheet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.BottomSheetSelectCategoryBinding
import com.uos.project_new_windy.navigationlobby.AddContentActivity
import java.lang.ClassCastException

class BottomSheetDialogWriteCategory :BottomSheetDialogFragment(){

    lateinit var bottomSheetButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetSelectCategoryBinding

    override fun onCreateView(
         inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_select_category,container,false)
        binding.bottomsheet = this@BottomSheetDialogWriteCategory
        //val view = inflater.inflate(R.layout.bottom_sheet_select_category,container,false)

        return binding.root

    }

    interface BottomSheetButtonClickListener{
        fun onBottomSheetButtonClick(text : String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            bottomSheetButtonClickListener = context as BottomSheetButtonClickListener

        }catch (e : ClassCastException){
            Log.d("bottomsheet","Click listener onAttach Error")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    fun groupClick(view : View){

        when(view.id){
            binding.bottomSheetSelectCategoryViewgroup1.id -> startActivity(Intent(binding.bottomsheet?.context,AddContentActivity::class.java))
            binding.bottomSheetSelectCategoryViewgroup2.id -> startActivity(Intent(binding.bottomsheet?.context,AddContentActivity::class.java))
            binding.bottomSheetSelectCategoryViewgroup3.id -> startActivity(Intent(binding.bottomsheet?.context,AddContentActivity::class.java))


        }
    }



}