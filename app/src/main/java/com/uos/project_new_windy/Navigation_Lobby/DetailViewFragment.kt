package com.uos.project_new_windy.Navigation_Lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.project_new_windy.DetailContentActivity
import com.uos.project_new_windy.LobbyActivity
import com.uos.project_new_windy.Navigation_Lobby.DetailActivityRecyclerViewAdapter.BuyViewRecyclerViewAdapter
import com.uos.project_new_windy.Navigation_Lobby.DetailActivityRecyclerViewAdapter.DetailViewRecyclerViewAdapter
import com.uos.project_new_windy.Navigation_Lobby.DetailActivityRecyclerViewAdapter.SellViewRecyclerViewAdapter
import com.uos.project_new_windy.R
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailViewFragment : Fragment() {

    var firestore : FirebaseFirestore ? = null
    var uid : String ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        
        
        //전체 게시글 초기화
        view.fragment_detail_recycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
        view.fragment_detail_recycler.layoutManager = LinearLayoutManager(activity)

        view.fragment_detail_textview_buys.setOnClickListener {
            //구매 게시글 리스트로 초기화
            view.fragment_detail_recycler.adapter = BuyViewRecyclerViewAdapter(activity as LobbyActivity)
            view.fragment_detail_recycler.layoutManager = LinearLayoutManager(activity)
        }

        view.fragment_detail_textview_sales.setOnClickListener {
            //판매 게시글 리스트로 초기화
            view.fragment_detail_recycler.adapter = SellViewRecyclerViewAdapter(activity as LobbyActivity)
            view.fragment_detail_recycler.layoutManager = LinearLayoutManager(activity)
        }

        view.fragment_detail_textview_all.setOnClickListener {
            //전체 게시글 출력
            view.fragment_detail_recycler.adapter = DetailViewRecyclerViewAdapter(activity as LobbyActivity)
            view.fragment_detail_recycler.layoutManager = LinearLayoutManager(activity)
        }

       

        return view
    }



}