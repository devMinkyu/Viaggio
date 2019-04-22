package com.kotlin.viaggio.data.source

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.kotlin.viaggio.android.ClearCache
import com.kotlin.viaggio.view.sign.common.Encryption
import dagger.Lazy
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.result.WhenDoneListener
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class LocalDataSource @Inject constructor() {
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    @Inject
    lateinit var clearCache: ClearCache
    @Inject
    lateinit var encryption: Encryption

    companion object {
        const val CACHE_IMG_FOLDER = "images/"
        const val IMG_NAME_FORMAT = "viaggio_%d%d.%s"
        const val FILE_PROVIDER_AUTHORITY = "com.kotlin.viaggio.fileprovider"
        const val IMG_FOLDER = "images/"
    }

    fun savePhotoResult(photoResult: PhotoResult): Single<Uri> {
        return Single.create { emitter ->
            val file: File
            try {
                file = createTempFile()
            } catch (err: Exception) {
                emitter.onError(err)
                return@create
            }
            photoResult.saveToFile(file)
                .whenDone(object : WhenDoneListener<Any> {
                    override fun whenDone(it: Any?) {
                        val fileUri: Uri
                        try {
                            fileUri = getFileUri(file)
                            emitter.onSuccess(fileUri)
                        } catch (err: IllegalAccessException) {
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
                val imgName = String.format(Locale.getDefault(), IMG_NAME_FORMAT, System.currentTimeMillis(), i, extension)
                val imgNameHash = encryption.encryptionValue(imgName)
                result = File(dir, imgNameHash)
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

    @SuppressLint("Recycle")
    fun imageAllPath(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)

        val cursor = appCtx.get().contentResolver?.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )
        var lastIndex: Int
        cursor?.let {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val columnDisplayName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val absolutePathOfImage = cursor.getString(columnIndex)
                val nameOfFile = cursor.getString(columnDisplayName)
                lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile)
                lastIndex = if (lastIndex >= 0) lastIndex else nameOfFile.length - 1

                if (TextUtils.isEmpty(absolutePathOfImage).not()) {
                    list.add(absolutePathOfImage)
                }
            }
        }
        cursor?.close()
        return list
    }

    fun cacheFile(bitmap: Bitmap): Single<List<String>> {
        return Single.create(SingleOnSubscribe<File> { emitter ->
            val cacheFile = createTempFile()
            val out = FileOutputStream(cacheFile)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush()
                out.close()
                emitter.onSuccess(cacheFile)
            }
        }).subscribeOn(Schedulers.io())
            .flatMap {
                recordImage(arrayOf(it.absolutePath))
            }
    }
    fun cacheFile(bitmaps: List<Bitmap>): Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> { emitter ->
            val result = mutableListOf<String>()
            for (bitmap in bitmaps) {
                val cacheFile = createTempFile()
                val out = FileOutputStream(cacheFile)
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush()
                    out.close()
                    result.add(cacheFile.absolutePath)
                }
            }
            emitter.onSuccess(result)
        }).subscribeOn(Schedulers.io())
            .flatMap {
                recordImage(it.toTypedArray())
            }
    }
    private fun recordImage(fileNames: Array<String>):Single<List<String>> {
        return Single.create(SingleOnSubscribe<List<String>> {
            val imageListUri:MutableList<String> = mutableListOf()
            for ((index, fileName) in fileNames.withIndex()) {
                if(File(fileName).exists()){
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 2
                    val cameraImg = BitmapFactory.decodeFile(fileName, options)

                    val sampleSize = normalQualitySizeCalculation(fileName)

                    val exit = ExifInterface(fileName)
                    val rotate = exifOrientationToDegrees(exit.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL))

                    val matrix = Matrix()
                    matrix.postRotate(rotate.toFloat())
                    val resizedBitmap = Bitmap.createBitmap(cameraImg, 0, 0, cameraImg.width, cameraImg.height, matrix, true)

                    val compressImg = Bitmap.createScaledBitmap(resizedBitmap, (resizedBitmap.width/sampleSize).toInt(),(resizedBitmap.height/sampleSize).toInt(),true)

                    val imageDir = File(appCtx.get().filesDir, IMG_FOLDER)
                    if(!imageDir.exists()){
                        imageDir.mkdirs()
                    }
                    try {
                        if(imageDir.exists()){
                            val imgName = String.format(
                                Locale.getDefault(),
                                IMG_NAME_FORMAT, System.currentTimeMillis(), index, "jpg")
                            val imgNameHash = encryption.encryptionValue(imgName)
                            val localFile = File(imageDir, imgNameHash)
                            localFile.createNewFile()

                            val out = FileOutputStream(localFile)
                            if (compressImg.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                                out.flush()
                                out.close()
                                imageListUri.add(localFile.absolutePath)

                                cameraImg.recycle()
                                resizedBitmap.recycle()
                                compressImg.recycle()
                            }
                        }
                    }catch (e: FileNotFoundException){
                        it.onError(e)
                        throw IOException("dir doesn't exit")
                    }
                }
            }
            clearCache.deleteCache(appCtx.get())
            it.onSuccess(imageListUri)
        }).subscribeOn(Schedulers.io())
    }


    private fun normalQualitySizeCalculation(fileName: String):Double{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 2
        BitmapFactory.decodeFile(fileName, options)
        val imageHeight = options.outHeight.toDouble()
        val imageWidth = options.outWidth.toDouble()
        return if (imageWidth > imageHeight) {
            imageWidth / 960
        } else {
            imageHeight / 960
        }
    }

    private fun highQualitySizeCalculation(fileName: String): Double {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileName, options)
        val imageHeight = options.outHeight.toDouble()
        val imageWidth = options.outWidth.toDouble()
        return if (imageWidth > imageHeight) {
            imageWidth / 1440
        } else {
            imageHeight / 1440
        }
    }

    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
}