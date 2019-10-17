package com.kotlin.viaggio.view.traveling.country

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_country.*
import kotlinx.android.synthetic.main.item_traveling_country.view.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.dip


class TravelingCountryFragment : BaseFragment<TravelingCountryFragmentViewModel>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().option = it.getBoolean(ArgName.TRAVEL_OPTION.name, false)
        }
    }

    override fun onResume() {
        super.onResume()
        if(getViewModel().option.not()){
            if(sliderInterface == null)
                sliderInterface = Slidr.replace(travelingCountryContainer, SlidrConfig.Builder().position(
                    SlidrPosition.LEFT)
                    .build())
        }
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingCountryBinding
    lateinit var adapter: RecyclerView.Adapter<TravelingSelectedCountryViewHolder>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_country, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryList.layoutManager = GridLayoutManager(context,2)

        val width = context!!.resources.displayMetrics.widthPixels - dip(59)

        getViewModel().countryLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                countryList.adapter = object : RecyclerView.Adapter<TravelingCountryViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_country, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: TravelingCountryViewHolder, position: Int) {
                        holder.binding?.data = list[position].country
                        holder.binding?.viewHandler = holder.TravelingCountryViewHandler()
                        holder.round()

                        val params = holder.itemView.item_container.layoutParams
                        params.width = width / 2
                        params.height = params.width
                        holder.itemView.item_container.layoutParams = params

                        Glide.with(holder.itemView)
                            .load(list[position].url)
                            .into(holder.itemView.countryItem)
                    }
                }
            }
        })

        getViewModel().continentLiveData.observe(this, Observer {
            val spinnerAdapter = ArrayAdapter(context!!, R.layout.spinner_continent_item, getViewModel().continentList)
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_continent_item)

            countrySpinner.adapter = spinnerAdapter
            countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    getViewModel().chooseContinent(position)
                }
            }
        })

        getViewModel().chooseAreaLiveData.observe(this, Observer {
            selectedCityList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = object : RecyclerView.Adapter<TravelingSelectedCountryViewHolder>(){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    TravelingSelectedCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_city, parent, false))
                override fun getItemCount() = getViewModel().chooseArea.size
                override fun onBindViewHolder(holder: TravelingSelectedCountryViewHolder, position: Int) {
                    holder.binding?.data = getViewModel().chooseArea[position]
                    holder.binding?.viewHandler = holder.TravelingSelectedCountryViewHandler()

                }
            }
            selectedCityList.adapter = adapter
        })

        getViewModel().completeLiveData.observe(this, Observer {
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
        fun confirm(){
            if(getViewModel().option){
                if(getViewModel().chooseArea.isEmpty()){
                    view?.snackbar(resources.getString(R.string.empty_country_hint))
                }else{
                    showLoading()
                    getViewModel().confirm()
                }
            }else{
                getViewModel().confirm()
            }
        }
        fun fetchData() {
            if(checkInternet()) {
                getViewModel().isExistData.set(true)
                getViewModel().loadingData.set(true)
                getViewModel().reDataFetch().observe(this@TravelingCountryFragment, Observer {
                    if (it != null && it.state.isFinished) {
                        getViewModel().loadingData.set(false)
                        getViewModel().countryDataFetch()
                    }
                })
            } else {
                showNetWorkError()
            }
        }
    }
    inner class TravelingCountryViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCountryBinding>(view)
        fun round(){
            val drawable = context?.getDrawable(R.drawable.round_bg) as GradientDrawable
            itemView.countryItem.background = drawable
            itemView.countryItem.clipToOutline = true
        }
        inner class TravelingCountryViewHandler{
            fun selected(){
                getViewModel().selectedCountry(binding?.data)
                baseIntent("http://viaggio.kotlin.com/traveling/city/")
            }
        }
    }
    inner class TravelingSelectedCountryViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemSelectedCityBinding>(view)
        inner class TravelingSelectedCountryViewHandler{
            fun delete(){
                binding?.data?.let {
                    val index = getViewModel().chooseArea.indexOf(it)
                    getViewModel().chooseArea.remove(it)
                    adapter.notifyItemRemoved(index)
                }

                if(getViewModel().chooseArea.isNullOrEmpty()){
                    getViewModel().empty()
                }
            }
        }
    }
}
