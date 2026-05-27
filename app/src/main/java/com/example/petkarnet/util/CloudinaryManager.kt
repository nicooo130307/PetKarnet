package com.example.petkarnet.util

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import java.io.File
import java.io.FileOutputStream
import com.cloudinary.android.callback.ErrorInfo

object CloudinaryManager {

    private const val CLOUD_NAME = "ddppdm2pe"

    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to CLOUD_NAME,
            "upload_preset" to "petkarnet_preset"
        )
        MediaManager.init(context, config)
    }

    // Subir imagen desde un File
    fun uploadImage(imageFile: File, callback: (String?) -> Unit) {
        MediaManager.get().upload(imageFile.absolutePath)
            .unsigned("petkarnet_preset")
            .option("folder", "petkarnet_app")
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    callback(url)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    callback(null)
                }
            })
            .dispatch()
    }

    // Función auxiliar para convertir Uri a File (útil para imágenes de la galería)
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("cloudinary_upload", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}