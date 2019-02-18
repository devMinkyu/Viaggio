package com.kotlin.viaggio.view.traveling

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling.*
import org.jetbrains.anko.support.v4.toast


class TravelingFragment:BaseFragment<TravelingFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        test.setOnClickListener {
            if(getViewModel().traveling){

            }else{
                getViewModel().permissionCheck(rxPermission.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }

        getViewModel().goToCamera.observe(this, Observer {
            it?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/camera/")
                getViewModel().goToCamera.value = null
            }
        })
        getViewModel().permissionRequestMsg.observe(this, Observer {
            when(it){
                PermissionError.NECESSARY_PERMISSION->toast(resources.getString(R.string.camera_permission))
                else -> {}
            }
            getViewModel().permissionRequestMsg.value = null
        })
    }

    inner class ViewHandler
}