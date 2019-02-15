package com.kotlin.viaggio.data.source

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.Lazy
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.result.WhenDoneListener
import io.reactivex.Single
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class LocalDataSource @Inject constructor(){
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    companion object {
        const val CACHE_IMG_FOLDER = "images/"
        const val IMG_NAME_FORMAT = "viaggio_%d%d.%s"
        const val FILE_PROVIDER_AUTHORITY = "com.kotlin.viaggio.fileprovider"
    }


    fun savePhotoResult(photoResult: PhotoResult): Single<Uri>{
        return Single.create {emitter ->
            val file:File
            try {
                file = createTempFile()
            } catch (err: Exception) {
                emitter.onError(err)
                return@create
            }
            photoResult.saveToFile(file)
                .whenDone(object :WhenDoneListener<Any>{
                    override fun whenDone(it: Any?) {
                        val fileUri:Uri
                        try {
                            fileUri = getFileUri(file)
                            emitter.onSuccess(fileUri)
                        }catch (err:IllegalAccessException){
                            emitter.onError(err)
                        }
                    }
                })
        }
    }

    @Throws(IOException::class)
    private fun createTempFile(): File {
        return createTempFile("jpg")
    }

    @Throws(IOException::class)
    private fun createTempFile(extension: String): File {
        var result: File? = null
        val target = appCtx.get().cacheDir
        val dir = buildPath(target, CACHE_IMG_FOLDER)
        var isDirExist = dir.exists()
        if (!isDirExist) {
            isDirExist = dir.mkdir()
        }
        if (isDirExist) {
            for (i in 0..99) {
                val imgName =
                    String.format(Locale.getDefault(), IMG_NAME_FORMAT, System.currentTimeMillis(), i, extension)
                result = File(dir, imgName)
                if (!result.exists()) {
                    break
                }
            }
            if (result!!.exists()) {
                throw IOException("too many file insert attempts in short period")
            }
        } else {
            throw IOException("dir doesn't exit")
        }
        val isTempImgFileCreated = result.createNewFile()
        if (!isTempImgFileCreated) {
            throw IOException("file was not created")
        }
        return result
    }

    private fun buildPath(base: File, vararg segments: String): File {
        var cur = base
        for (segment in segments) {
            cur = File(cur, segment)
        }
        return cur
    }

    private fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            appCtx.get(),
            FILE_PROVIDER_AUTHORITY,
            file
        )
    }
}