package com.kotlin.viaggio.view.travel.enroll

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.TravelingError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_travel_enroll.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*


class TravelEnrollFragment : BaseFragment<TravelEnrollFragmentViewModel>() {
    companion object {
        val TAG:String = TravelEnrollFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelEnrollBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        binding.travelingThemes.layoutManager = layoutManager
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(travelingContainer, SlidrConfig.Builder().position(
                SlidrPosition.LEFT)
                .listener(object :SlidrListener{
                    override fun onSlideClosed() {
                        fragmentPopStack()
                    }
                    override fun onSlideStateChanged(state: Int) {}
                    override fun onSlideChange(percent: Float) {}
                    override fun onSlideOpened() {}
                })
                .build())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().goToCamera.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/camera/")
            }

        })
        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { permissionError ->
                when (permissionError) {
                    PermissionError.NECESSARY_PERMISSION -> toast(resources.getString(R.string.camera_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().errorMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { error ->
                when(error){
                    TravelingError.THEME_EMPTY -> toast(resources.getString(R.string.theme_empty))
                    TravelingError.COUNTRY_EMPTY -> toast(resources.getString(R.string.country_empty))
                    else -> {}
                }
            }
        })

        getViewModel().travelThemeListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->
                travelingThemes.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = ThemeTravelingSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_selected_theme, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as ThemeTravelingSelectedViewHolder
                        holder.binding?.data = list[position]
                        holder.binding?.viewHandler = ViewHandler()
                    }
                }
            }
        })

        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
//                baseIntent("http://viaggio.kotlin.com/traveling/start/")
                baseIntent("http://viaggio.kotlin.com/home/main/")
            }
        })
    }

    inner class ViewHandler{
        fun cameraOpen(){
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
        fun addTheme(){
            baseIntent("http://viaggio.kotlin.com/home/main/theme/")
        }
        fun changeDate(){
            alert {
                lateinit var datePicker: DatePicker
                customView {
                    verticalLayout {
                        datePicker = datePicker {
                            this.maxDate = System.currentTimeMillis()
                        }
                    }
                    okButton {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, datePicker.year)
                        cal.set(Calendar.MONTH, datePicker.month)
                        cal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                        getViewModel().changeStartOfDay(SimpleDateFormat(resources.getString(R.string.date_format),Locale.ENGLISH).format(cal.time))
                    }
                    cancelButton {
                        it.dismiss()
                    }
                }
            }.show()
        }
        fun travelStart(){
            if(getViewModel().travelStart()){
                showLoading()
            }
        }
    }

    inner class ThemeTravelingSelectedViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingSelectedThemeBinding>(view)
    }
}