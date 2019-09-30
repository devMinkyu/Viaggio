package com.kotlin.viaggio.view.traveling.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_day_count.*

class TravelingDayCountActionDialogFragment:BaseDialogFragment<TravelingDayCountActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDayCountActionDialogFragment::class.java.simpleName
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
        travelingDayCountActionList.layoutManager = GridLayoutManager(context,4)

        getViewModel().dayCountLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { count ->
                travelingDayCountActionList.adapter = object : RecyclerView.Adapter<TravelingDayCountViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingDayCountViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_day_count, parent, false))
                    override fun getItemCount() = count
                    override fun onBindViewHolder(holder: TravelingDayCountViewHolder, position: Int) {
                        if(getViewModel().traveling){
                            holder.binding?.data = count - position
                        }else{
                            holder.binding?.data = position + 1
                        }
                        holder.binding?.viewHandler = holder.TravelingDayCountViewHandler()
                    }
                }
            }
        })
        getViewModel().completeLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })
    }

    inner class ViewHandler{
        fun cancel(){
            dismiss()
        }
    }

    inner class TravelingDayCountViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemOptionDayCountBinding>(view)
        inner class TravelingDayCountViewHandler{
            fun selected() {
                binding?.data?.let {
                    getViewModel().chooseDayCount.set(it)
                    getViewModel().confirm()
                }
            }
        }
    }
}