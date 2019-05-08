package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingCardImageEnrollBinding
import com.kotlin.viaggio.databinding.ItemTravelingOfDayImageBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_card_image_enroll.*
import kotlinx.android.synthetic.main.item_traveling_of_day_image.view.*


class SettingProfileImageEnrollFragment : BaseFragment<SettingProfileImageEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingCardImageEnrollBinding
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
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_image_enroll, container, false)
//        binding.viewModel = getViewModel()
//        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }

        fun confirm() {

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

            }
        }
    }
}