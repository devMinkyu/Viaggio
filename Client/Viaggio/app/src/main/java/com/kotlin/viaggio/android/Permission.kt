package com.kotlin.viaggio.android

import android.Manifest
import android.content.Context
import com.tbruyelle.rxpermissions2.RxPermissions
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class Permission @Inject constructor(@param: Named("Application") val mAppContext: Context) {
    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    fun permissionCheck(rxPermissions: RxPermissions){

    }
}


