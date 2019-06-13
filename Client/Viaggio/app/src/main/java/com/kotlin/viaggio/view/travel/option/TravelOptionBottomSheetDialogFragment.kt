package com.kotlin.viaggio.view.travel.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingFinishActionDialogFragment


class TravelOptionBottomSheetDialogFragment : BaseBottomDialogFragment<TravelOptionBottomSheetDialogFragmentViewModel>() {
    companion object {
        val TAG:String = TravelOptionBottomSheetDialogFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentBottomSheetDialogTravelOptionBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_travel_option, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun changeTitle(){
            TravelTitleBottomSheetDialogFragment().show(fragmentManager!!, TravelTitleBottomSheetDialogFragment.TAG)
            dismiss()
        }
        fun addCountry(){
            if(getViewModel().travel.travelKind == 0){
                baseIntent("http://viaggio.kotlin.com/option/country/")
            }else{
                baseIntent("http://viaggio.kotlin.com/option/domestics/")
            }
            dismiss()
        }
        fun addTheme(){
            baseIntent("http://viaggio.kotlin.com/option/theme/")
            dismiss()
        }
        fun changeRepresentativeImage(){
            baseIntent("http://viaggio.kotlin.com/option/image/")
            dismiss()
        }

        fun finish(){
            TravelingFinishActionDialogFragment().show(fragmentManager!!, TravelingFinishActionDialogFragment.TAG)
            dismiss()
        }
    }
}