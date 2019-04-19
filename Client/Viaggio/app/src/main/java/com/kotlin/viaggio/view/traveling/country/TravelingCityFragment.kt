package com.kotlin.viaggio.view.traveling.country

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_city.*

class TravelingCityFragment:BaseFragment<TravelingCityFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingCityFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().travelType = it.getInt(ArgName.TRAVEL_TYPE.name, 0)
        }
    }

    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingCityBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_city, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        cityList.layoutManager = layoutManager

        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {

            }
        })
    }

    inner class ViewHandler{
        fun cancel(){
            fragmentPopStack()
        }
        fun choose(){

        }
    }
}