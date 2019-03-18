package com.kotlin.viaggio.view.traveling

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.`object`.TravelOfDayVal
import com.kotlin.viaggio.databinding.ItemTravelingBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling.*
import kotlinx.android.synthetic.main.item_traveling.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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
        val adapter = TravelOfDayAdapter()
        travelingList.adapter = adapter
        getViewModel().travelOfDayPagedLiveData.observe(this, Observer(adapter::submitList))

        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")
            }
        })

    }

    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
    }
    inner class TravelOfDayAdapter :
        PagedListAdapter<TravelOfDay, TravelOfDayViewHolder>(object :
            DiffUtil.ItemCallback<TravelOfDay>() {
            override fun areItemsTheSame(oldItem: TravelOfDay, newItem: TravelOfDay) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: TravelOfDay, newItem: TravelOfDay) = oldItem == newItem
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TravelOfDayViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_traveling, parent, false)
        )

        override fun onBindViewHolder(holder: TravelOfDayViewHolder, position: Int) {
            holder.binding?.data = converter(getItem(position)!!)
            holder.binding?.viewHandler = holder.TravelOfDayViewHandler()
            holder.loadImage(getItem(position)?.themeImageName)
        }

        @SuppressLint("SimpleDateFormat")
        fun converter(travelOfDay: TravelOfDay):TravelOfDayVal{
            val cal = Calendar.getInstance()
            cal.time = travelOfDay.date
            val item = TravelOfDayVal()
            item.id = travelOfDay.id
            item.countries = travelOfDay.dayCountries.joinToString {
                it
            }
            item.dayCount = travelOfDay.travelOfDay
            item.weekend = cal.get(Calendar.DAY_OF_WEEK)
            item.day = SimpleDateFormat("EEE",Locale.ENGLISH).format(cal.time).toUpperCase()
            item.week = SimpleDateFormat("MMM d",Locale.ENGLISH).format(cal.time).toUpperCase()
            return item
        }
    }

    inner class TravelOfDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingBinding>(view)
        fun loadImage(imageName:String?){
            val imgDir = File(context?.filesDir, "images/")
            imageName?.let { themeImageName ->
                if(TextUtils.isEmpty(themeImageName).not()){
                    val imgFile = File(imgDir, themeImageName)
                    if (imgFile.exists()) {
                        Uri.fromFile(imgFile).let { uri ->
                            Glide.with(itemView.travelingItemThemeImg)
                                .load(uri)
                                .into(itemView.travelingItemThemeImg)
                        }
                    }
                }
            }
        }
        inner class TravelOfDayViewHandler{
            fun detail(){
                baseIntent("http://viaggio.kotlin.com/traveling/${binding?.data?.id}/detail/")
            }
        }
    }
}