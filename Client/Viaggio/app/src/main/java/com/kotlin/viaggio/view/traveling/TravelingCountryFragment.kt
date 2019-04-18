package com.kotlin.viaggio.view.traveling

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_country.*
import kotlinx.android.synthetic.main.item_traveling_country.view.*
import org.jetbrains.anko.support.v4.dip


class TravelingCountryFragment : BaseFragment<TravelingCountryFragmentViewModel>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            it.getInt(ArgName.TRAVEL_TYPE.name, 0)
        }
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingCountryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_country, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryList.layoutManager = GridLayoutManager(context, 2)
        countryList.addItemDecoration(TravelCountryItemDecoration())

        val width = context!!.resources.displayMetrics.widthPixels - dip(59)

        getViewModel().countryLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                countryList.adapter = object : RecyclerView.Adapter<TravelingCountryViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingCountryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_country, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: TravelingCountryViewHolder, position: Int) {
                        holder.binding?.data = list[position]
                        holder.binding?.viewHandler = holder.TravelingCountryViewHandler()
                        holder.round()

                        val params = holder.itemView.item_container.layoutParams
                        params.width = width / 2
                        params.height = params.width
                        holder.itemView.item_container.layoutParams = params
                    }
                }
            }
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

            }
        }
    }
}


class TravelCountryItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstVerticalMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val adapterPos = parent.getChildAdapterPosition(view)

        if (adapterPos < 2) {
            val firstVerticalMarginVal = firstVerticalMargin
                ?: (parent.context.resources.getDimension(R.dimen.country_top_margin))
            firstVerticalMargin = firstVerticalMarginVal
            outRect.top = firstVerticalMarginVal.toInt()
        }
    }
}