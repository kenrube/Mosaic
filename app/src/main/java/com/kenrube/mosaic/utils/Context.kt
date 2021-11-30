package com.kenrube.mosaic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RawRes

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

fun Context.openActivityForUri(uri: Uri) {
    startActivity(Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    })
}
