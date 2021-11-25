package com.kenrube.mosaic.utils

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.kenrube.mosaic.BuildConfig

@GlideModule
class MosaicGlideModule : AppGlideModule() {

    override fun isManifestParsingEnabled(): Boolean = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.apply {
            setDefaultRequestOptions {
                // We load only local images so we can cache them already decoded
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            }
            if (BuildConfig.DEBUG) {
                setLogLevel(Log.DEBUG)
            }
        }
    }
}