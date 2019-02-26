package com.kotlin.viaggio.view.traveling.traveling_card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_card_enroll.*
import kotlinx.android.synthetic.main.item_camera_image.view.*


class TravelingCardEnrollFragment:BaseFragment<TravelingCardEnrollFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var animator: ViewPropertyAnimator = travelCardEnrollImageBtn.animate()
            .setDuration(0)
            .scaleX(0.25f)
            .scaleY(0.25f)
            .alpha(0f)
        animator.start()
        travelCardEnrollImageAllList.layoutManager = GridLayoutManager(context, 3)
        BottomSheetBehavior.from(travelCardEnrollBottomSheet).setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {
                if(p1 == 2 || p1 == 5){
                     animator = travelCardEnrollImageBtn.animate()
                        .setDuration(150)
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                    animator.start()
                }else{
                    animator = travelCardEnrollImageBtn.animate()
                        .setDuration(0)
                        .scaleX(0.25f)
                        .scaleY(0.25f)
                        .alpha(0f)
                    animator.start()
                }
            }
        })


        getViewModel().imagePathList.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                travelCardEnrollImageAllList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingCardImgViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_camera_image, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as TravelingCardImgViewHolder
                        holder.imageBinding(list[position])
                    }
                }
            }
        })
    }

    inner class ViewHandler{
        fun next(){

        }
        fun openImgList(){
            BottomSheetBehavior.from(travelCardEnrollBottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    inner class TravelingCardImgViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemCameraImageBinding>(itemView)
        private lateinit var fileNamePath:String
        fun imageBinding(string: String){
            fileNamePath = string
            binding?.let {
                Glide.with(itemView)
                    .load(string)
                    .into(itemView.cameraViewListImage)
            }
        }
    }
}