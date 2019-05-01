package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragment<SettingFragmentViewModel>() {
    companion object {
        val TAG:String = SettingFragment::class.java.simpleName
    }
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(setting_container, SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .build())
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
        fun sign(){
            baseIntent("http://viaggio.kotlin.com/home/login/")
        }
        fun imageSetting(){
            SettingImageBottomSheetDialogFragment().show(fragmentManager!!, SettingImageBottomSheetDialogFragment.TAG)
        }
        fun autoUpload(){
            if(getViewModel().isLogin.get()){
                SettingAutoBottomSheetDialogFragment().show(fragmentManager!!, SettingAutoBottomSheetDialogFragment.TAG)
            }else{
                baseIntent("http://viaggio.kotlin.com/home/login/")
            }
        }
        fun myProfile(){

        }
        fun uploadCheck(){

        }
        fun sync(){

        }
        fun logout(){

        }
    }
}


