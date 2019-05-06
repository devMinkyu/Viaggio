package com.kotlin.viaggio.view.traveling.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.databinding.ItemDomesticsBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_domestics_country.*
import kotlinx.android.synthetic.main.item_domestics.view.*


class TravelingDomesticsCountryFragment : BaseFragment<TravelingDomesticsCountryFragmentViewModel>() {
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(travelingCountryContainer, SlidrConfig.Builder().position(
                SlidrPosition.LEFT)
                .build())

    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingDomesticsCountryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_domestics_country, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryList.layoutManager = LinearLayoutManager(context)
        getViewModel().domesticsLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                countryList.adapter = object : RecyclerView.Adapter<TravelingDomesticsCountryViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingDomesticsCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_domestics_country, parent, false))
                    override fun getItemCount() = getViewModel().groupDomestics.size

                    override fun onBindViewHolder(holder: TravelingDomesticsCountryViewHolder, position: Int) {
                        holder.binding?.data = getViewModel().groupDomestics[position].country
                        val item = getViewModel().groupDomestics[position].area.map { areaVal ->
                            Area(country = getViewModel().groupDomestics[position].country, city = areaVal)
                        }
                        holder.cityCreateView(item)
                    }
                }
            }
        })
    }

    inner class ViewHandler{
        fun back(){
            fragmentPopStack()
        }
        fun confirm(){
            getViewModel().selectedCity()
            fragmentPopStack()
        }
    }

    inner class TravelingDomesticsCountryViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingDomesticsCountryBinding>(view)
        fun cityCreateView(areas: List<Area>){
            val main = itemView.findViewById(R.id.domesticsList) as ViewGroup
            if (main.childCount > 0) {
                main.removeAllViews()
            }
            for (area in areas) {
                val domesticsView = layoutInflater.inflate(R.layout.item_domestics, null)
                val domesticsBinding = DataBindingUtil.bind<ItemDomesticsBinding>(domesticsView)
                getViewModel().selectedCities.firstOrNull {
                    it.city == area.city
                }?.let {
                    getViewModel().selectedCities.remove(it)
                    area.selected.set(true)
                    getViewModel().selectedCities.add(area)
                }
                domesticsBinding?.data = area
                main.addView(domesticsView)

                domesticsView.domesticsName.setOnClickListener {
                    area.selected.set(area.selected.get().not())
                    if(getViewModel().selectedCities.contains(area)){
                        getViewModel().selectedCities.remove(area)
                    }else{
                        getViewModel().selectedCities.add(area)
                    }
                }
            }
        }
    }
}
