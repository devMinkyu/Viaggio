package com.kotlin.viaggio.view.traveled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.ItemTraveledBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveled.*


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
        traveledList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                TraveledViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveled, parent, false))
            override fun getItemCount() = 5

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            }
        }
    }

    inner class ViewHandler

    inner class TraveledViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemTraveledBinding>(itemView)
    }
}