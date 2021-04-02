package com.uos.project_new_windy.bottomsheet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.uos.project_new_windy.DeleteAcceptActivity
import com.uos.project_new_windy.R
import com.uos.project_new_windy.ReportPostActivity
import com.uos.project_new_windy.databinding.BottomSheetSelectCategoryBinding
import com.uos.project_new_windy.databinding.BottomSheetSelectContentOptionBinding
import com.uos.project_new_windy.model.contentdto.ContentBuyDTO
import com.uos.project_new_windy.model.contentdto.ContentNormalDTO
import com.uos.project_new_windy.model.contentdto.ContentSellDTO
import com.uos.project_new_windy.model.contentdto.ContentShopDTO
import com.uos.project_new_windy.navigationlobby.AddBuyContentActivity
import com.uos.project_new_windy.navigationlobby.AddContentActivity
import com.uos.project_new_windy.navigationlobby.AddSellContentActivity
import com.uos.project_new_windy.navigationlobby.AddShopContentActivity
import com.uos.project_new_windy.util.TimeUtil
import java.lang.ClassCastException

class BottomSheetDialogContentOption : BottomSheetDialogFragment() {

    lateinit var bottomSheetContentOptionButtonClickListener: BottomSheetButtonClickListener
    private lateinit var binding: BottomSheetSelectContentOptionBinding

