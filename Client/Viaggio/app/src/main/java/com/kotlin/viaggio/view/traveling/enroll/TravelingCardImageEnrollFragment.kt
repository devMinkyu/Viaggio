package com.kotlin.viaggio.view.traveling.enroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingCardImageEnrollBinding
import com.kotlin.viaggio.databinding.ItemTravelingOfDayImageBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_card_image_enroll.*
import kotlinx.android.synthetic.main.item_traveling_of_day_image.view.*


class TravelingCardImageEnrollFragment : BaseFragment<TravelingCardImageEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingCardImageEnrollBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_image_enroll, container, false)
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

        travelingOfDayEnrollImageList.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
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
                        holder.imageBinding(list[position].imageName)
                        holder.binding?.viewHandler = holder.TravelingOfDayImgViewHandler()
                        holder.binding?.chooseCount = list[position].chooseCountList
                    }
                }
            }
            if(getViewModel().imageChooseList.isNotEmpty()) {
                travelingOfDayEnrollImageView.setImageFilePath(getViewModel().imageChooseList.last())
            } else {
                travelingOfDayEnrollImageView.setImageFilePath(getViewModel().imageAllList.first().imageName)
            }
        })

        getViewModel().folderNameListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->

                val spinnerAdapter =
                    ArrayAdapter(context!!, R.layout.spinner_continent_item, list)
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_continent_item)

                travelingOfDayEnrollSpinner.adapter = spinnerAdapter
                travelingOfDayEnrollSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        getViewModel().fetchImage(list[position])
                    }
                }
            }
        })
        showBackToTopAnimation()
    }

    private fun showBackToTopAnimation() {
        val animator = backToTop.animate().setDuration(250)
            .translationY(backToTop.height.toFloat() + 150f)
        animator.start()
        travelingOfDayEnrollImageList.addOnScrollListener(object :RecyclerView.OnScrollListener() {
            var showBackToTop = false
            var mNewState = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(mNewState == 1) {
                    if(dy > 0 && showBackToTop.not()) {
                        showBackToTop = true
                        val animator1 = backToTop.animate().setDuration(250)
                            .translationY(0f)
                        animator1.start()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                this.mNewState = newState
                try {
                    if (travelingOfDayEnrollImageList.canScrollVertically(-1).not()) {
                        showBackToTop = false
                        val animator1 = backToTop.animate().setDuration(250)
                            .translationY(backToTop.height.toFloat() + 150f)
                        animator1.start()
                    }
                } catch (e: NullPointerException) {
                    travelingOfDayEnrollImageList.scrollToPosition(0)
                }
            }
        })
    }
    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }
        fun backToTop() {
            travelingOfDayEnrollImageList.smoothScrollToPosition(0)
        }
        fun confirm() {
            if(getViewModel().imageChooseList.isNotEmpty()){
                getViewModel().imageBitmapChooseList.add(travelingOfDayEnrollImageView.croppedImage)
                if (getViewModel().imageChooseList.size == getViewModel().imageBitmapChooseList.size) {
                    getViewModel().selectImage()
                    fragmentPopStack()
                }
            }else {
                getViewModel().imageBitmapChooseList.clear()
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
            layoutParams.width = width / 4
            layoutParams.height = width / 4
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
                    if (getViewModel().entireChooseCount < getViewModel().imageLimitCount) {
                        if(getViewModel().imageChooseList.isNotEmpty()){
                            getViewModel().imageBitmapChooseList.add(travelingOfDayEnrollImageView.croppedImage)
                        }
                        travelingOfDayEnrollImageView.resetDisplay()
                        travelingOfDayEnrollImageView.setImageFilePath(fileNamePath)
                        getViewModel().entireChooseCount += 1
                        binding.chooseCount?.set(getViewModel().entireChooseCount)
                        getViewModel().imageChooseList.add(fileNamePath)
                    }
                } else {
                    var cancelIndex = getViewModel().imageChooseList.indexOf(fileNamePath)
                    if (getViewModel().imageBitmapChooseList.size > cancelIndex) {
                        val bitmap = getViewModel().imageBitmapChooseList[cancelIndex]
                        getViewModel().imageBitmapChooseList.remove(bitmap)
                    }
                    getViewModel().imageChooseList.remove(fileNamePath)

                    getViewModel().entireChooseCount -= 1

                    binding?.chooseCount?.set(0)

                    for ((i, s) in getViewModel().imageChooseList.withIndex()) {
                        val item = getViewModel().imageAllList.firstOrNull {
                            it.imageName == s
                        }
                        item?.chooseCountList?.set(i+1)
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