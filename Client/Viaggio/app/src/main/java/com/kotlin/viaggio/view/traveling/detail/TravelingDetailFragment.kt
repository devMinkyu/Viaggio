package com.kotlin.viaggio.view.traveling.detail

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_detail.*
import kotlinx.android.synthetic.main.fragment_traveling_of_day_enroll.*
import kotlinx.android.synthetic.main.item_traveling_pager_img.view.*
import java.io.File


class TravelingDetailFragment:BaseFragment<TravelingDetailFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingDetailBinding
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(travelingDetailLayout, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = appBar.layoutParams
        params.width = width
        params.height = width
        val imgDir = File(context?.filesDir, "images/")
        getViewModel().travelOfDayCardImageListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { imageNames ->
                travelingDetailDayImg.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        object :RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_pager_img, parent, false)){}
                    override fun getItemCount() = imageNames.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        imageNames[position].let { themeImageName ->
                            if(TextUtils.isEmpty(themeImageName).not()){
                                val imgFile = File(imgDir, themeImageName)
                                if (imgFile.exists()) {
                                    Uri.fromFile(imgFile).let { uri ->
                                        Glide.with(holder.itemView.travelingPagerImg)
                                            .load(uri)
                                            .into(holder.itemView.travelingPagerImg)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
        travelingDetailDayImg.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 0){
                    enableSliding(true)
                }else{
                    enableSliding(false)
                }
            }
        })
//        scrollContainer.setOnTouchListener { v, event ->
//            when(event.action){
//                MotionEvent.ACTION_DOWN -> enableSliding(true)
//                MotionEvent.ACTION_UP ->{
//                    if(travelingDetailDayImg.currentItem != 0){
//                        enableSliding(false)
//                    }
//                }
//            }
//            false
//        }

    }
    inner class ViewHandler{
        fun back(){
            fragmentPopStack()
        }
        fun add(){
            TravelingDetailActionDialogFragment().show(fragmentManager!!,TravelingDetailActionDialogFragment.TAG)
        }
        fun modify(){
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/card/")
        }
    }
}