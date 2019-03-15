package com.kotlin.viaggio.view.travel.kinds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


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
        fun selectedKinds(kinds: String){
            when(kinds){
                "overseas" ->{
                    getViewModel().selectKind(kinds)
                    baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
                }
                "domestic" ->{
                    getViewModel().selectKind(kinds)
                }
            }
            dismiss()
        }
        fun selectBeforeKinds(kinds: String){
            when(kinds) {
                "overseas" -> {
                    getViewModel().openCalendarView(kinds)
                }
                "domestic" -> {
                    getViewModel().openCalendarView(kinds)
                }
            }
        }
    }
}


class TravelKindsBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel(){
    fun openCalendarView(kinds: String) {
        rxEventBus.openCalendar.onNext(true)
        selectKind(kinds)
    }
    fun selectKind(kinds: String){
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVEL_KINDS, kinds).blockingAwait()
    }
}