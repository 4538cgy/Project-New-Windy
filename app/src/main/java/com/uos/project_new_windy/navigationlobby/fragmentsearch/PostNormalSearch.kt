package com.uos.project_new_windy.navigationlobby.fragmentsearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uos.project_new_windy.R
import com.uos.project_new_windy.databinding.FragmentPostNormalSearchBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostNormalSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostNormalSearch : Fragment() {
    // TODO: Rename and change types of parameters


    lateinit var binding : FragmentPostNormalSearchBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_post_normal_search,container,false)
        binding.fragmentpostnormalsearch
        return binding.root
    }


}