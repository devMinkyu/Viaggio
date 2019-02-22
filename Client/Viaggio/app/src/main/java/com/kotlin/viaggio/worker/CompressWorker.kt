package com.kotlin.viaggio.worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.work.Data
import androidx.work.WorkerParameters
import com.kotlin.viaggio.android.WorkerName
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CompressWorker(context: Context, params:WorkerParameters):BaseWorker(context, params){
    companion object {
        const val IMG_FOLDER = "images/"
        const val IMG_NAME_FORMAT = "viaggio_%d%d.%s"
    }
    override fun doWork(): Result {
        val fileNames = inputData.getStringArray(WorkerName.COMPRESS_IMAGE.name)
        return fileNames?.let {
            val outputData = Data.Builder().putStringArray(WorkerName.COMPRESS_IMAGE.name, recordImage(fileNames).blockingGet().toTypedArray())
                .build()
            Result.success(outputData)
        }?:Result.success()
    }

    private fun recordImage(fileNames: Array<String>):Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            val imageListUri:MutableList<String> = mutableListOf()
            for ((index, fileName) in fileNames.withIndex()) {

                val cameraImg = BitmapFactory.decodeFile(fileName)
                val sampleSize = normalQualitySizeCalculation(fileName)
                val compressImg = Bitmap.createScaledBitmap(cameraImg, (cameraImg.width/sampleSize).toInt(),(cameraImg.height/sampleSize).toInt(),true )

                val imageDir = File(applicationContext.filesDir, IMG_FOLDER)
                if(!imageDir.exists()){
                    imageDir.mkdirs()
                }
                try {
                    if(imageDir.exists()){
                        val imgName = String.format(
                                Locale.getDefault(),
                                IMG_NAME_FORMAT, System.currentTimeMillis(), index, "jpg")
                        val localFile = File(imageDir, imgName)
                        localFile.createNewFile()

                        val out = FileOutputStream(localFile)
                        if (compressImg.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                            out.flush()
                            out.close()
                            imageListUri.add(localFile.absolutePath)
                        }
                    }
                }catch (e:FileNotFoundException){
                    it.onError(e)
                    throw IOException("dir doesn't exit")
                }
            }
            it.onSuccess(imageListUri)
        }).subscribeOn(Schedulers.io())
    }


    private fun normalQualitySizeCalculation(fileName: String):Double{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, options)
        val imageHeight = options.outHeight.toDouble()
        return imageHeight/960
    }
    private fun highQualitySizeCalculation(fileName: String):Double{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, options)
        val imageHeight = options.outHeight.toDouble()
        return imageHeight/1440
    }
}