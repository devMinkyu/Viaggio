package com.kotlin.viaggio.view.common

import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kotlin.viaggio.R

abstract class AbstractBaseBottomDialogFragment : BottomSheetDialogFragment() {
    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.let { windowVal ->
            val params = windowVal.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params as WindowManager.LayoutParams
        }
    }

    override fun getTheme(): Int {
        return R.style.ViaggioAppTheme_Dialog_Opaque
    }
}