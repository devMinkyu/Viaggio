package com.kotlin.viaggio.view.traveling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment


class TravelCardBottomSheetDialogFragment : BaseBottomDialogFragment<TravelCardBottomSheetDialogFragmentViewModel>() {
    companion object {
        val TAG:String = TravelCardBottomSheetDialogFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentBottomSheetDialogTravelCardBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_travel_card, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun modify(){
            getViewModel().modify()
            dismiss()
        }
        fun delete(){
            getViewModel().delete()
            dismiss()
        }
    }
}