package com.kotlin.viaggio.view.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.viaggio.R
import kotlinx.android.synthetic.main.fragment_dialog_loading.*

class LoadingDialogFragment: AbstractBaseDialogFragment() {
    companion object {
        val TAG: String = LoadingDialogFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        return inflater.inflate(R.layout.fragment_dialog_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        loading_anim.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        loading_anim.cancelAnimation()
    }

    override fun getTheme(): Int {
        return R.style.ViaggioAppTheme_Dialog_Opaque
    }
}
