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
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.DeleteAcceptActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.ReportPostActivity
import com.uos.project_new_windy.databinding.BottomSheetSelectCategoryBinding
import com.uos.project_new_windy.databinding.BottomSheetSelectContentOptionBinding
import com.uos.project_new_windy.navigationlobby.AddBuyContentActivity
import java.lang.ClassCastException

class BottomSheetDialogContentOption : BottomSheetDialogFragment(){

    lateinit var bottomSheetContentOptionButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetSelectContentOptionBinding

    var userId : String ? = null
    var destinationUid : String ? = null
    var postExplain : String ? = null
    var postUid : String ? = null
    var uid : String ? = null
    var postType : String ? = null
    var viewType : String ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.bottom_sheet_select_content_option,container,false)
        binding.bottomsheetcontentoption = this@BottomSheetDialogContentOption

        var bundle = arguments
        userId = bundle?.getString("userId")
        uid = bundle?.getString("uid")
        destinationUid = bundle?.getString("destinationUid")
        postExplain = bundle?.getString("postExplain")
        postUid = bundle?.getString("postUid")
        postType = bundle?.getString("postType")
        viewType = bundle?.getString("viewType")
        
        System.out.println("테스트테스트테스트" + uid.toString())
        System.out.println("테스트세트세트2" + FirebaseAuth.getInstance().currentUser?.uid)
        System.out.println("테스트세트세트3" + FirebaseAuth.getInstance().currentUser?.email.toString())


        if(FirebaseAuth.getInstance().currentUser?.uid.toString().equals("jOMoLi0YgZUJUDypzaXSVQ84cEU2") || FirebaseAuth.getInstance().currentUser?.email.toString().equals("hG9W4uIR4dOmLweh2XOHMs9HbBE3") || FirebaseAuth.getInstance().currentUser?.email.toString().equals("ay72HtBWTWetM9JYE2VKYlmbYqh2"))
        {
            println("삭제버튼이 보입니다.")
            binding.bottomSheetSelectContentOptionConstDelete.visibility = View.VISIBLE
        }else {
            if (destinationUid != FirebaseAuth.getInstance().currentUser?.uid) {
                // 삭제 버튼 안보이게
                println("삭제버튼이 안보입니다.")
                binding.bottomSheetSelectContentOptionConstDelete.visibility = View.GONE
            }
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

                var intent = Intent(binding.bottomsheetcontentoption?.context, ReportPostActivity::class.java)
                intent.apply {
                    putExtra("uid" , FirebaseAuth.getInstance().currentUser?.uid)
                    putExtra("destinationUid",destinationUid)
                    putExtra("postExplain",postExplain)
                    putExtra("postUid",postUid)
                }
                startActivity(intent)
                System.out.println("클릭되어씀1")
            }

            //삭제
           binding.bottomSheetSelectContentOptionConstDelete.id -> {
               var intent = Intent(binding.bottomsheetcontentoption?.context, DeleteAcceptActivity::class.java)
               intent.apply {
                   putExtra("postUid",postUid)
                   putExtra("postType",postType)
                   putExtra("viewType",viewType)
               }
                startActivity(intent)


                System.out.println("클릭되어씀2")
            }





        }
        dismiss()
    }





}