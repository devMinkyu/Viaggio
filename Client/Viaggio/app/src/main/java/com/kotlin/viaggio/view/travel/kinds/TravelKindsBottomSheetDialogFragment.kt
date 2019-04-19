package com.kotlin.viaggio.view.travel.kinds

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.util.*
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
            getViewModel().selectKind(kinds)
            when(kinds){
                "overseas" ->{
                    baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
                }
                "domestic" ->{
                }
            }
            dismiss()
        }
        fun selectBeforeKinds(kinds: String){
            getViewModel().selectKind(kinds)
            SlyCalendarDialog()
                .setSingle(false)
                .setTimeTheme(null)
                .setCallback(object :SlyCalendarDialog.Callback{
                    override fun onDataSelected(
                        firstDate: Calendar?,
                        secondDate: Calendar?,
                        hours: Int,
                        minutes: Int
                    ) {
                        dismiss()
                    }
                    override fun onCancelled() {}
                })
                .show(fragmentManager!!, null)
        }
    }
}


class TravelKindsBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel(){
    fun selectKind(kinds: String){
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVEL_KINDS, kinds).blockingAwait()
        when(kinds){
            "overseas" ->{
                rxEventBus.travelType.onNext(0)
            }
            "domestic" ->{
                rxEventBus.travelType.onNext(1)
            }
        }
    }
}