    var userId: String? = null
    var destinationUid: String? = null
    var postExplain: String? = null
    var postUid: String? = null
    var uid: String? = null
    var postType: String? = null
    var viewType: String? = null
    var boardType: String? = null
    var contentUploadTime : Long ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.bottom_sheet_select_content_option, container, false)
        binding.bottomsheetcontentoption = this@BottomSheetDialogContentOption

        var bundle = arguments
        userId = bundle?.getString("userId")
        uid = bundle?.getString("uid")
        destinationUid = bundle?.getString("destinationUid")
        postExplain = bundle?.getString("postExplain")
        postUid = bundle?.getString("postUid")
        postType = bundle?.getString("postType")
        viewType = bundle?.getString("viewType")
        boardType = bundle?.getString("boardType")
        contentUploadTime = bundle?.getLong("contentUploadTime")

        System.out.println("테스트테스트테스트" + uid.toString())
        System.out.println("테스트세트세트2" + FirebaseAuth.getInstance().currentUser?.uid)
        System.out.println("테스트세트세트3" + FirebaseAuth.getInstance().currentUser?.email.toString())


        if (FirebaseAuth.getInstance().currentUser?.uid.toString()
                .equals("6TzkruCHS2YufEURE9vtK4VdFu52")
            || FirebaseAuth.getInstance().currentUser?.uid.toString()
                .equals("jOMoLi0YgZUJUDypzaXSVQ84cEU2")
            || FirebaseAuth.getInstance().currentUser?.email.toString()
                .equals("hG9W4uIR4dOmLweh2XOHMs9HbBE3")
            || FirebaseAuth.getInstance().currentUser?.email.toString()
                .equals("ay72HtBWTWetM9JYE2VKYlmbYqh2")
            || FirebaseAuth.getInstance().currentUser?.uid.toString()
                .equals("dZvFUbfbL9NZ5SYygiFsmSrAmM63")
        ) {
            println("삭제버튼이 보입니다.")
            binding.bottomSheetSelectContentOptionConstDelete.visibility = View.VISIBLE
            binding.bottomSheetSelectContentOptionConstUpdate.visibility = View.VISIBLE
            binding.bottomSheetSelectContentOptionConstUpdateTime.visibility = View.VISIBLE
        } else {
            if (destinationUid != FirebaseAuth.getInstance().currentUser?.uid) {
                // 삭제 버튼 안보이게
                println("삭제버튼이 안보입니다.")
                binding.bottomSheetSelectContentOptionConstDelete.visibility = View.GONE
                binding.bottomSheetSelectContentOptionConstUpdate.visibility = View.GONE
                binding.bottomSheetSelectContentOptionConstUpdateTime.visibility = View.GONE
            }
        }



        return binding.root

    }

    interface BottomSheetButtonClickListener {
        fun onBottomSheetButtonClick(text: String)
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


        when (view.id) {
            //신고
            binding.bottomSheetSelectContentOptionConstReport.id -> {

                var intent = Intent(binding.bottomsheetcontentoption?.context,
                    ReportPostActivity::class.java)
                intent.apply {
                    putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid)
                    putExtra("destinationUid", destinationUid)
                    putExtra("postExplain", postExplain)
                    putExtra("postUid", postUid)
                }
                startActivity(intent)
                System.out.println("클릭되어씀1")
            }

            //삭제
            binding.bottomSheetSelectContentOptionConstDelete.id -> {
                var intent = Intent(binding.bottomsheetcontentoption?.context,
                    DeleteAcceptActivity::class.java)
                intent.apply {
                    putExtra("postUid", postUid)
                    putExtra("postType", postType)
                    putExtra("viewType", viewType)
                }
                startActivity(intent)


                System.out.println("클릭되어씀2")
            }

            //수정하기
            binding.bottomSheetSelectContentOptionConstUpdate.id -> {
                var intent: Intent? = null


                when (boardType) {
                    "sell" -> {
                        intent = Intent(binding.bottomsheetcontentoption?.context,
                            AddSellContentActivity::class.java)
                        intent.apply {
                            putExtra("postUid", postUid)
                            putExtra("postType", postType)
                            putExtra("viewType", viewType)
                            putExtra("updateCheck", true)
                        }
                    }
                    "buy" -> {
                        intent = Intent(binding.bottomsheetcontentoption?.context,
                            AddBuyContentActivity::class.java)
                        intent.apply {
                            putExtra("postUid", postUid)
                            putExtra("postType", postType)
                            putExtra("viewType", viewType)
                            putExtra("updateCheck", true)
                        }
                    }
                    "normal" -> {
                        intent = Intent(binding.bottomsheetcontentoption?.context,
                            AddContentActivity::class.java)
                        intent.apply {
                            putExtra("postUid", postUid)
                            putExtra("postType", postType)
                            putExtra("viewType", viewType)
                            putExtra("updateCheck", true)
                        }
                    }
                    "shop" -> {
                        intent = Intent(binding.bottomsheetcontentoption?.context,
                            AddShopContentActivity::class.java)
                        intent.apply {
                            putExtra("postUid", postUid)
                            putExtra("postType", postType)
                            putExtra("viewType", viewType)
                            putExtra("updateCheck", true)
                        }
                    }

                }

                startActivity(intent)
            }
            //게시글 끌어올리기
            binding.bottomSheetSelectContentOptionConstUpdateTime.id -> {
                var intent: Intent? = null
                updateContentUploadTime(boardType!!)
            }
        }
        dismiss()
    }

    fun updateContentUploadTime(type: String) {
        var tsDoc =
            FirebaseFirestore.getInstance()?.collection("contents")
                ?.document(type).collection("data")
                ?.document(postUid!!)

        if (contentUploadTime != null) {
            if (TimeUtil().getDayCheck(contentUploadTime!!)!!) {

                println("하루 이상 지난 게시글입니다.")


                FirebaseFirestore.getInstance()?.runTransaction { transaction ->


                    System.out.println("트랜잭션 시작")
                    when(type){

                        "sell" ->{
                            var contentDTO = transaction.get(tsDoc!!)
                                .toObject(ContentSellDTO::class.java)

                            contentDTO?.timeStamp = System.currentTimeMillis()
                            contentDTO?.uploadTimeStamp = contentUploadTime
                            transaction.set(tsDoc, contentDTO!!)
                        }
                        "normal" -> {
                            var contentDTO = transaction.get(tsDoc!!)
                                .toObject(ContentNormalDTO::class.java)

                            contentDTO?.timestamp = System.currentTimeMillis()
                            contentDTO?.uploadTimeStamp = contentUploadTime
                            transaction.set(tsDoc, contentDTO!!)
                        }
                        "buy" -> {
                            var contentDTO = transaction.get(tsDoc!!)
                                .toObject(ContentBuyDTO::class.java)

                            contentDTO?.timeStamp = System.currentTimeMillis()
                            contentDTO?.uploadTimeStamp = contentUploadTime
                            transaction.set(tsDoc, contentDTO!!)
                        }
                        "shop" ->{
                            var contentDTO = transaction.get(tsDoc!!)
                                .toObject(ContentShopDTO::class.java)

                            contentDTO?.timeStamp = System.currentTimeMillis()
                            contentDTO?.uploadTimeStamp = contentUploadTime
                            transaction.set(tsDoc, contentDTO!!)
                        }

                        else -> return@runTransaction
                    }




                }.addOnFailureListener {
                    println("viewCountIncreaseFail ${it.toString()}")
                }.addOnCompleteListener {
                    println("transaction success ${it.toString()}")
                }
            }else {
                Toast.makeText(context,"하루 이상 지난 게시글만 끌어올리기가 가능합니다.",Toast.LENGTH_LONG).show()
            }


            }


        }


}