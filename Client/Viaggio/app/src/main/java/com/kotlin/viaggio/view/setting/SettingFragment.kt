package com.kotlin.viaggio.view.setting

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.design.snackbar


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
                if (value) {
                    view.snackbar(resources.getText(R.string.sync_check_done))
                } else {
                    if (checkInternet()) {
                        SyncActionDialogFragment().show(fragmentManager!!, SyncActionDialogFragment.TAG)
                    } else {
                        showNetWorkError()
                    }
                }
            }
        })
        getViewModel().imageName.observe(this, Observer {
            it.getContentIfNotHandled()?.let {image ->
                val drawable = context?.getDrawable(R.drawable.oval_bg) as GradientDrawable
                settingProfileImg.background = drawable
                settingProfileImg.clipToOutline = true
                if(TextUtils.isEmpty(image)) {
                    Glide.with(settingProfileImg)
                        .load(ResourcesCompat.getDrawable(resources, R.drawable.icon_profile, null))
                        .into(settingProfileImg)
                } else {
                    Glide.with(settingProfileImg)
                        .load(image)
                        .into(settingProfileImg)
                }
            }
        })
        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                view.snackbar("완료 되었습니다.")
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
        fun uploadCheck() {
            if (checkInternet()) {
                if(getViewModel().getTraveling()) {
                     view?.snackbar("여행을 종료 해주시길 바랍니다.")
                } else {
                    UploadCheckActionDialogFragment().show(fragmentManager!!, UploadCheckActionDialogFragment.TAG)
                }
            } else {
                showNetWorkError()
            }
        }
    }
}


