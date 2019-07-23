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
        fun imageModify() {
            getViewModel().modify(2)
            dismiss()
        }
        fun modify(){
            getViewModel().modify(0)
            dismiss()
        }
        fun areaModify() {
            getViewModel().modify(3)
            dismiss()
        }
        fun themeModify() {
            getViewModel().modify(4)
            dismiss()
        }
        fun delete(){
            getViewModel().delete()
            dismiss()
        }
    }
}