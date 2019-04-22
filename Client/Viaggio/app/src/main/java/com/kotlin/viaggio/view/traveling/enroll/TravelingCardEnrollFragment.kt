package com.kotlin.viaggio.view.traveling.enroll

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_card_enroll.*
import kotlinx.android.synthetic.main.item_traveling_card_img.view.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TravelingCardEnrollFragment : BaseFragment<TravelingCardEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingCardEnrollBinding
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(enroll_container, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })

        travelCardEnrollImageList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        getViewModel().imageLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                travelCardEnrollImageList.adapter = object : RecyclerView.Adapter<TravelCardEnrollViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelCardEnrollViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_card_img, parent, false))
                    override fun getItemCount() = list.size + 1
                    override fun onBindViewHolder(holder: TravelCardEnrollViewHolder, position: Int) {
                        holder.binding?.viewHandler = holder.TravelCardEnrollViewHandler()
                        if(position > 0){
                            holder.itemView.travelingPagerImg.visibility = View.VISIBLE
                            holder.loadImage(list[position - 1])
                        }else{
                            holder.itemView.travelingPagerImg.visibility = View.GONE
                        }
                    }
                }
            }
        })

        travelCardEnrollImageList.setOnScrollChangeListener { v, _, _, _, _ ->
            travelCardEnrollImageList?.let {
                if (travelCardEnrollImageList.canScrollHorizontally(-1).not()) {
                    enableSliding(true)
                } else {
                    enableSliding(false)
                }
            }
        }

        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { permissionError ->
                when (permissionError) {
                    PermissionError.STORAGE_PERMISSION -> toast(resources.getString(R.string.storage_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().imageViewShow.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/traveling/enroll/image/")
            }
        })
    }

    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }
        fun save() {
            showLoading()
            getViewModel().saveCard()
        }

        fun enrollOfTime() {
            val cal = Calendar.getInstance()
            getViewModel().contents.set("${getViewModel().contents.get()}\n${SimpleDateFormat(resources.getString(R.string.travel_of_day_time_pattern), Locale.ENGLISH).format(cal.time)}\n")

        }

        fun changeCountry(){

        }

        fun changeDayCount(){

        }
    }

    inner class TravelCardEnrollViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCardImgBinding>(view)

        fun loadImage(image:Any){
            when (image) {
                is Bitmap -> {
                    Glide.with(context!!)
                        .load(image)
                        .into(itemView.travelingPagerImg)
                }
                is String -> {
                    val imgDir = File(context?.filesDir, "images/")
                    if (TextUtils.isEmpty(image).not()) {
                        val imgFile = File(imgDir, image)
                        if (imgFile.exists()) {
                            Uri.fromFile(imgFile).let { uri ->
                                Glide.with(itemView)
                                    .load(uri)
                                    .into(itemView.travelingPagerImg)
                            }
                        } else { }
                    } else { }
                }
                else -> { }
            }
        }
        inner class TravelCardEnrollViewHandler{
            fun imageAdd(){
                getViewModel().permissionCheck(
                    rxPermission.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }
}