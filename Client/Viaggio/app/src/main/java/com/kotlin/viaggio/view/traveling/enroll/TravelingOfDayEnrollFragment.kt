package com.kotlin.viaggio.view.traveling.enroll

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingOfDayEnrollBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_of_day_enroll.*
import java.text.SimpleDateFormat
import java.util.*


class TravelingOfDayEnrollFragment : BaseFragment<TravelingOfDayEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingOfDayEnrollBinding
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(enroll_container, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_of_day_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })
        getViewModel().changeCursor.observe(this, Observer {event ->
            event.getContentIfNotHandled()?.let {
                travelingOfDayEnrollContents.text?.let {
                    travelingOfDayEnrollContents.setSelection(travelingOfDayEnrollContents.text.toString().length)
                }
            }
        })
    }


    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }
        fun save() {
//            showLoading()
//            getViewModel().saveTravelCard()
        }

        fun enrollOfTime() {
            val cal = Calendar.getInstance()
            getViewModel().contents.set("${getViewModel().contents.get()}\n${SimpleDateFormat(resources.getString(R.string.travel_of_day_time_pattern), Locale.ENGLISH).format(cal.time)}\n")
        }
        fun image(){
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/image/")
        }

    }
}