package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.support.v4.toast


class SettingFragment : BaseFragment<SettingFragmentViewModel>() {
    companion object {
        val TAG: String = SettingFragment::class.java.simpleName
    }

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                setting_container, SlidrConfig.Builder()
                    .position(SlidrPosition.TOP)
                    .build()
            )
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().showDialogLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                showLoading()
            }
        })
        getViewModel().checkLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { value ->
                stopLoading()
                toast(
                    if (value) {
                        "업로드 다됨"
                    } else {
                        "업로드 안됨"
                    }
                )

            }
        })
    }
    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }

        fun sign() {
            baseIntent("http://viaggio.kotlin.com/home/login/")
        }

        fun imageSetting() {
            SettingImageBottomSheetDialogFragment().show(fragmentManager!!, SettingImageBottomSheetDialogFragment.TAG)
        }

        fun lockSetting() {
            baseIntent("http://viaggio.kotlin.com/setting/lock/")
        }

        fun autoUpload() {
            if (getViewModel().isLogin.get()) {
                SettingAutoBottomSheetDialogFragment().show(fragmentManager!!, SettingAutoBottomSheetDialogFragment.TAG)
            } else {
                baseIntent("http://viaggio.kotlin.com/home/login/")
            }
        }

        fun myProfile() {
            if (checkInternet()) {
                baseIntent("http://viaggio.kotlin.com/setting/profile/")
            } else {
                showNetWorkError()
            }
        }

        fun password() {
            if (checkInternet()) {
                baseIntent("http://viaggio.kotlin.com/setting/password/")
            } else {
                showNetWorkError()
            }
        }

        fun logout() {
            if (checkInternet()) {
                LogoutActionDialogFragment().show(fragmentManager!!, LogoutActionDialogFragment.TAG)
            } else {
                showNetWorkError()
            }

        }

        fun sync() {
            if (checkInternet()) {

            } else {
                showNetWorkError()
            }
        }

        fun uploadCheck() {
            if (checkInternet()) {
                UploadCheckActionDialogFragment().show(fragmentManager!!, UploadCheckActionDialogFragment.TAG)
            } else {
                showNetWorkError()
            }
        }
    }
}


