package com.kotlin.viaggio.view.traveling

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.common.BaseViewModel
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_detail.*
import javax.inject.Inject

class TravelingDetailActionDialogFragment:BaseDialogFragment<TravelingDetailActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDetailActionDialogFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
            clp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            clp.leftMargin = it[0]
            clp.topMargin = it[1]
            clp.rightMargin = resources.getDimension(R.dimen.tool_bar_title).toInt()
            detailDialogChangeCountry.layoutParams = clp
        }

        getViewModel().completeLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
