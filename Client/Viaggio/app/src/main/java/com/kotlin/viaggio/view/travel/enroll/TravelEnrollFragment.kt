package com.kotlin.viaggio.view.travel.enroll

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.PermissionError
import com.kotlin.viaggio.data.obj.TravelingError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_travel_enroll.*
import org.jetbrains.anko.design.snackbar


class TravelEnrollFragment : BaseFragment<TravelEnrollFragmentViewModel>() {
    companion object {
        val TAG:String = TravelEnrollFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelEnrollBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
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
                    PermissionError.NECESSARY_PERMISSION -> view.snackbar(resources.getString(R.string.camera_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().errorMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { error ->
                when(error){
                    TravelingError.THEME_EMPTY -> view.snackbar(resources.getString(R.string.theme_empty))
                    TravelingError.COUNTRY_EMPTY -> view.snackbar(resources.getString(R.string.country_empty))
                    else -> {}
                }
            }
        })

        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                baseIntent("http://viaggio.kotlin.com/home/main/")

                parentFragmentManager.let { fm ->
                    val cnt = fm.backStackEntryCount
                    for (i in 0 until cnt) {
                        fm.popBackStackImmediate()
                    }
                }
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
        fun back() {
            fragmentPopStack()
        }
        fun addCountry(){
            if(getViewModel().travelKind == 0){
                baseIntent("http://viaggio.kotlin.com/traveling/country/")
            }else{
                baseIntent("http://viaggio.kotlin.com/traveling/country/domestic/")
            }
        }
        fun addTheme(){
            baseIntent("http://viaggio.kotlin.com/home/main/theme/")
        }
        fun changeDate(){
            if(getViewModel().endDate == null){
                baseIntent("http://viaggio.kotlin.com/traveling/calendar/")
            }
        }
        fun travelStart(){
            if(getViewModel().travelStart()){
                showLoading()
            }
        }
    }
}