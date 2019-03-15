package com.kotlin.viaggio.view.travel.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import kotlinx.android.synthetic.main.fragment_action_dialog_calendar_view.*

class CalendarViewActionDialogFragment:BaseDialogFragment<CalendarViewActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = CalendarViewActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogCalendarViewBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_calendar_view, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class ViewHandler
}