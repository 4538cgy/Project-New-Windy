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
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.BuyViewRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.DetailViewRecyclerViewAdapter
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.SellViewRecyclerViewAdapter
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentDetailBinding
import com.uos.project_new_windy.navigationlobby.DetailActivityRecyclerViewAdapter.contentadapter.ContentNormalRecyclerViewAdapter

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
        binding.fragmentDetailRecycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)

        binding.fragmentDetailTextviewBuys.setOnClickListener {
            //구매 게시글 리스트로 초기화
            binding.fragmentDetailRecycler.adapter = BuyViewRecyclerViewAdapter(activity as LobbyActivity)
            binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
        }

        binding.fragmentDetailTextviewSales.setOnClickListener {
            //판매 게시글 리스트로 초기화
            binding.fragmentDetailRecycler.adapter = SellViewRecyclerViewAdapter(activity as LobbyActivity)
            binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
        }

        binding.fragmentDetailTextviewAll.setOnClickListener {
            //전체 게시글 출력
            //binding.fragmentDetailRecycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
            //binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(activity)
            setContentNormalRecycler()
        }



       

        return binding.root
    }

    fun setContentNormalRecycler(){
        val contentNormalRecyclerViewAdapter = ContentNormalRecyclerViewAdapter(binding.root.context)

        binding.fragmentDetailRecycler.layoutManager = LinearLayoutManager(binding.root.context)
        binding.fragmentDetailRecycler.adapter = contentNormalRecyclerViewAdapter

        contentNormalRecyclerViewAdapter.notifyDataSetChanged()

    }


}