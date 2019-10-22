package com.kotlin.viaggio.view.traveling.day_trip

import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.obj.TravelCardValue
import com.kotlin.viaggio.databinding.*
import com.kotlin.viaggio.extenstions.baseIntent
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.traveling.TravelCardViewHandler
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_travel_day_trip.*
import kotlinx.android.synthetic.main.item_travel_day_trip_left.view.*
import kotlinx.android.synthetic.main.item_travel_day_trip_right.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class TravelDayTripFragment : BaseFragment<TravelDayTripFragmentViewModel>() {
    companion object {
        val TAG: String = TravelDayTripFragment::class.java.simpleName
    }

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                travelingContainer, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }

    lateinit var binding: FragmentTravelDayTripBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_day_trip, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        travelDayTripList.layoutManager = LinearLayoutManager(context)
        travelDayTripList.addItemDecoration(TravelDayTripItemDecoration())
        val adapter = TravelDayTripAdapter()
        travelDayTripList.adapter = adapter
        getViewModel().travelDayTripPagedLiveData.observe(this, Observer(adapter::submitList))
    }

    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }

        fun enroll() {
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
        }
    }

    inner class TravelDayTripAdapter :
        PagedListAdapter<TravelCard, RecyclerView.ViewHolder>(object :
            DiffUtil.ItemCallback<TravelCard>() {
            override fun areItemsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem.localId == newItem.localId
            override fun areContentsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem == newItem
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when(viewType){
                0 -> TravelDayTripLeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_travel_day_trip_left, parent, false))
                else -> TravelDayTripRightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_travel_day_trip_right, parent, false))
            }
        override fun getItemViewType(position: Int): Int {
            return position % 2
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            getViewModel().notEmpty.set(true)
            val travelCardVal = getItem(position)
            val item = travelCardVal?.let {
                TravelCardValue().apply {
                    id = it.localId
                    content = it.content
                    country = it.country
                    theme = if(it.theme.isNotEmpty()) it.theme.joinToString(", ") else resources.getString(R.string.base_theme)
                    imageName =
                        if (it.imageNames.isNotEmpty()) it.imageNames[Random.nextInt(it.imageNames.size)] else ""
                    travelId = it.travelLocalId
                    travelOfDay = it.travelOfDay
                    time = SimpleDateFormat("a h:mm", Locale.getDefault()).format(it.time)
                }
            }
            when(holder){
                is TravelDayTripLeftViewHolder ->{
                    holder.binding?.data = item
                    holder.binding?.viewHandler = holder.TravelDayTripViewHandlerImp()
                    holder.round()
                    holder.loadImage(item?.imageName)
                }
                is TravelDayTripRightViewHolder ->{
                    holder.binding?.data = item
                    holder.binding?.viewHandler = holder.TravelDayTripViewHandlerImp()
                    holder.round()
                    holder.loadImage(item?.imageName)
                }
            }
        }
    }

    inner class TravelDayTripLeftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelDayTripLeftBinding>(view)
        fun round(){
            val drawable = context?.getDrawable(R.drawable.round_bg) as GradientDrawable
            itemView.travelingItemThemeImg.background = drawable
            itemView.travelingItemThemeImg.clipToOutline = true
        }
        fun loadImage(imageName: String?) {
            if (TextUtils.isEmpty(imageName).not()) {
                Glide.with(itemView)
                    .load(imageName)
                    .into(itemView.travelingItemThemeImg)
                itemView.TravelingItemTheme.visibility = View.GONE
                itemView.travelingItemInfo.visibility = View.GONE
                itemView.travelingItemThemeBg.visibility = View.GONE
            }
        }

        inner class TravelDayTripViewHandlerImp:TravelCardViewHandler {
            override fun detail() {
                getViewModel().setSelectedTravelCard(binding?.data?.id)
                baseIntent("http://viaggio.kotlin.com/traveling/detail/")
            }
        }
    }
    inner class TravelDayTripRightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelDayTripRightBinding>(view)
        fun round(){
            val drawable = context?.getDrawable(R.drawable.round_bg) as GradientDrawable
            itemView.travelingItemThemeImg1.background = drawable
            itemView.travelingItemThemeImg1.clipToOutline = true
        }
        fun loadImage(imageName: String?) {
            if (TextUtils.isEmpty(imageName).not()) {
                Glide.with(itemView)
                    .load(imageName)
                    .into(itemView.travelingItemThemeImg1)
                itemView.TravelingItemTheme1.visibility = View.GONE
                itemView.travelingItemInfo1.visibility = View.GONE
                itemView.travelingItemThemeBg1.visibility = View.GONE
            }
        }
        inner class TravelDayTripViewHandlerImp: TravelCardViewHandler {
            override fun detail() {
                getViewModel().setSelectedTravelCard(binding?.data?.id)
                baseIntent("http://viaggio.kotlin.com/traveling/detail/")
            }
        }
    }
}

class TravelDayTripItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null
    private var remainHorMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            val firstHorMarginVal1 = firstHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.common_margin))
            firstHorMargin = firstHorMarginVal1
            outRect.top = firstHorMarginVal1.toInt()
        } else {
            val remainHorMargin1 = remainHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.tool_bar_title)) * -1
            remainHorMargin = remainHorMargin1
            outRect.top = (remainHorMargin1.toInt())
        }
    }
}