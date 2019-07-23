package com.kotlin.viaggio.view.traveling.country

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.data.obj.Area
import com.kotlin.viaggio.databinding.ItemDomesticsBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_domestics_country.*
import kotlinx.android.synthetic.main.item_domestics.view.*
import org.jetbrains.anko.design.snackbar


class TravelingDomesticsCountryFragment : BaseFragment<TravelingDomesticsCountryFragmentViewModel>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().option = it.getBoolean(ArgName.TRAVEL_OPTION.name, false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                travelingCountryContainer, SlidrConfig.Builder().position(
                    SlidrPosition.LEFT
                )
                    .build()
            )

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
        countryList.addItemDecoration(DomesticsItemDecoration())
        getViewModel().domesticsLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                countryList.adapter = object : RecyclerView.Adapter<TravelingDomesticsCountryViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingDomesticsCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_domestics_country, parent, false))
                    override fun getItemCount() = getViewModel().groupDomestics.size
                    override fun onBindViewHolder(holder: TravelingDomesticsCountryViewHolder, position: Int) {
                        holder.binding?.data = getViewModel().groupDomestics[position].country
                        val item = list.filter { areaVal ->
                            areaVal.country == getViewModel().groupDomestics[position].country
                        }
                        holder.cityCreateView(item)
                    }
                }
                val adapter = ArrayAdapter(context!!, R.layout.spinner_dropdown_auto_item, getViewModel().autoSearchList)
                autoCompleteTextView.setAdapter(adapter)
                autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { p0, _, p2, _ ->
                    val item = p0.getItemAtPosition(p2).toString()
                    val index = getViewModel().autoSearchList.indexOf(item)
                    list.firstOrNull { area ->
                        area.city == item
                    }?.let { area ->
                        if(getViewModel().selectedCities.contains(area).not()) {
                            getViewModel().selectedBooleans[index].set(true)
                            getViewModel().selectedCities.add(area)
                        }
                    }
                    view.snackbar(String.format(getString(R.string.travel_auto_selected), item))
                    autoCompleteTextView.text.clear()
                }
            }
        })

        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })

        countryList.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!countryList.canScrollVertically(-1)) {
                val animator1 = backToTop.animate().setDuration(250)
                    .translationY(backToTop.height.toFloat() + 150f)
                animator1.start()
                val animator2 = auto.animate().setDuration(350)
                    .alpha(1f)
                    .translationY(0f)
                animator2.start()
            } else {
                val animator1 = backToTop.animate().setDuration(250)
                    .translationY(0f)
                animator1.start()
                val animator2 = auto.animate().setDuration(350)
                    .alpha(0f)
                    .translationY((auto.height.toFloat() + 150f) * -1)
                animator2.start()
            }
        }
    }

    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }

        fun backToTop() {
            countryList.smoothScrollToPosition(0)
        }
        fun confirm() {
            if (getViewModel().option) {
                if (getViewModel().selectedCities.isEmpty()) {
                    view?.snackbar(resources.getString(R.string.empty_country_hint))
                } else {
                    showLoading()
                    getViewModel().selectedCity()
                }
            } else {
                getViewModel().selectedCity()
            }
        }

        fun fetchData() {
            if(checkInternet()) {
                getViewModel().isExistData.set(true)
                getViewModel().loadingData.set(true)
                getViewModel().reDataFetch().observe(this@TravelingDomesticsCountryFragment, Observer {
                    if (it != null && it.state.isFinished) {
                        getViewModel().loadingData.set(false)
                        getViewModel().domesticsDataFetch()
                    }
                })
            } else {
                showNetWorkError()
            }
        }
    }

    inner class TravelingDomesticsCountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingDomesticsCountryBinding>(view)
        fun cityCreateView(areas: List<Area>) {
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
                    if (area.selected.get()) {
                        area.selected.set(area.selected.get().not())
                        if (getViewModel().selectedCities.contains(area)) {
                            getViewModel().selectedCities.remove(area)
                        }
                    } else {
                        if (getViewModel().selectedCities.size <= 20) {
                            area.selected.set(area.selected.get().not())
                            getViewModel().selectedCities.add(area)
                        }
                    }
                }
            }
        }
    }
}


class DomesticsItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            val firstHorMarginVal1 = firstHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.travel_card_end))
            firstHorMargin = firstHorMarginVal1
            outRect.top = firstHorMarginVal1.toInt()
        }
    }
}