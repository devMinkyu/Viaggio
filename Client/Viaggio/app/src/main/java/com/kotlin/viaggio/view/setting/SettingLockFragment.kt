package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_setting_lock.*


class SettingLockFragment : BaseFragment<SettingLockFragmentViewModel>() {
    companion object {
        val TAG: String = SettingLockFragment::class.java.simpleName
    }

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                lockContainer, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSettingLockBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_lock, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lock_switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked != getViewModel().lockApp.get()){
                lock_switch.isChecked = getViewModel().lockApp.get()
            }
        }

        getViewModel().completableLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                lock_switch.isChecked = true
            }
        })

    }

    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }
        fun fingerPrint(){
            getViewModel().fingerPrintLockApp.set(getViewModel().fingerPrintLockApp.get().not())
        }
        fun lockApp(){
            if(getViewModel().lockApp.get()){
                getViewModel().initPassword()
                getViewModel().lockApp.set(false)
                getViewModel().fingerPrintLockApp.set(false)
            } else {
                val frag = SettingLockActionDialogFragment()
                val arg = Bundle()
                arg.putBoolean(ArgName.LOCK_ENROLL_MODE.name, true)
                frag.arguments = arg
                frag.show(fragmentManager!!, SettingLockActionDialogFragment.TAG)
                getViewModel().fingerPrintLockApp.set(false)
            }
        }
    }
}


