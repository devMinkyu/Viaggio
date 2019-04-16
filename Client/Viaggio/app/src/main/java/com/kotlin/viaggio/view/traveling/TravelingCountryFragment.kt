package com.kotlin.viaggio.view.traveling

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_country.*
import org.jetbrains.anko.backgroundColor


class TravelingCountryFragment : BaseFragment<TravelingCountryFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingCountryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_country, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryContinent.layoutManager = LinearLayoutManager(context)
        countryContinent.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        countryContinent.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        countryArea.layoutManager = LinearLayoutManager(context)
        countryArea.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        countryArea.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        countryCountry.layoutManager = LinearLayoutManager(context)
        countryCountry.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        countryCountry.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))

        val continentAdapter = TravelingCountryAdapter()
        val areaAdapter = TravelingCountryAdapter()

        getViewModel().continentLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {continents ->
                continentAdapter.list = continents.toMutableList()
                continentAdapter.viewType = if(getViewModel().chooseContinent.get()) 1 else 0
                continentAdapter.type = 0

                countryContinent.adapter = continentAdapter
            }
        })

        getViewModel().areaLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { areas ->
                continentAdapter.viewType = if(getViewModel().chooseContinent.get()) 1 else 0
                continentAdapter.selectedPosition = getViewModel().continentPosition
                continentAdapter.notifyDataSetChanged()

                areaAdapter.list = areas.toMutableList()
                areaAdapter.viewType = if(getViewModel().chooseArea.get()) 1 else 0
                areaAdapter.type = 1
                countryArea.adapter = areaAdapter
            }
        })

        getViewModel().countryLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { countries ->
                areaAdapter.viewType = if(getViewModel().chooseArea.get()) 1 else 0
                areaAdapter.selectedPosition = getViewModel().areaPosition
                areaAdapter.notifyDataSetChanged()

                val adapter = TravelingCountryAdapter()
                adapter.list = countries.toMutableList()
                adapter.viewType = 0
                countryCountry.adapter = adapter
            }
        })

        getViewModel().completeLiveData.observe(this, Observer {
            stopLoading()
            fragmentPopStack()
        })
    }

    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }

        fun choose(position: Int) {
            when{
                getViewModel().chooseContinent.get().not() ->{
                    getViewModel().showArea(position)
                }
                getViewModel().chooseArea.get().not() -> {
                    getViewModel().showCountry(position)
                }
                else -> {
                    showLoading()
                    getViewModel().changeCountry(position)
                }
            }
        }
        fun reChoose(position: Int, type:Int){
            when(type){
                0 -> {
                    getViewModel().showArea(position)
                }
                1 -> {
                    getViewModel().showCountry(position)
                }
            }
        }
    }

    inner class TravelingCountryAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var list: MutableList<String> = mutableListOf()
        var viewType: Int = 0
        var selectedPosition:Int = 0
        var type = 0

        override fun getItemViewType(position: Int) = viewType
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when(viewType){
                0 -> {
                    TravelingCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_country, parent, false))
                }
                1 -> {
                    TravelingCountryChooseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_country_choose, parent, false))
                }
                else -> {
                    TravelingCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_country, parent, false))
                }
            }
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(getItemViewType(position)){
                0 -> {
                    holder as TravelingCountryViewHolder
                    holder.binding?.data = list[position]
                    holder.binding?.position = position
                    holder.binding?.viewHandler = ViewHandler()
                }
                1 -> {
                    holder as TravelingCountryChooseViewHolder
                    holder.binding?.data = list[position]
                    holder.binding?.position = position
                    holder.binding?.type = type
                    holder.binding?.viewHandler = ViewHandler()
                    if(position == selectedPosition){
                        holder.itemView.backgroundColor = resources.getColor(R.color.pinkish_grey, null)
                    }else{
                        holder.itemView.backgroundColor = Color.WHITE
                    }
                }
            }
        }
    }

    inner class TravelingCountryViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCountryBinding>(view)
    }
    inner class TravelingCountryChooseViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCountryChooseBinding>(view)
    }
}