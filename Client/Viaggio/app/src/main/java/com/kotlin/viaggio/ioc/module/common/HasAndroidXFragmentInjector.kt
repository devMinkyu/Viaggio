package com.kotlin.viaggio.ioc.module.common

import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector

interface HasAndroidXFragmentInjector {

    /** Returns an [AndroidInjector] of [Fragment]s.  */
    fun androidXFragmentInjector(): AndroidInjector<Fragment>
}