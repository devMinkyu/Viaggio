package com.kotlin.viaggio.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.traveled.TraveledFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment:BaseFragment<HomeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.homeMain, HomeMainFragment())
            .commit()
        childFragmentManager.beginTransaction()
            .replace(R.id.homeTraveledListView, TraveledFragment())
            .commit()
        childFragmentManager.beginTransaction()
            .replace(R.id.homeTravelingListView, TravelingFragment())
            .commit()
    }


    @SuppressLint("WrongConstant")
    inner class ViewHandler{
        fun traveling(){
            if(homeContentFrame.isDrawerOpen(Gravity.END)){
                homeContentFrame.closeDrawer(Gravity.END)
            }
            if(!homeContentFrame.isDrawerOpen(Gravity.START)){
                homeContentFrame.openDrawer(Gravity.START)
            }
        }
        fun traveled(){
            if(homeContentFrame.isDrawerOpen(Gravity.START)){
                homeContentFrame.closeDrawer(Gravity.START)
            }
            if(!homeContentFrame.isDrawerOpen(Gravity.END)){
                homeContentFrame.openDrawer(Gravity.END)
            }
        }
    }
}