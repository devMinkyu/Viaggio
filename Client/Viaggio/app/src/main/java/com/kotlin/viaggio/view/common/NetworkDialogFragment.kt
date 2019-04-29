package com.kotlin.viaggio.view.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.viaggio.R
import kotlinx.android.synthetic.main.fragment_dialog_network.*

class NetworkDialogFragment: AbstractBaseDialogFragment() {
    companion object {
        val TAG: String = NetworkDialogFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close.setOnClickListener {
            dismiss()
        }
        network_container.setOnClickListener {
            dismiss()
        }
    }
    override fun onResume() {
        super.onResume()
        network_anim.playAnimation()
    }
    override fun onPause() {
        super.onPause()
        network_anim.cancelAnimation()
    }
    override fun getTheme(): Int {
        return R.style.ViaggioAppTheme_Dialog_Opaque
    }
}
