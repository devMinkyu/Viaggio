package com.kotlin.viaggio.view.traveling

import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
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
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelCardValue
import com.kotlin.viaggio.databinding.ItemTravelingBinding
import com.kotlin.viaggio.databinding.ItemTravelingDayCountBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling.*
import kotlinx.android.synthetic.main.item_traveling.view.*
import kotlin.random.Random


class TravelingFragment : BaseFragment<TravelingFragmentViewModel>() {
    companion object {
        val TAG: String = TravelingFragment::class.java.simpleName
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

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        travelingList.layoutManager = LinearLayoutManager(context)
        travelingList.addItemDecoration(TravelingItemDecoration())
        val adapter = TravelCardAdapter()
        travelingList.adapter = adapter
        getViewModel().travelCardPagedLiveData.observe(this, Observer(adapter::submitList))

        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")
            }
        })
        getViewModel().showTravelCard.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                if (it) {
                    baseIntent("http://viaggio.kotlin.com/traveling/detail/")
                } else {
                    baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
                }
            }
        })
    }

    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }

        fun enroll() {
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
        }
    }

    inner class TravelCardAdapter :
        PagedListAdapter<TravelCard, RecyclerView.ViewHolder>(object :
            DiffUtil.ItemCallback<TravelCard>() {
            override fun areItemsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem == newItem
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when(viewType){
                0 -> TravelCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling, parent, false))
                else -> TravelCardCountViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_day_count, parent, false))
            }
        var count = 0
        override fun getItemViewType(position: Int): Int {
            return if(position == 0){
                count = getItem(position)?.travelOfDay?:0
                1
            }else{
                if(count != getItem(position)?.travelOfDay?:0){
                    count = getItem(position)?.travelOfDay?:0
                    1
                }else{
                    0
                }
            }
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            getViewModel().notEmpty.set(true)
            val travelCardVal = getItem(position)
            val item = travelCardVal?.let {
                TravelCardValue().apply {
                    id = it.id
                    content = it.content
                    country = it.country
                    theme = it.theme.joinToString(", ")
                    imageName =
                        if (it.imageNames.isNotEmpty()) it.imageNames[Random.nextInt(it.imageNames.size)] else ""
                    travelId = it.travelId
                    travelOfDay = it.travelOfDay
                }
            }
            when(holder){
                is TravelCardViewHolder ->{
                    holder.binding?.data = item
                    holder.binding?.viewHandler = holder.TravelCardViewHandlerImp()
                    holder.round()
                    holder.loadImage(item?.imageName)
                }
                is TravelCardCountViewHolder ->{
                    holder.binding?.data = item
                    holder.binding?.viewHandler = holder.TravelCardCountViewHandlerImp()
                    holder.round()
                    holder.loadImage(item?.imageName)
                }
            }
        }
    }

    inner class TravelCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingBinding>(view)
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
            }
        }

        inner class TravelCardViewHandlerImp:TravelCardViewHandler {
            override fun detail() {
                getViewModel().setSelectedTravelCard(binding?.data?.id)
            }
        }
    }
    inner class TravelCardCountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingDayCountBinding>(view)
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
            }
        }
        inner class TravelCardCountViewHandlerImp:TravelCardViewHandler {
            override fun detail() {
                getViewModel().setSelectedTravelCard(binding?.data?.id)
            }
        }
    }
}
interface TravelCardViewHandler{
    fun detail()
}


class TravelingItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            val firstHorMarginVal1 = firstHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.title_start))
            firstHorMargin = firstHorMarginVal1
            outRect.top = firstHorMarginVal1.toInt()
        }
    }
}