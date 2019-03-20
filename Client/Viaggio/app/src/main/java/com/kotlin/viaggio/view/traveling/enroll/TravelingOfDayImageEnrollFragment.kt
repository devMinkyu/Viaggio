package com.kotlin.viaggio.view.traveling.enroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingOfDayImageEnrollBinding
import com.kotlin.viaggio.databinding.ItemTravelingOfDayImageBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_of_day_image_enroll.*
import kotlinx.android.synthetic.main.item_traveling_of_day_image.view.*


class TravelingOfDayImageEnrollFragment : BaseFragment<TravelingOfDayImageEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingOfDayImageEnrollBinding
    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                enroll_container, SlidrConfig.Builder()
                    .position(SlidrPosition.TOP)
                    .build()
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_of_day_image_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = travelingOfDayEnrollImageView.layoutParams
        params.width = width
        params.height = width
        travelingOfDayEnrollImageView.layoutParams = params

        travelingOfDayEnrollImageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> enableSliding(false)
                MotionEvent.ACTION_UP -> {
                    if (travelingOfDayEnrollImageList.canScrollVertically(-1).not()) {
                        enableSliding(true)
                    }
                }
            }
            false
        }

        travelingOfDayEnrollImageList.layoutManager = GridLayoutManager(context, 5, RecyclerView.VERTICAL, false)
        getViewModel().imagePathList.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->
                travelingOfDayEnrollImageList.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingOfDayImgViewHolder(
                            LayoutInflater.from(parent.context).inflate(
                                R.layout.item_traveling_of_day_image,
                                parent,
                                false
                            )
                        )

                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as TravelingOfDayImgViewHolder
                        holder.imageBinding(list[position])
                        holder.binding?.viewHandler = holder.TravelingOfDayImgViewHandler()
                        holder.binding?.chooseCount = getViewModel().chooseCountList[position]
                    }
                }
            }
            travelingOfDayEnrollImageView.setImageFilePath(getViewModel().imageChooseList[0])
        })
        travelingOfDayEnrollImageList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            travelingOfDayEnrollImageList?.let {
                if (travelingOfDayEnrollImageList.canScrollVertically(-1).not()) {
                    enableSliding(true)
                } else {
                    enableSliding(false)
                }
            }
        }
    }


    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }

        fun confirm() {
            getViewModel().imageBitmapChooseList.add(travelingOfDayEnrollImageView.croppedImage)
            if (getViewModel().imageChooseList.size == getViewModel().imageBitmapChooseList.size) {
                getViewModel().selectImage()
                fragmentPopStack()
            }
        }
    }

    inner class TravelingOfDayImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemTravelingOfDayImageBinding>(itemView)
        private lateinit var fileNamePath: String

        fun imageBinding(string: String) {
            fileNamePath = string
            val layoutParams = itemView.travelingCardContainer.layoutParams
            layoutParams.width = width / 5
            layoutParams.height = width / 5
            itemView.travelingCardContainer.layoutParams = layoutParams

            binding?.let {
                Glide.with(itemView)
                    .load(string)
                    .into(itemView.travelingCardListImage)
            }
        }

        inner class TravelingOfDayImgViewHandler {
            fun imagePicker() {
                if (binding?.chooseCount?.get() == 0) {
                    if (getViewModel().entireChooseCount < 20) {
                        getViewModel().imageBitmapChooseList.add(travelingOfDayEnrollImageView.croppedImage)
                        travelingOfDayEnrollImageView.resetDisplay()
                        getViewModel().entireChooseCount += 1
                        binding.chooseCount?.set(getViewModel().entireChooseCount)
                        getViewModel().imageChooseList.add(fileNamePath)
                        travelingOfDayEnrollImageView.setImageFilePath(fileNamePath)
                    }
                } else {
                    if (getViewModel().entireChooseCount != 1) {
                        var cancelIndex = getViewModel().imageChooseList.indexOf(fileNamePath)
                        if (getViewModel().imageBitmapChooseList.size > cancelIndex) {
                            val bitmap = getViewModel().imageBitmapChooseList[cancelIndex]
                            getViewModel().imageBitmapChooseList.remove(bitmap)
                        }
                        getViewModel().imageChooseList.remove(fileNamePath)

                        getViewModel().entireChooseCount -= 1

                        binding?.chooseCount?.set(0)

                        for ((i, s) in getViewModel().imageChooseList.withIndex()) {
                            val index = getViewModel().imageAllList.indexOf(s)
                            getViewModel().chooseCountList[index].set(i + 1)
                        }
                        if (getViewModel().imageChooseList.isNotEmpty()) {
                            travelingOfDayEnrollImageView.setImageFilePath(getViewModel().imageChooseList.last())
                            cancelIndex = getViewModel().imageChooseList.indexOf(getViewModel().imageChooseList.last())
                            if (getViewModel().imageBitmapChooseList.size > cancelIndex) {
                                val bitmap = getViewModel().imageBitmapChooseList[cancelIndex]
                                getViewModel().imageBitmapChooseList.remove(bitmap)
                            }
                        }

                    }
                }
            }
        }
    }
}