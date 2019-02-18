package com.kotlin.viaggio.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment


class HomeMainFragment:BaseFragment<HomeMainFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentHomeMainBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_main, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    inner class ViewHandler{
        fun userProfile(){
            baseIntent("http://viaggio.kotlin.com/setting/main/")
        }
    }
}