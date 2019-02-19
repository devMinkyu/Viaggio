package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragment<SettingFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.let { context ->
            Glide.with(context)
                .load(R.drawable.empty_gallery)
                .apply(RequestOptions().circleCrop())
                .into(settingUserProfile)
        }
    }
    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
        fun sign(){
            baseIntent("http://viaggio.kotlin.com/home/login/")
        }
    }
}


