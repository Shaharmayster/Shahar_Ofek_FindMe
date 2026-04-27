package com.example.findme_shahar_ofek

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

/** Stores user-selected images in internal storage so Room can reference them offline. */
object ImageCache {
    fun saveInternalCopy(context: Context, uri: Uri, folderName: String): String {
        val cacheDir = File(context.filesDir, "image_cache/$folderName").apply {
            if (!exists()) mkdirs()
        }
        val target = File(cacheDir, "${UUID.randomUUID()}.jpg")

        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Could not open selected image." }
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return target.absolutePath
    }

    fun existingFileOrNull(path: String?): File? {
        if (path.isNullOrBlank()) return null
        return File(path).takeIf { it.exists() && it.isFile }
    }

    fun deleteIfInternal(path: String?) {
        existingFileOrNull(path)?.delete()
    }
}
