package com.uos.project_new_windy.bottomsheet.searchbottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uos.project_new_windy.R
import com.uos.project_new_windy.bottomsheet.BottomSheetDialogContentOption
import com.uos.project_new_windy.databinding.BottomSheetDialogSearchOptionAddressBinding
import java.lang.ClassCastException

class BottomSheetDialogSearchOptionAddress : BottomSheetDialogFragment() {

    lateinit var bottomSheetContentOptionButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetDialogSearchOptionAddressBinding

    interface BottomSheetButtonClickListener {
        fun onBottomSheetButtonClick(text: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_search_option_address,container,false)
        binding.bottomsheetdialogsearchoptionaddress = this@BottomSheetDialogSearchOptionAddress


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            bottomSheetContentOptionButtonClickListener = context as BottomSheetButtonClickListener

        } catch (e: ClassCastException) {
            Log.d("bottomsheet", "Click listener onAttach Error")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    fun groupClick(view: View) {



    }


}