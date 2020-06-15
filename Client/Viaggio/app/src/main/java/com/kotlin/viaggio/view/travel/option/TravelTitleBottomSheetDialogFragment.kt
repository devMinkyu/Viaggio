package com.kotlin.viaggio.view.travel.option

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseBottomDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog_travel_title.*


class TravelTitleBottomSheetDialogFragment : BaseBottomDialogFragment<TravelTitleBottomSheetDialogFragmentViewModel>() {
    companion object {
        val TAG:String = TravelTitleBottomSheetDialogFragment::class.java.simpleName
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentBottomSheetDialogTravelTitleBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_travel_title, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelOptionTitle.requestFocus()
        getViewModel().confirmLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })

        getViewModel().changeCursorLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                travelOptionTitle.text?.let {
                    travelOptionTitle.setSelection(travelOptionTitle.text.toString().length)
                }
                if(travelOptionTitle.text.length == getViewModel().travelTitle.get()!!.length){
                    getViewModel().isChangeCursor = true
                }
            }
        })

    }

    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun confirm(){
            if(getViewModel().travelTitle.get().isNullOrEmpty()){
                val animator = travelOptionTitleCount.animate()
                    .setDuration(100)
                    .x(0f)
                    .xBy(10f)
                    .setInterpolator(CycleInterpolator(5f))
                animator.start()
            }else{
                getViewModel().confirm()
            }
        }

        fun init(){
            getViewModel().travelTitle.set("")
        }
    }
}