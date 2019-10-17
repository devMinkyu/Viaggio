package com.kotlin.viaggio.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_finish.*

class LogoutActionDialogFragment:BaseDialogFragment<LogoutActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = LogoutActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogLogoutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_logout, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerPop.setOnClickListener { dismiss() }

        getViewModel().completeLiveDate.observe(this, Observer {
            dismiss()
        })
    }

    inner class ViewHandler{
        fun cancel(){
            dismiss()
        }
        fun logout(){
            if(checkInternet()) {
                getViewModel().logout()
            } else {
                showNetWorkError()
            }

        }
    }
}