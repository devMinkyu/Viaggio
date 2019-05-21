package com.kotlin.viaggio.view.traveling.detail

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_image_detail.*
import kotlinx.android.synthetic.main.item_detail_image.view.*

class TravelingImageDetailActionDialogFragment:BaseDialogFragment<TravelingImageDetailActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingImageDetailActionDialogFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().currentImageSize.set(it.getInt(ArgName.TRAVEL_CARD_IMG_POSITION.name, 1))
        }
    }

    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingImageDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_image_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().travelOfDayCardImageListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { imageNames ->
                getViewModel().imageShow.set(true)
                travelCardViewPager.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail_image, parent, false)){}
                    override fun getItemCount() = imageNames.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        imageNames[position].let { themeImageName ->
                            if (TextUtils.isEmpty(themeImageName).not()) {
                                Glide.with(holder.itemView.travelCardImg)
                                    .load(themeImageName)
                                    .into(holder.itemView.travelCardImg)
                            }
                        }
                    }
                }
                travelCardViewPager.setCurrentItem(getViewModel().currentImageSize.get() - 1, false)
            }
        })


        travelCardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getViewModel().currentImageSize.set(position + 1)
                getViewModel().timeDisposable?.dispose()
                getViewModel().imageShow.set(true)
                getViewModel().showNotice()
            }
        })
    }
    inner class ViewHandler{
        fun close(){
            dismiss()
        }
    }
}
