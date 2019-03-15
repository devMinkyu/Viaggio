package com.kotlin.viaggio.view.traveling.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingDetailActionDialogFragment:BaseDialogFragment<TravelingDetailActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDetailActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun theme(){
            baseIntent("http://viaggio.kotlin.com/traveling/theme/")
            dismiss()
        }
        fun changeBackground(){
            baseIntent("http://viaggio.kotlin.com/traveling/representative/image/")
            dismiss()
        }
    }
}


class TravelingDetailActionDialogFragmentViewModel @Inject constructor() : BaseViewModel()