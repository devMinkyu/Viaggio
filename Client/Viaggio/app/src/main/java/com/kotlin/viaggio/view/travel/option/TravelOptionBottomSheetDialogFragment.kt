package com.kotlin.viaggio.view.travel.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingDeleteActionDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingFinishActionDialogFragment
import org.jetbrains.anko.design.snackbar


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().showLoadingLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                showLoading()
            }
        })
        getViewModel().resultLiveData.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let { value ->
                if(value) {
                    view.snackbar(getString(R.string.travel_delete_success))
                    dismiss()
                } else {
                    view.snackbar(getString(R.string.travel_delete_fail))
                }
            }
        })
    }

    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun modifyCalendar() {
            baseIntent("http://viaggio.kotlin.com/option/calendar/")
            dismiss()
        }
        fun changeTitle(){
            showBottomDialog(TravelTitleBottomSheetDialogFragment(), TravelTitleBottomSheetDialogFragment.TAG)
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
        fun changeRepresentativeImage() {
            baseIntent("http://viaggio.kotlin.com/option/image/")
            dismiss()
        }
        fun instagramShare() {
            baseIntent("http://viaggio.kotlin.com/option/instagram/share/")
            dismiss()
        }
        fun travelDelete() {
            getViewModel().checkTraveling().observe(this@TravelOptionBottomSheetDialogFragment, Observer {
                if(it) {
                    val frag = TravelingDeleteActionDialogFragment()
                    val arg = Bundle()
                    arg.putBoolean(ArgName.TRAVEL_CARD_MODE.name, false)
                    frag.arguments = arg
                    showDialog(frag, TravelingDeleteActionDialogFragment.TAG)
                } else {
                    view?.snackbar(getString(R.string.traveling))
                }
            })
        }
    }
}