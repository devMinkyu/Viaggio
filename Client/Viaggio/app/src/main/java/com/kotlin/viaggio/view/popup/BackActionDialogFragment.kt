package com.kotlin.viaggio.view.popup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentActionDialogBackBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import kotlinx.android.synthetic.main.fragment_action_dialog_back.*
import javax.inject.Inject

class BackActionDialogFragment:BaseDialogFragment<BackActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = BackActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:FragmentActionDialogBackBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_back, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { mContext ->
            MobileAds.initialize(mContext)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        adView.adListener = object :AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                getViewModel().loading.set(false)
            }
        }
    }
    inner class ViewHandler{
        fun close() {
            dismiss()
        }
        fun yes() {
            activity?.finish()
        }
    }
}
class BackActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    val loading = ObservableBoolean(true)
}