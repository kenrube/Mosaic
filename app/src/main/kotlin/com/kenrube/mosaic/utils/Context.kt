package com.kenrube.mosaic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RawRes
import com.kenrube.mosaic.R

fun Context.dpToPx(dp: Int) = (dp.toFloat() * resources.displayMetrics.density).toInt()

fun Context.getRawFile(@RawRes resId: Int): String =
    resources.openRawResource(resId).bufferedReader().use { it.readText() }

fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    })
}

fun Context.openImageForViewing(imageUri: Uri) {
    startActivity(Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(imageUri, "image/jpeg")
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    })
}

fun Context.shareImage(imageUri: Uri) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        setDataAndType(imageUri, "image/jpeg")
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, getString(R.string.photo_sharesheet_title)))
}
