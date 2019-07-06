package com.kotlin.viaggio.view.setting

import android.Manifest
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
import com.kotlin.viaggio.data.obj.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting_my_profile.*
import org.jetbrains.anko.design.snackbar


class SettingMyProfileFragment : BaseFragment<SettingMyProfileFragmentViewModel>() {
    companion object {
        val TAG: String = SettingMyProfileFragment::class.java.simpleName
    }

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                setting_profile_container, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
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

        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { permissionError ->
                when (permissionError) {
                    PermissionError.STORAGE_PERMISSION -> view.snackbar(resources.getString(R.string.storage_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().imageViewShow.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/setting/profile/image/")
            }
        })
        getViewModel().imageNameLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {image ->
                val drawable = context?.getDrawable(R.drawable.oval_bg) as GradientDrawable
                profileImage.background = drawable
                profileImage.clipToOutline = true
                if(TextUtils.isEmpty(image)) {
                    Glide.with(profileImage)
                        .load(ResourcesCompat.getDrawable(resources, R.drawable.icon_profile, null))
                        .into(profileImage)
                } else {
                    Glide.with(profileImage)
                        .load(image)
                        .into(profileImage)
                }
            }
        })
    }

    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }

        fun save() {
            showLoading()
            getViewModel().save()
        }

        fun changeProfileImg() {
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}


