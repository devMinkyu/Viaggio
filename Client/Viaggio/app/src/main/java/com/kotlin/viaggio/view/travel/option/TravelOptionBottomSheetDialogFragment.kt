package com.kotlin.viaggio.view.travel.option

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
import org.jetbrains.anko.support.v4.toast
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.util.*
import javax.inject.Inject


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

        }
        fun addTheme(){

        }
        fun changeRepresentativeImage(){

        }
    }
}