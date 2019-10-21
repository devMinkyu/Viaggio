package com.kotlin.viaggio.fcm

import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection

class ViaggioFcmService :FirebaseMessagingService() {
    companion object {
        val TAG: String = ViaggioFcmService::class.java.simpleName
    }
    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    //token 얻어오는곳
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.isNotEmpty().let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            } else {

            }
        }
    }

}