package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentPostSellSearchBinding
import com.uos.project_new_windy.model.contentdto.ContentSellDTO

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostSellSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostSellSearch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentPostSellSearchBinding

    var categoryData: ArrayList<String> = arrayListOf()

    var contentSellDTO: ArrayList<ContentSellDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentData : ArrayList<ContentSellDTO> = arrayListOf()

    init {

        FirebaseFirestore.getInstance().collection("contents")
            .document("sell")
            .collection("data")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                contentSellDTO.clear()
                contentUidList.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentSellDTO::class.java)
                    System.out.println("데이터들 " + item.toString())
                    //거래완료 상품이 아니면 보여줌
                    if (item?.checkSellComplete == false) {
                        contentSellDTO.add(item!!)
                        System.out.println("데이터들2" + contentSellDTO.toString())
                        contentUidList.add(snapshot.id)
                    }


                }



            }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_sell_search, container, false)
        binding.fragmentpostsellsearch



        startActivityForResult(Intent(
            binding.root.context,
            PostSellSearchCategorySetActivity::class.java,
        ), 1234)
        return binding.root
    }

    fun dataCleaning(){
        for(c in contentSellDTO.indices){
            for(d in categoryData.indices)
            if (contentSellDTO[c].category.equals(categoryData[d].toString())){
                    System.out.println("중복됩니다." + categoryData[d] + contentSellDTO[c])
                contentData.add(contentSellDTO[c])
            }
        }

        contentData.forEach {
            System.out.println("카테고리를 포함한 데이터 $it" )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {

            if (resultCode == 1556) {
                System.out.println("데이터 전달 성공적으로 완수3123123123123")
                categoryData = data?.getStringArrayListExtra("categoryList")!!
                categoryData.forEach {
                    System.out.println("카테고리 리스트 목록 = $it")
                }
                dataCleaning()
            }


        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostSellSearch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostSellSearch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}