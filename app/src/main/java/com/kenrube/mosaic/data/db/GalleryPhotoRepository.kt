package com.kenrube.mosaic.data.db

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.kenrube.mosaic.data.supportedMimeTypes
import com.kenrube.mosaic.domain.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION") // to avoid warning on DATA column
class GalleryPhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context
    ) : PhotoRepository {
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

        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val idColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                val id = it.getInt(idColumnIndex)
                val imagePath = it.getString(dataColumnIndex)
                val imageUri = Uri.Builder().scheme(ContentResolver.SCHEME_FILE).path(imagePath).build()
                images.add(Photo(id, imageUri))
            }
        }

        return images
    }
}
