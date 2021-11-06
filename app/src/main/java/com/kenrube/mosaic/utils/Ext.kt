package com.kenrube.mosaic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun ImageView.setImage(uri: String?) {
    GlideApp.with(context)
        .load(uri)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun Context.dpToPx(dp: Int) = (dp.toFloat() * resources.displayMetrics.density).toInt()

fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    })
}
