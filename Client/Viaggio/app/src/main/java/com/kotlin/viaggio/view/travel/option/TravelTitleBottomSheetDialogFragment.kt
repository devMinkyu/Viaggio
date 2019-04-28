package com.kotlin.viaggio.view.travel.option

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog_travel_title, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelOptionTitle.requestFocus()
//        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

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

//    private fun hide(){
//        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//    }

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