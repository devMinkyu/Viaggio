package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_profile_image_enroll.*
import kotlinx.android.synthetic.main.item_profile_image.view.*


class SettingProfileImageEnrollFragment : BaseFragment<SettingProfileImageEnrollFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentProfileImageEnrollBinding
    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                enroll_container, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_image_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelingOfDayEnrollImageList.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        getViewModel().imagePathList.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                travelingOfDayEnrollImageList.adapter = object :RecyclerView.Adapter<SettingProfileImageEnrollViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = SettingProfileImageEnrollViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_image, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: SettingProfileImageEnrollViewHolder, position: Int) {
                        holder.imageBinding(list[position].imageName)
                        holder.binding?.choose = list[position].chooseCountList
                        holder.binding?.viewHandler = holder.SettingProfileImageEnrollViewHandler()
                    }
                }
            }
        })
    }


    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }

        fun confirm() {
            getViewModel().confirm()
            fragmentPopStack()
        }
    }

    inner class SettingProfileImageEnrollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemProfileImageBinding>(itemView)
        private lateinit var fileNamePath: String

        fun imageBinding(string: String) {
            fileNamePath = string
            val layoutParams = itemView.travelingRepresentativeContainer.layoutParams
            layoutParams.width = width / 4
            layoutParams.height = width / 4
            itemView.travelingRepresentativeContainer.layoutParams = layoutParams

            Glide.with(itemView)
                .load(string)
                .into(itemView.profileListImage)
        }

        inner class SettingProfileImageEnrollViewHandler {
            fun imagePicker() {
                if(TextUtils.isEmpty(getViewModel().chooseImage.get())){
                    getViewModel().chooseImage.set(fileNamePath)
                    binding?.choose?.set(1)
                }else{
                    val item = getViewModel().imageAllList.firstOrNull {
                        it.imageName == getViewModel().chooseImage.get()
                    }
                    item?.chooseCountList?.set(0)
                    getViewModel().chooseImage.set(fileNamePath)
                    binding?.choose?.set(1)
                }
            }
        }
    }
}