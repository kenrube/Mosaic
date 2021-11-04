package com.kenrube.mosaic.data.db

import android.content.Context
import android.provider.MediaStore
import com.kenrube.mosaic.domain.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION") // to avoid warning on DATA column
class GalleryPhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context
    ) : PhotoRepository {
    override suspend fun getPhotos(): List<Photo> {
        val images = ArrayList<Photo>()

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.MediaColumns.DATA),
            "${MediaStore.MediaColumns.MIME_TYPE} REGEXP 'image/(jpeg|png|gif)'",
            null,
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        )
        cursor?.use {
            val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                val uri = it.getString(dataColumnIndex)
                images.add(Photo(uri))
            }
        }

        return images
    }
}