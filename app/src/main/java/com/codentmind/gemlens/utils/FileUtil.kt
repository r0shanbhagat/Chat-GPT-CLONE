package com.codentmind.gemlens.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.graphics.scale
import com.codentmind.gemlens.GemLensApp
import com.codentmind.gemlens.domain.model.MediaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Compresses and saves a Bitmap to a file in the cache directory.
 *
 * @param bitmap The Bitmap to compress and save.
 * @return The File object representing the saved image, or null if the operation failed.
 */
private fun compressAndSaveImage(bitmap: Bitmap): File? {
    // Get the cache directory
    GemLensApp.getInstance().filesDir ?: run {
        Timber.e("Failed to get cache directory")
        return null
    }
    val imgDir = getChatImgDir()
    if (File(imgDir).exists().not()) {
        File(imgDir).mkdirs()
    }

    // Create a file in the cache directory
    val compressedImageFile = File(imgDir, "ai_image${System.currentTimeMillis()}.jpg")
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            // fos.close()
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
fun clearImageDir() {
    clearExistingFiles(File(getChatImgDir()), "ai_image")
    clearExistingFiles(GemLensApp.getInstance().cacheDir, "camera_image")
}

/**
 * Deletes files in the specified directory that match the given file name pattern.
 *
 * @param dir The directory to search for files.
 * @param fileName The pattern to match file names.
 */
fun clearExistingFiles(dir: File, fileName: String) {
    if (dir.isDirectory) {
        val files = dir.listFiles()
        files?.forEach { file ->
            if (file.name.contains(fileName) && file.delete()) {
                Timber.d("File deleted: ${file.name}")
            } else {
                Timber.e("Failed to delete file: ${file.name}")
            }
        }
    }
}

/**
 * Creates a temporary file for storing a camera image in the cache directory.
 *
 * @param context The context used to access the cache directory.
 * @return A temporary File object for the camera image.
 */
fun getTempCameraFile(context: Context): File {
    val storageDir = context.cacheDir
    return File.createTempFile(
        "camera_image_${System.currentTimeMillis()}",
        ".jpg",
        storageDir
    )
}

/**
 * Converts a list of MediaModel objects into a list of MediaModel objects with locally saved images.
 *perform the below operation:
 * 1.Store the bitmap to the local directory
 * 2.Compress the Bitmap
 * 3.Return the Media FileName
 * @receiver List<MediaModel>? The list of MediaModel objects to process.
 * @return A list of MediaModel objects with locally saved images.
 */
suspend fun List<MediaModel>?.getAiMediaList(): List<MediaModel> {
    val imageUriList = mutableListOf<MediaModel>()
    this?.forEach { mediaModel ->
        saveImageLocallyFromUri(GemLensApp.getInstance(), mediaModel.imageUri)?.let {
            val fileName = it.name
            val bitmap = getBitmapFromFileName(fileName)
            imageUriList.add(MediaModel(bitmap = bitmap, fileName = fileName))
        }

    }
    return imageUriList
}


/**
 * Recycles the bitmaps in the provided list of MediaModel objects to free up memory.
 *
 * @param mediaList The list of MediaModel objects containing bitmaps to recycle.
 */
fun recycleBitmap(mediaList: List<MediaModel>) {
    try {
        mediaList.forEach { mediaModel ->
            if (mediaModel.bitmap?.isRecycled == false) {
                mediaModel.bitmap.recycle()
            }
        }
    } catch (e: Exception) {
        Timber.e(e.message)
    }
}

/**
 * Retrieves a Bitmap object from a file name in the chat image directory.
 *
 * @param fileName The name of the file to load the bitmap from.
 * @return The Bitmap object loaded from the file.
 */
fun getBitmapFromFileName(fileName: String): Bitmap =
    BitmapFactory.decodeFile(fileName.getAbsolutePath())

/**
 * Converts a file name into its absolute path in the chat image directory.
 *
 * @receiver String The file name to convert.
 * @return The absolute path of the file.
 */
fun String.getAbsolutePath(): String {
    return getChatImgDir() + "/" + this
}

/**
 * Retrieves the directory path for storing chat images.
 *
 * @return The absolute path of the chat image directory.
 */
fun getChatImgDir(): String {
    return GemLensApp.getInstance().filesDir.absolutePath + "/ChatImages"
}

/**
 * Converts a URI into a Bitmap object.
 *
 * @param context The context used to access the content resolver.
 * @param uri The URI of the image to convert.
 * @return The Bitmap object, or null if the conversion fails.
 */
fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: IOException) {
        Timber.e("uriToBitmap: ${e.message}")
        null
    }
}


/**
 * Compresses and resizes an image from a given URI asynchronously,
 * and saves it as a high-quality JPEG in the local directory.
 *
 * @param context The context used to access the content resolver.
 * @param imageUri The URI of the image to process.
 * @param maxWidth The maximum width for resizing (default is 1280px).
 * @return The compressed image File, or null if the operation fails.
 */
suspend fun saveImageLocallyFromUri(
    context: Context,
    imageUri: Uri,
    maxWidth: Int = 1280
): File? = withContext(Dispatchers.IO) {
    try {
        // Step 1: Open input stream from the URI
        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: return@withContext null

        // Step 2: First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        val imageHeight = options.outHeight
        val imageWidth = options.outWidth

        // Step 3: Calculate inSampleSize
        var inSampleSize = 1
        if (imageWidth > maxWidth) {
            inSampleSize = imageWidth / maxWidth
        }

        // Step 4: Decode bitmap with inSampleSize set
        val inputStream2 = context.contentResolver.openInputStream(imageUri)
        val options2 = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        val sampledBitmap = BitmapFactory.decodeStream(inputStream2, null, options2)
        inputStream2?.close()

        // Step 5: Further resize if needed
        val resizedBitmap = sampledBitmap?.let { bitmap ->
            if (bitmap.width > maxWidth) {
                val ratio = bitmap.height.toFloat() / bitmap.width
                val targetHeight = (maxWidth * ratio).toInt()
                bitmap.scale(maxWidth, targetHeight)
            } else {
                bitmap
            }
        } ?: return@withContext null

        // Step 6: Create a cache file
        compressAndSaveImage(resizedBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
