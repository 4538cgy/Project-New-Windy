package com.uos.project_new_windy.navigationlobby

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentSearchBinding
import com.uos.project_new_windy.navigationlobby.fragmentsearch.*
import kotlinx.android.synthetic.main.fragment_search.*

const val NUM_PAGES = 2
private const val ARG_OBJECT = "object"

class SearchFragment : Fragment() {

    lateinit var binding : FragmentSearchBinding
    private lateinit var demoCollectionAdapter: DemoCollectionAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search, container, false)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_search,container,false)

        binding.fragmentSearchViewpager2.adapter
        binding.fragmentSearchImagebuttonCategoryOption.setOnClickListener {

        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var main_adapter = DemoCollectionAdapter(this)
        binding.fragmentSearchViewpager2.adapter = main_adapter

        TabLayoutMediator(binding.fragmentSearchTablayout, binding.fragmentSearchViewpager2) { tab, position ->


            when(position){

                0 ->
                {
                    //tab.text = "농업 뉴스"
                    tab.text = "구매 게시판 검색"
                }


                1 ->
                {
                    tab.text = "새 소식 검색"
                }
                2 ->
                {
                    tab.text = "판매 게시판 검색"
                }

            }


        }.attach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



    }

    inner class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)

            var fragment : Fragment

            fragment = MainSearchFragment()

            when(position){

                0 ->
                {
                    fragment = PostBuySearch()
                }


                1 ->
                {
                    fragment = PostNormalSearch()
                }
                2 ->
                {
                    fragment = PostSellSearch()
                }

            }
            /*
            val fragment = MainSearchFragment()
            fragment.arguments = Bundle().apply {
                // Our object is just an integer :-P
                putInt(ARG_OBJECT, position + 1)
            }

             */
            return fragment
        }
    }
}