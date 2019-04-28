package com.kotlin.viaggio.view.traveling.option

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.ItemOptionCityBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_cities.*

class TravelingCitiesActionDialogFragment:BaseDialogFragment<TravelingCitiesActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingCitiesActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingCitiesBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_cities, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawable = context?.getDrawable(R.drawable.dialog_bg) as GradientDrawable
        travelingCitiesActionList.background = drawable
        travelingCitiesActionList.clipToOutline = true
        travelingCitiesActionList.layoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.CENTER
        }

        getViewModel().areaListLiveData.observe(this, Observer {
            travelingCitiesActionList.adapter = object : RecyclerView.Adapter<TravelingCitiesViewHolder>(){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    TravelingCitiesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option_city, parent, false))
                override fun getItemCount() = getViewModel().areaList.size

                override fun onBindViewHolder(holder: TravelingCitiesViewHolder, position: Int) {
                    holder.binding?.data = getViewModel().areaList[position]
                    holder.binding?.viewHandler = holder.TravelingCitiesViewHandler()
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
        fun confirm(){
            getViewModel().confirm()
        }
    }


    inner class TravelingCitiesViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<ItemOptionCityBinding>(view)
        inner class TravelingCitiesViewHandler{
            fun selected() {
                binding?.data?.let {
                    getViewModel().chooseArea.get()!!.selected.set(false)
                    it.selected.set(true)
                    getViewModel().chooseArea.set(it)
                }
            }
        }
    }
}