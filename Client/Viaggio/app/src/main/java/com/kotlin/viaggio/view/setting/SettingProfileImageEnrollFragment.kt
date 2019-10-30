package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.text.TextUtils
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
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_profile_image_enroll.*
import kotlinx.android.synthetic.main.item_profile_image.view.*


class SettingProfileImageEnrollFragment : BaseFragment<SettingProfileImageEnrollFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentProfileImageEnrollBinding
//    override fun onResume() {
//        super.onResume()
//        if (sliderInterface == null)
//            sliderInterface = Slidr.replace(
//                enroll_container, SlidrConfig.Builder()
//                    .position(SlidrPosition.LEFT)
//                    .build()
//            )
//    }
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

        fun confirm() {
            getViewModel().confirm()
            fragmentPopStack()
        }
        fun backToTop() {
            travelingOfDayEnrollImageList.smoothScrollToPosition(0)
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