package com.kotlin.viaggio.worker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.work.WorkerParameters
import com.kotlin.viaggio.android.WorkerName
import id.zelory.compressor.Compressor
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CompressWorker(context: Context, params:WorkerParameters):BaseWorker(context, params){
    companion object {
        const val IMG_FOLDER = "images/"
    }
    override fun doWork(): Result {
        val fileNames = inputData.getStringArray(WorkerName.COMPRESS_IMAGE.name)
        fileNames?.let {
            for (fileName in fileNames) {
                val compressImage = Compressor(applicationContext).compressToBitmap(File(fileName))

                recordImage(compressImage).subscribe()
            }
        }
        return Result.success()
    }

    private fun recordImage(bitmap: Bitmap):Single<Uri> {
        return Single.create(SingleOnSubscribe<Uri> {
            val imageDir = File(applicationContext.filesDir, IMG_FOLDER)
            if(!imageDir.exists()){
                imageDir.mkdirs()
            }
            try {
                if(imageDir.exists()){
                    val localFile = File(imageDir, "ttttt.jpg")
                    localFile.createNewFile()

                    val out = FileOutputStream(localFile)
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                        out.flush()
                        out.close()
                        it.onSuccess(Uri.parse(imageDir.absolutePath))
                    }
                }
            }catch (e:FileNotFoundException){
                it.onError(e)
                throw IOException("dir doesn't exit")
            }
        }).subscribeOn(Schedulers.io())
    }
}