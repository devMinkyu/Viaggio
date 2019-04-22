package com.kotlin.viaggio.view.traveling.enroll

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_card_enroll.*
import org.jetbrains.anko.support.v4.toast
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
        travelCardEnrollImageList.adapter = object : RecyclerView.Adapter<TravelCardEnrollViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                TravelCardEnrollViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_card_img, parent, false))
            override fun getItemCount() = getViewModel().imageList.size + 1
            override fun onBindViewHolder(holder: TravelCardEnrollViewHolder, position: Int) {
                holder.binding?.viewHandler = holder.TravelCardEnrollViewHandler()
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

//        getViewModel().imageFirstLiveData.observe(this, Observer {
//            it.getContentIfNotHandled()?.let { image ->
//                when (image) {
//                    is Bitmap -> {
//                        Glide.with(context!!)
//                            .load(image)
//                            .into(travelOfDayImage)
//                    }
//                    is String -> {
//                        val imgDir = File(context?.filesDir, "images/")
//                        if (TextUtils.isEmpty(image).not()) {
//                            val imgFile = File(imgDir, image)
//                            if (imgFile.exists()) {
//                                Uri.fromFile(imgFile).let { uri ->
//                                    Glide.with(context!!)
//                                        .load(uri)
//                                        .into(travelOfDayImage)
//                                }
//                            } else { }
//                        } else { }
//                    }
//                    else -> { }
//                }
//            }
//        })
//        getViewModel().changeCursor.observe(this, Observer {event ->
//            event.getContentIfNotHandled()?.let {
//                travelingOfDayEnrollContents.text?.let {
//                    travelingOfDayEnrollContents.setSelection(travelingOfDayEnrollContents.text.toString().length)
//                }
//            }
//        })
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
    }

    inner class TravelCardEnrollViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCardImgBinding>(view)

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