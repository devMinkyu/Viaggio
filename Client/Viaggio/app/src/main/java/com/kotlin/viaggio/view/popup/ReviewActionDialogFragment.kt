package com.kotlin.viaggio.view.popup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.databinding.FragmentActionDialogBackBinding
import com.kotlin.viaggio.databinding.FragmentActionDialogReviewBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import kotlinx.android.synthetic.main.fragment_action_dialog_back.*
import javax.inject.Inject

class ReviewActionDialogFragment:BaseDialogFragment<ReviewActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = BackActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:FragmentActionDialogReviewBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_review, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    
    inner class ViewHandler{
        fun close() {
            getViewModel().nextReview()
            dismiss()
        }
        fun yes() {
            getViewModel().goReview()
            var appPackageName = activity?.packageName
            appPackageName?.let {
                val list = it.split(".")
                appPackageName = list.take(3).joinToString(".")
                try {
                    startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")), 200)
                }catch (ex:android.content.ActivityNotFoundException) {
                    startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")), 200)
                }
            }
            dismiss()
        }
    }
}
class ReviewActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    fun nextReview() {
        prefUtilService.putBool(AndroidPrefUtilService.Key.NEXT_GIVE_REVIEW, true).blockingAwait()
    }

    fun goReview() {
        prefUtilService.putBool(AndroidPrefUtilService.Key.GIVE_REVIEW, true).blockingAwait()
    }

    val loading = ObservableBoolean(true)
}