package com.kotlin.viaggio.view.traveling.option

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_day_count.*
import java.text.SimpleDateFormat
import java.util.*

class TravelingDayCountActionDialogFragment:BaseDialogFragment<TravelingDayCountActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDayCountActionDialogFragment::class.java.simpleName
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().changeMode = it.getBoolean(ArgName.TRAVEL_CARD_CHANGE_MODE.name, false)
        }
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingDayCountBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_day_count, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelingDayCountActionList.clipToOutline = true
        travelingDayCountActionList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        getViewModel().dayCountLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { count ->
                if(getViewModel().traveling) {
                    getViewModel().chooseDayCount.set(count)
                } else {
                    getViewModel().chooseDayCount.set(1)
                }
                travelingDayCountActionList.adapter = object : RecyclerView.Adapter<TravelingDayCountViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingDayCountViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_day_count, parent, false))
                    override fun getItemCount() = count
                    override fun onBindViewHolder(holder: TravelingDayCountViewHolder, position: Int) {
                        holder.binding?.day = position + 1
                        holder.binding?.date = SimpleDateFormat("E", Locale.getDefault()).format(getViewModel().startDate.time)
                        holder.binding?.month = SimpleDateFormat("yy, MMM dd", Locale.US).format(getViewModel().startDate.time)
                        getViewModel().startDate.add(Calendar.DAY_OF_MONTH, 1)
                        holder.binding?.viewHandler = holder.TravelingDayCountViewHandler()
                        holder.binding?.viewModel = getViewModel()
                    }
                }
            }
        })
        getViewModel().chooseDate.set(Calendar.HOUR_OF_DAY, travelingDayCountActionTimePicker.hour)
        getViewModel().chooseDate.set(Calendar.MINUTE, travelingDayCountActionTimePicker.minute)
        travelingDayCountActionTimePicker.setOnTimeChangedListener { _, i, i2 ->
            getViewModel().chooseDate.set(Calendar.HOUR_OF_DAY, i)
            getViewModel().chooseDate.set(Calendar.MINUTE, i2)
        }
    }

    inner class ViewHandler{
        fun cancel(){
            dismiss()
        }
        fun confirm() {
            getViewModel().confirm().observe(this@TravelingDayCountActionDialogFragment, Observer {
                it.getContentIfNotHandled()?.let {
                    dismiss()
                }
            })
        }
    }

    inner class TravelingDayCountViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemOptionDayCountBinding>(view)
        inner class TravelingDayCountViewHandler{
            fun selected() {
                binding?.day?.let {
                    getViewModel().chooseDayCount.set(it)
                }
            }
        }
    }
}