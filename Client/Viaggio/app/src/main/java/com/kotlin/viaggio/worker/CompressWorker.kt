package com.kotlin.viaggio.worker

import android.content.Context
import android.graphics.Bitmap
import androidx.work.WorkerParameters
import com.kotlin.viaggio.android.WorkerName
import id.zelory.compressor.Compressor

class CompressWorker(context: Context, params:WorkerParameters):BaseWorker(context, params){
    override fun doWork(): Result {
        val fileName = inputData.keyValueMap[WorkerName.COMPRESS_IMAGE.name] as MutableList<*>

        for (any in fileName) {
            val image = any as Bitmap
//            val a = Compressor(appCtx.get()).compressToBitmap(image)
        }

        return Result.success()
    }
}