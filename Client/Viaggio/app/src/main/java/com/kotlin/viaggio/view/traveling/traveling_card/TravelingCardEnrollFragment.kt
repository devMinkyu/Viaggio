package com.kotlin.viaggio.view.traveling.traveling_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.ItemTravelingCardImageBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_card_enroll.*
import kotlinx.android.synthetic.main.item_traveling_card_image.view.*


class TravelingCardEnrollFragment : BaseFragment<TravelingCardEnrollFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var animator: ViewPropertyAnimator? =
            travelCardEnrollImageBtn?.animate()?.setDuration(0)?.scaleX(0.25f)?.scaleY(0.25f)?.alpha(0f)
        animator?.start()
        travelCardEnrollImageAllList.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        BottomSheetBehavior.from(travelCardEnrollBottomSheet)
            .setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {}
                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == 2 || p1 == 5) {
                        animator =
                            travelCardEnrollImageBtn?.animate()?.setDuration(150)?.scaleX(1f)?.scaleY(1f)?.alpha(1f)
                        animator?.start()
                    } else {
                        animator =
                            travelCardEnrollImageBtn?.animate()?.setDuration(0)?.scaleX(0.25f)?.scaleY(0.25f)?.alpha(0f)
                        animator?.start()
                    }
                }
            })


        getViewModel().imagePathList.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->
                travelCardEnrollImageAllList.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingCardImgViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_card_image, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as TravelingCardImgViewHolder
                        holder.imageBinding(list[position])
                        holder.binding?.viewHandler = holder.TravelingCardImgViewHandler()
                        holder.binding?.chooseCount = getViewModel().chooseCountList[position]
                    }
                }
                Glide.with(context!!)
                    .load(list[0])
                    .into(travelCardEnrollLastImg)
            }
        })
    }

    inner class ViewHandler {
        fun next() {
            if (BottomSheetBehavior.from(travelCardEnrollBottomSheet).state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.from(travelCardEnrollBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
            } else {

            }
        }

        fun openImgList() {
            BottomSheetBehavior.from(travelCardEnrollBottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    inner class TravelingCardImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ItemTravelingCardImageBinding>(itemView)
        private lateinit var fileNamePath: String

        fun imageBinding(string: String) {
            fileNamePath = string
            binding?.let {
                Glide.with(itemView)
                    .load(string)
                    .into(itemView.travelingCardListImage)
            }
        }

        inner class TravelingCardImgViewHandler {
            fun imagePicker() {
                if (binding?.chooseCount?.get() == 0) {
                    if (getViewModel().entireChooseCount < 10) {
                        getViewModel().entireChooseCount += 1
                        binding.chooseCount?.set(getViewModel().entireChooseCount)
                        getViewModel().imageChooseList.add(fileNamePath)
                        Glide.with(context!!)
                            .load(fileNamePath)
                            .into(travelCardEnrollLastImg)
                    }
                } else {
                    getViewModel().imageChooseList.remove(fileNamePath)
                    getViewModel().entireChooseCount -= 1
                    binding?.chooseCount?.set(0)
                    for ((i, s) in getViewModel().imageChooseList.withIndex()) {
                        val index = getViewModel().imageAllList.indexOf(s)
                        getViewModel().chooseCountList[index].set(i + 1)
                    }
                    if (getViewModel().imageChooseList.isNotEmpty()) {
                        Glide.with(context!!)
                            .load(getViewModel().imageChooseList.last())
                            .into(travelCardEnrollLastImg)
                    }
                }
            }
        }
    }
}