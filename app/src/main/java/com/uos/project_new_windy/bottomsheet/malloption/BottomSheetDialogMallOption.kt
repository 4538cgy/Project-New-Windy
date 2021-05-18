package com.uos.project_new_windy.bottomsheet.malloption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogWriteCategory
import com.uos.project_new_windy.databinding.BottomSheetSelectCategoryBinding
import com.uos.project_new_windy.databinding.FragmentBottomSheetDialogMallOptionBinding
import com.uos.project_new_windy.navigationlobby.*
import java.lang.ClassCastException


class BottomSheetDialogMallOption : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding : FragmentBottomSheetDialogMallOptionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bottom_sheet_dialog_mall_option,container,false)
        binding.fragmentbottomsheetdialogmall = this

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
            binding.fragmentBottomSheetDialogMallOptionTextview1.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("인기매물")
            }
            binding.fragmentBottomSheetDialogMallOptionTextview2.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("농산물")
            }
            binding.fragmentBottomSheetDialogMallOptionTextview3.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("축산물")
            }
            binding.fragmentBottomSheetDialogMallOptionTextview4.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("소모품")
            }
            binding.fragmentBottomSheetDialogMallOptionTextview5.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("농지")
            }
            binding.fragmentBottomSheetDialogMallOptionTextview6.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("가구")
            }
            binding.fragmentBottomSheetDialogMallOptionTextviewEtc.id -> {
                bottomSheetButtonClickListener.onBottomSheetButtonClick("기타")
            }




        }
        dismiss()
    }

}