package com.kotlin.viaggio.view.sign.common

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Singleton
import kotlin.experimental.and


@Singleton
class Encryption {
    fun encryptionValue(value: String) = encryptionMD5(value)

    private fun encryptionMD5(value: String):String{
        var MD5 = ""
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(value.toByteArray())
            val byteData = md.digest()
            val sb = StringBuffer()
            for (i in byteData.indices) {
                sb.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
            }
            MD5 = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return encryptionSHA256(MD5)
    }

    private fun encryptionSHA256(value: String):String{
        var SHA = ""
        try {
            val sh = MessageDigest.getInstance("SHA-256")
            sh.update(value.toByteArray())
            val byteData = sh.digest()
            val sb = StringBuffer()
            for (i in byteData.indices) {
                sb.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
            }
            SHA = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return SHA
    }
}