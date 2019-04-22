package com.kotlin.viaggio.view.traveling

import android.graphics.Rect
import android.net.Uri
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.databinding.ItemTravelingBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling.*
import kotlinx.android.synthetic.main.item_traveling.view.*
import kotlinx.android.synthetic.main.item_traveling_pager_img.view.*
import java.io.File


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
                    .listener(object : SlidrListener {
                        override fun onSlideClosed() {
                            fragmentPopStack()
                        }
                        override fun onSlideStateChanged(state: Int) {}
                        override fun onSlideChange(percent: Float) {}
                        override fun onSlideOpened() {}
                    })
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
                if(it){
                    baseIntent("http://viaggio.kotlin.com/traveling/detail/")
                }else{
                    baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
                }
            }
        })
    }

    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
        fun enroll(){
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
        }
    }
    inner class TravelCardAdapter :
        PagedListAdapter<TravelCard, TravelCardViewHolder>(object :
            DiffUtil.ItemCallback<TravelCard>() {
            override fun areItemsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem == newItem
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TravelCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_traveling, parent, false)
        )

        override fun onBindViewHolder(holder: TravelCardViewHolder, position: Int) {
            getViewModel().notEmpty.set(true)
            holder.binding?.data = getItem(position)
            holder.binding?.viewHandler = holder.TravelCardViewHandler()
            holder.loadViewPager(getItem(position)?.imageNames)
        }
    }

    inner class TravelCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingBinding>(view)
        fun loadViewPager(imageNames:ArrayList<String>?){
            if(imageNames.isNullOrEmpty()){
                itemView.travelingItemThemeImg.visibility = View.GONE
            }else{
                itemView.travelingItemThemeImg.visibility = View.VISIBLE
                val params = itemView.travelingItemThemeImg.layoutParams
                params.width = width
                params.height = width
                itemView.travelingItemThemeImg.layoutParams = params

//                itemView.travelingItemIndicator.setCurrPageNumber(0)
                itemView.travelingItemIndicator.setTotalPageNumber(imageNames.size)
                itemView.travelingItemThemeImg.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        object : RecyclerView.ViewHolder(
                            LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_pager_img, parent,false)
                        ){}
                    override fun getItemCount() = imageNames.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        Glide.with(holder.itemView)
                            .load(imageNames[position])
                            .into(holder.itemView.travelingPagerImg)
                    }
                }

                itemView.travelingItemThemeImg.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
//                        itemView.travelingItemIndicator.setCurrPageNumber(position)
                        if(position == 0){
                            enableSliding(true)
                        }else{
                            enableSliding(false)
                        }
                    }
                })

            }
        }
        inner class TravelCardViewHandler{
            fun detail(){
                getViewModel().setSelectedTravelCard(binding?.data?.id)
            }
        }
    }
}



class TravelingItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if(parent.getChildAdapterPosition(view) == 0){
            val firstHorMarginVal1 = firstHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.traveling_top))
            firstHorMargin = firstHorMarginVal1
            outRect.top = firstHorMarginVal1.toInt()
        }
    }
}