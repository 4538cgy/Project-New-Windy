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
import com.google.firebase.auth.FirebaseAuth
import com.uos.project_new_windy.R
import com.uos.project_new_windy.ReportActivity
import com.uos.project_new_windy.databinding.BottomSheetSelectCategoryBinding
import com.uos.project_new_windy.databinding.BottomSheetSelectContentOptionBinding
import com.uos.project_new_windy.navigationlobby.AddBuyContentActivity
import com.uos.project_new_windy.navigationlobby.AddContentActivity
import com.uos.project_new_windy.navigationlobby.AddSellContentActivity
import java.lang.ClassCastException

class BottomSheetDialogContentOption : BottomSheetDialogFragment(){

    lateinit var bottomSheetContentOptionButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetSelectContentOptionBinding

    var uid : String ? = null
    var destinationUid : String ? = null
    var postExplain : String ? = null
    var postUid : String ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.bottom_sheet_select_content_option,container,false)
        binding.bottomsheetcontentoption = this@BottomSheetDialogContentOption

        var bundle = arguments
        uid = FirebaseAuth.getInstance().currentUser?.uid
        destinationUid = bundle?.getString("destinationUid")
        postExplain = bundle?.getString("postExplain")
        postUid = bundle?.getString("postUid")

        if(postUid != uid){
            // 삭제 버튼 안보이게
            binding.bottomSheetSelectContentOptionConstDelete.visibility = View.GONE
        }
        
        return binding.root

    }

    interface BottomSheetButtonClickListener{
        fun onBottomSheetButtonClick(text : String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try{
            bottomSheetContentOptionButtonClickListener = context as BottomSheetButtonClickListener

        }catch (e : ClassCastException){
            Log.d("bottomsheet","Click listener onAttach Error")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    fun groupClick(view : View){



        when(view.id){
            //신고
            binding.bottomSheetSelectContentOptionConstReport.id -> {

                var intent = Intent(binding.bottomsheetcontentoption?.context, ReportActivity::class.java)
                intent.apply {
                    putExtra("uid" , FirebaseAuth.getInstance().currentUser?.uid)
                    putExtra("destinationUid",destinationUid)
                    putExtra("postExplain",postExplain)
                    putExtra("postUid",postUid)
                }
                startActivity(intent)
                System.out.println("클릭되어씀1")
            }

            //삭제제
           binding.bottomSheetSelectContentOptionConstDelete.id -> {
                startActivity(
                    Intent(binding.bottomsheetcontentoption?.context,
                        AddBuyContentActivity::class.java)
                )
                System.out.println("클릭되어씀2")
            }



        }
    }



}