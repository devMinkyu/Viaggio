package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.SignError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting_my_profile.*


class SettingMyProfileFragment : BaseFragment<SettingMyProfileFragmentViewModel>() {
    companion object {
        val TAG:String = SettingMyProfileFragment::class.java.simpleName
    }
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(setting_profile_container, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingMyProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_my_profile, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })
    }
    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
        fun save(){
            showLoading()
            getViewModel().save()
        }
        fun changeProfileImg(){

        }
    }
}


