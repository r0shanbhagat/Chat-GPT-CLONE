package com.codentmind.gemlens.utils

import android.graphics.Bitmap
import com.codentmind.gemlens.GemLensApp
import com.codentmind.gemlens.domain.model.MediaModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Get image path will perform the below operation:
 * 1.Store the bitmap to the cache directory
 * 2.Compress the Bitmap
 * 3.Return the FilePath
 */
fun Bitmap.getLocalImageUri(): String {
    // 1. Compress and save the bitmap to the cache directory
    val imageFile = compressAndSaveImage(this)

    if (imageFile != null) {
        Timber.d("Image compressed and saved to cache: ${imageFile.path}")
        return imageFile.absolutePath
    }
    return ""
}

/**
 * Compresses and saves a Bitmap to a file in the cache directory.
 *
 * @param bitmap The Bitmap to compress and save.
 * @return The File object representing the saved image, or null if the operation failed.
 */
private fun compressAndSaveImage(bitmap: Bitmap): File? {
    // Get the cache directory
    val cacheDir = GemLensApp.getInstance().cacheDir ?: run {
        Timber.e("Failed to get cache directory")
        return null
    }

    // Create a file in the cache directory
    val compressedImageFile = File(cacheDir, "ai_image${System.currentTimeMillis()}.jpg")
    try {
        if (!compressedImageFile.createNewFile()) {
            Timber.e("Failed to create file in cache directory")
            return null
        }
    } catch (e: IOException) {
        Timber.e("Error creating file: ${e.message}")
        return null
    }

    // Compress the bitmap and write it to the file using Kotlin's use extension function
    try {
        FileOutputStream(compressedImageFile).use { fos ->
            // Adjust the quality as needed (80 is a good starting point)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            return compressedImageFile
        }
    } catch (e: IOException) {
        Timber.e("Error compressing and saving image: ${e.message}")
        return null
    }
}

/**
 * Clears the image cache directory by deleting all JPEG files.
 */
fun clearCacheDir() {
    val cacheDir = GemLensApp.getInstance().cacheDir
    if (cacheDir != null && cacheDir.isDirectory) {
        val files = cacheDir.listFiles()
        files?.forEach { file ->
            if (file.name.contains("ai_image") && file.delete()) {
                Timber.d("File deleted: ${file.name}")
            } else {
                Timber.e("Failed to delete file: ${file.name}")
            }
        }
    }
}

/**
 * Get image uri list
 *
 * @param mediaList
 * @return
 */
fun getImageUriList(mediaList: List<MediaModel>?): List<MediaModel> {
    val imageUriList = mutableListOf<MediaModel>()
    mediaList?.forEach { mediaModel ->
        val imageUri = mediaModel.bitmap.getLocalImageUri()
        if (imageUri.isNotEmpty()) {
            imageUriList.add(MediaModel(mediaModel.bitmap, imageUri))
        }
    }
    return imageUriList
}

/**
 * Get image uri list
 *
 * @param mediaList
 * @return
 */
fun recycleBitmap(mediaList: List<MediaModel>) {
    try {
        mediaList.forEach { mediaModel ->
            if (!mediaModel.bitmap.isRecycled) {
                mediaModel.bitmap.recycle()
            }
        }
    } catch (e: Exception) {
        Timber.e(e.message)
    }
}