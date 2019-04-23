package com.kotlin.viaggio.view.traveling.country

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.databinding.ItemCityBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_city.*


class TravelingCityFragment:BaseFragment<TravelingCityFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingCityFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().travelType = it.getInt(ArgName.TRAVEL_TYPE.name, 0)
        }
    }
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(container_view, SlidrConfig.Builder().position(
                SlidrPosition.TOP)
                .build())
    }


    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingCityBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_city, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER

        val lay = StaggeredGridLayoutManager(4, RecyclerView.HORIZONTAL)
        cityList.layoutManager = lay
        cityList.addItemDecoration(TravelCityItemDecoration())

        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                cityList.adapter = object : RecyclerView.Adapter<TravelingCityViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingCityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false))
                    override fun getItemCount() = getViewModel().cityList.size

                    override fun onBindViewHolder(holder: TravelingCityViewHolder, position: Int) {
                        holder.binding?.data = getViewModel().cityList[position]
                        holder.binding?.viewHandler = holder.TravelingCityViewHandler()
                    }
                }
            }
        })
    }

    inner class ViewHandler{
        fun cancel(){
            fragmentPopStack()
        }
        fun choose(){
            getViewModel().selectedCity()
            fragmentPopStack()
//            if(getViewModel().travelType == 0){
//                fragmentManager?.popBackStackImmediate()
//                fragmentPopStack()
//            }else{
//                fragmentPopStack()
//            }
        }
    }

    inner class TravelingCityViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<ItemCityBinding>(view)

        inner class TravelingCityViewHandler{

            fun select(){
                binding?.data?.selected?.let {
                    binding.data!!.selected.set(it.get().not())
                    if(getViewModel().selectedCities.contains(binding.data)){
                        getViewModel().selectedCities.remove(binding.data)
                    }else{
                        getViewModel().selectedCities.add(binding.data)
                    }
                }
//                if(getViewModel().travelType == 0){
//                    binding?.data?.selected?.let {
//                        binding.data!!.selected.set(it.get().not())
//                        if(getViewModel().selectedCities.contains(binding.data)){
//                            getViewModel().selectedCities.remove(binding.data)
//                        }else{
//                            if(getViewModel().selectedCities.size > 0){
//                                getViewModel().selectedCities[0].selected.set(false)
//                                getViewModel().selectedCities.clear()
//                            }
//                            getViewModel().selectedCities.add(binding.data)
//                        }
//                    }
//                }else{
//
//                }
            }
        }
    }
}

class TravelCityItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null
    private var firstHorMargin2: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        when(parent.getChildAdapterPosition(view)){
            1,3 ->{
                val firstHorMarginVal1 = firstHorMargin
                    ?: (parent.context.resources.getDimension(R.dimen.common_margin))
                firstHorMargin2 = firstHorMarginVal1
                outRect.left = firstHorMarginVal1.toInt()
            }
        }
    }
}