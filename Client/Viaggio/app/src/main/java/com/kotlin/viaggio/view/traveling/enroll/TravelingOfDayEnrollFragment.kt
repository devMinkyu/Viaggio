package com.kotlin.viaggio.view.traveling.enroll

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_of_day_enroll.*
import java.text.SimpleDateFormat
import java.util.*


class TravelingOfDayEnrollFragment : BaseFragment<TravelingOfDayEnrollFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
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
        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
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

        @SuppressLint("SimpleDateFormat")
        fun enrollOfTime() {
            val cal = Calendar.getInstance()
            TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener { timePicker, i, i1 ->
                cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                cal.set(Calendar.MINUTE, timePicker.minute)
                getViewModel().time.set(SimpleDateFormat(resources.getString(R.string.date_time_format)).format(cal.time))
            }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true).show()
        }

    }
}