package com.kotlin.viaggio.view.sign.common

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Singleton
import kotlin.experimental.and


@Singleton
class Encryption {
    fun encryptionValue(value: String) = executeGenerate(value)

    private fun executeGenerate(value: String) :String{
        val pwdLength = 8
        val passwordTable =  listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        val random = Random(System.currentTimeMillis())
        val tableLength = passwordTable.size
        val buf = StringBuffer()
        for(i in 0 until pwdLength) {
            buf.append(passwordTable[random.nextInt(tableLength)])
        }
        return encryptionMD5(buf.toString()+value)
    }
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