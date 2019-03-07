package com.kotlin.viaggio.view.traveled

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.Traveled
import com.kotlin.viaggio.databinding.ItemTraveledBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveled.*
import kotlinx.android.synthetic.main.item_traveled.view.*
import java.io.File
import java.text.SimpleDateFormat


class TraveledFragment:BaseFragment<TraveledFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTraveledBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveled, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        traveledList.layoutManager = LinearLayoutManager(context)
        val adapter = TraveledAdapter()
        traveledList.adapter = adapter
        getViewModel().traveledPagedLiveData.observe(this, Observer(adapter::submitList))
    }

    inner class ViewHandler

    inner class TraveledAdapter: PagedListAdapter<Travel, TraveledViewHolder>(object :
        DiffUtil.ItemCallback<Travel>(){
        override fun areItemsTheSame(oldItem: Travel, newItem: Travel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Travel, newItem: Travel) = oldItem == newItem
    }){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = TraveledViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveled, parent, false))
        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: TraveledViewHolder, position: Int) {
            getViewModel().existTraveled.set(true)
            val travel = getItem(position)
            travel?.let {travel ->
                var theme = ""
                var countries = ""
                for ((i,s) in travel.theme.withIndex()) {
                    if(i != 0){
                        theme = "$theme, $s"
                    }else{
                        theme = s
                    }
                }
                for ((i,s) in travel.entireCountries.withIndex()) {
                    if(i != 0){
                        countries = "$countries > $s"
                    }else{
                        countries = s
                    }
                }
                val item = Traveled(id = travel.id, theme = theme, period =
                "${SimpleDateFormat(resources.getString(R.string.date_format)).format(travel.startDate)} " +
                        "~ ${SimpleDateFormat(resources.getString(R.string.date_format)).format(travel.endDate)}",
                    countries = countries
                )

                val imgDir = File(context?.filesDir, "images/")
                holder.binding?.data = item
                holder.binding?.viewHandler = holder.TraveledViewHandler()
                getItem(position)?.themeImageName?.let {
                    val imgFile = File(imgDir, it)
                    if (imgFile.exists()) {
                        Uri.fromFile(imgFile).let { uri ->
                            Glide.with(holder.itemView.traveledBackground)
                                .load(uri)
                                .into(holder.itemView.traveledBackground)
                        }
                    }
                }

            }
        }
    }

    inner class TraveledViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemTraveledBinding>(itemView)

        inner class TraveledViewHandler{
            fun selectedTraveled(){

            }
        }
    }
}