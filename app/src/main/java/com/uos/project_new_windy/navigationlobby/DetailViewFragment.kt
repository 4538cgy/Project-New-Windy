package com.uos.project_new_windy.navigationlobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentDetailBinding
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentBuyRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentNormalRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentSellRecyclerViewAdapter

class DetailViewFragment : Fragment() {

    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null

    lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)


        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        
        
        //전체 게시글 초기화


        binding.fragmentDetailRecycler.adapter = ContentSellRecyclerViewAdapter(binding.root.context, fragmentManager!!)
        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)




        binding.fragmentDetailTextviewBuys.setOnClickListener {
            //구매 게시글 리스트로 초기화
            //binding.fragmentDetailRecycler.adapter = BuyViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)

            setContentBuyRecycler()
        }

        binding.fragmentDetailTextviewSales.setOnClickListener {
            //판매 게시글 리스트로 초기화
            //binding.fragmentDetailRecycler.adapter = SellViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)

            setContentSellRecycler()
        }

        binding.fragmentDetailTextviewAll.setOnClickListener {
            //전체 게시글 출력
            //binding.fragmentDetailRecycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            setContentNormalRecycler()
        }



       

        return binding.root
    }

    fun setContentBuyRecycler(){
        val contentBuyViewRecyclerViewAdapter = ContentBuyRecyclerViewAdapter(binding.root.context)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentBuyViewRecyclerViewAdapter
        contentBuyViewRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun setContentSellRecycler(){
        val contentSellViewRecyclerViewAdapter = ContentSellRecyclerViewAdapter(binding.root.context,fragmentManager!!)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentSellViewRecyclerViewAdapter
        contentSellViewRecyclerViewAdapter.notifyDataSetChanged()
    }

    fun setContentNormalRecycler(){
        val contentNormalRecyclerViewAdapter = ContentNormalRecyclerViewAdapter(binding.root.context)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentNormalRecyclerViewAdapter

        contentNormalRecyclerViewAdapter.notifyDataSetChanged()

    }


}