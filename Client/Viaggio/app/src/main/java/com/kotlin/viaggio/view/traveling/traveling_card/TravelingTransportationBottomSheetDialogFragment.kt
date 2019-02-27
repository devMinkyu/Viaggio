package com.kotlin.viaggio.view.traveling.traveling_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentBottomSheetDialogTravelingTransportationBinding
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment


class TravelingTransportationBottomSheetDialogFragment : BaseBottomDialogFragment<TravelingTransportationBottomSheetDialogFragmentViewModel>() {
    companion object {
        val TAG:String = TravelingTransportationBottomSheetDialogFragment::class.java.simpleName
    }
    lateinit var binding: FragmentBottomSheetDialogTravelingTransportationBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_traveling_transportation, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    inner class ViewHandler{
        fun selectedTransportation(string: String){
            getViewModel().selectedTransportation(string)
            dismiss()
        }
    }
}