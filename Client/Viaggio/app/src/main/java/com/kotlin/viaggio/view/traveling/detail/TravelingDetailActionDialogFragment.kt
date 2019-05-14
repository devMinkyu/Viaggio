package com.kotlin.viaggio.view.traveling.detail

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_detail.*

class TravelingDetailActionDialogFragment:BaseDialogFragment<TravelingDetailActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDetailActionDialogFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        arguments?.let {
            getViewModel().location = it.getIntArray(ArgName.TRAVEL_CARD_LOCATION.name)
        }
    }

    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().location?.let {
            val clp = detailDialogChangeCountry.layoutParams as ConstraintLayout.LayoutParams
            clp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            clp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            clp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            clp.leftMargin = it[0]
            clp.topMargin = it[1]
            detailDialogChangeCountry.layoutParams = clp
        }
        showKeyBoard()

        getViewModel().completeLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })

        detailDialogChangeCountry.setOnTouchListener { v, event ->
            if(v.id == R.id.detailDialogChangeCountry){
                v.parent.requestDisallowInterceptTouchEvent(true)
                when(event.action) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }
    override fun onStop() {
        super.onStop()
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }
    inner class ViewHandler{
        fun close(){
            dismiss()
        }
        fun save(){
            getViewModel().save()
        }
    }
}
