package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.SignError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting_password.*


class SettingPasswordFragment : BaseFragment<SettingPasswordFragmentViewModel>() {
    companion object {
        val TAG:String = SettingPasswordFragment::class.java.simpleName
    }
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(setting_password_container, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingPasswordBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_password, container, false)
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

        getViewModel().error.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let { signError ->
                profile_old.setHintTextColor(ResourcesCompat.getColor(resources, R.color.very_light_pink, null))
                profile_confirm.setHintTextColor(ResourcesCompat.getColor(resources, R.color.very_light_pink, null))
                profile_new.setHintTextColor(ResourcesCompat.getColor(resources, R.color.very_light_pink, null))
                when (signError) {
                    SignError.PW_MISMATCH -> {
                        getViewModel().newPasswordConfirm.set("")
                        profile_confirm.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red, null))
                        profile_confirm.hint = getString(R.string.err_pw_mismatch)
                    }
                    SignError.WRONG_PW -> {
                        getViewModel().password.set("")
                        profile_old.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red,null))
                        profile_old.hint = getString(R.string.err_wrong_pw)
                    }
                    SignError.SAME_PW -> {
                        getViewModel().newPassword.set("")
                        profile_new.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red,null))
                        profile_new.hint = getString(R.string.err_pw_same)
                    }
                    SignError.PW_NUM -> {
                        getViewModel().password.set("")
                        profile_new.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red, null))
                        profile_new.hint = getString(R.string.err_password_num)
                    }
                    else -> {}
                }
            }
        })
    }
    inner class ViewHandler{
        fun close(){
            fragmentPopStack()
        }
        fun save(){
            if(getViewModel().save()){
                showLoading()
            }
        }
    }
}


