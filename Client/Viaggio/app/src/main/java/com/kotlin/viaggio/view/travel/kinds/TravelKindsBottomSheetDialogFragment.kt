package com.kotlin.viaggio.view.travel.kinds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import org.jetbrains.anko.design.snackbar


class TravelKindsBottomSheetDialogFragment : BaseBottomDialogFragment<TravelKindsBottomSheetDialogFragmentViewModel>() {
    companion object {
        val TAG:String = TravelKindsBottomSheetDialogFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentBottomSheetDialogTravelKindsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_travel_kinds, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    inner class ViewHandler{
        fun selectedKinds(kinds: Int){
            if(getViewModel().travel){
                view?.snackbar(resources.getText(R.string.traveling))
            }else{
                getViewModel().selectKind(kinds)
                baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
                dismiss()
            }
        }
        fun selectBeforeKinds(kinds: Int) {
            getViewModel().selectKind(kinds)
            baseIntent("http://viaggio.kotlin.com/home/calendar/")
            dismiss()
        }
    }
}