package com.kenrube.mosaic.data.db

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.kenrube.mosaic.R
import com.kenrube.mosaic.data.supportedMimeTypes
import com.kenrube.mosaic.domain.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GalleryPhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoRepository {

    @Suppress("DEPRECATION") // To avoid warning on DATA column
    override suspend fun getPhotos(): List<Photo> {
        val images = ArrayList<Photo>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA)
        val selection = """${MediaStore.MediaColumns.MIME_TYPE} IN 
            (${supportedMimeTypes.joinToString { "'$it'" }}) 
            AND ${MediaStore.MediaColumns.SIZE} > 0"""
            .trimMargin()
        val selectionArgs: Array<String>? = null
        val sortOrder = "${MediaStore.MediaColumns._ID} DESC"

        val cursor =
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val idColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                val id = it.getInt(idColumnIndex)
                val imagePath = it.getString(dataColumnIndex)
                val imageUri =
                    Uri.Builder().scheme(ContentResolver.SCHEME_FILE).path(imagePath).build()
                images.add(Photo(id, imageUri))
            }
        }

        return images
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @Throws(IOException::class)
    override suspend fun savePhoto(bitmap: Bitmap): Uri {
        val folderName = context.getString(R.string.app_name)
        val fileName = "IMG_${dateFormat.format(Date())}"

        val fos: OutputStream?
        val imageUri: Uri?
        var imageFile: File? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + folderName
                )
            }
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri == null) {
                throw IOException("Failed to create new MediaStore record")
            }
            fos = resolver.openOutputStream(imageUri)
        } else {
            @Suppress("DEPRECATION")
            val picturesDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString()
            val imagesDir = File(picturesDirectory + File.separator + folderName)
            if (!imagesDir.exists()) {
                imagesDir.mkdir()
            }
            imageFile = File(imagesDir, "$fileName.jpg")
            imageUri = imageFile.toUri()
            fos = FileOutputStream(imageFile)
        }
        fos.use {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)) {
                throw IOException("Failed to save bitmap")
            }
            it?.flush()
        }

        imageFile?.run { // before Android Q
            MediaScannerConnection
                .scanFile(context, arrayOf(imageFile.toString()), arrayOf("image/jpeg"), null)
        }

        return imageUri
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US)
    }
}
