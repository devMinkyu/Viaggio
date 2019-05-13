package com.kotlin.viaggio.view.traveling

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.databinding.FragmentActionDialogTravelingDeleteBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_finish.*

class TravelingDeleteActionDialogFragment:BaseDialogFragment<TravelingDeleteActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingDeleteActionDialogFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().travelCardMode = it.getBoolean(ArgName.TRAVEL_CARD_MODE.name, false)
        }
    }

    lateinit var binding:FragmentActionDialogTravelingDeleteBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_delete, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerPop.setOnClickListener { dismiss() }
    }

    inner class ViewHandler{
        fun cancel(){
            dismiss()
        }
        fun delete(){
            getViewModel().delete()
            dismiss()
        }
    }
}