package com.example.application.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    fun resizeImageFromUri(contentResolver: ContentResolver, uri: Uri, width: Int, height: Int): File? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
                val resizedFile = File.createTempFile("resized_image", ".jpg")
                FileOutputStream(resizedFile).use { out ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                resizedFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
