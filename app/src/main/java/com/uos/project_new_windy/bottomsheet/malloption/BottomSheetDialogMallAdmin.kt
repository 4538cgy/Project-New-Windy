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
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentBoottomSheetDialogMallAdminBinding
import com.uos.project_new_windy.newwindymall.MallAdminActivity
import java.lang.ClassCastException


class BottomSheetDialogMallAdmin : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener: BottomSheetButtonClickListener

    lateinit var binding : FragmentBoottomSheetDialogMallAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_boottom_sheet_dialog_mall_admin,container,false)
        binding.fragmentbottomsheetdialogmalladmin = this

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
            binding.fragmentBottomSheetDialogMallAdminTextviewOrderinfo.id -> {
                println("주문 정보")
            }
            binding.fragmentBottomSheetDialogMallAdminTextviewAdmin.id ->{
                startActivity(Intent(binding.root.context,MallAdminActivity::class.java))
            }
        }
        dismiss()
    }
}