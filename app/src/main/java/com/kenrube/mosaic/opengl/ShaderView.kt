package com.kenrube.mosaic.opengl

import android.content.Context
import android.graphics.*
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kenrube.mosaic.R
import com.kenrube.mosaic.utils.DispatchersProvider
import com.kenrube.mosaic.utils.getRawFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

@AndroidEntryPoint
class ShaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    @Inject
    lateinit var dispatchersProvider: DispatchersProvider

    private val surfaceView = GLSurfaceView(context, attrs).apply {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
    }

    var filter = ShaderFilter(
        context.getRawFile(R.raw.vertex_default),
        context.getRawFile(R.raw.fragment_default)
    )
        set(filter) {
            field = filter
            surfaceView.queueEvent { renderer.setFilter(filter) }
        }

    private val renderer = ShaderRenderer(filter).also {
        surfaceView.apply {
            setRenderer(it)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            requestRender()
        }
    }

    private var currentBitmap: Bitmap? = null

    init {
        addView(surfaceView)
    }

    fun setImage(file: File) = launchOnIO {
        val bitmap = ImageLoader(file.absolutePath).load()
        deleteImage()
        setImage(bitmap)
    }

    private fun setImage(bitmap: Bitmap) {
        currentBitmap = bitmap
        surfaceView.queueEvent { renderer.setImageBitmap(bitmap, false) }
        surfaceView.requestRender()
    }

    private fun deleteImage() {
        surfaceView.queueEvent { renderer.deleteImage() }
        currentBitmap = null
        surfaceView.requestRender()
    }

    private fun View.launchOnIO(block: suspend CoroutineScope.() -> Unit) {
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch(dispatchersProvider.io(), block = block)
    }

    @Suppress("DEPRECATION") // To ignore deprecations of WindowManager's methods
    private inner class ImageLoader(private val imagePath: String) {
        private val outputWidth = context.getSystemService<WindowManager>()!!.defaultDisplay.width
        // Approximate height because here GLSurfaceView doesn't measured height yet
        private val outputHeight = context.getSystemService<WindowManager>()!!.defaultDisplay.height

        fun load(): Bitmap {
            var options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            decode(options)

            val ratio1 = options.outWidth / outputWidth
            val ratio2 = options.outHeight / outputHeight
            val ratioMax = max(ratio1, ratio2)

            options = BitmapFactory.Options().apply { inSampleSize = ratioMax }
            var bitmap = decode(options)!!
            bitmap = rotateImage(bitmap)
            bitmap = scaleBitmap(bitmap)
            return bitmap
        }

        private fun decode(options: BitmapFactory.Options? = null): Bitmap? =
            BitmapFactory.decodeFile(imagePath, options)

        private fun rotateImage(bitmap: Bitmap): Bitmap {
            var rotatedBitmap = bitmap
            val orientation = ExifInterface(imagePath).rotationDegrees
            if (orientation != 0) {
                val matrix = Matrix().apply { postRotate(orientation.toFloat()) }
                rotatedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
            }
            return rotatedBitmap
        }

        private fun scaleBitmap(bitmap: Bitmap): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val (newWidth, newHeight) = getScaleSize(width, height)
            val workBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            if (workBitmap != bitmap) {
                bitmap.recycle()
                return workBitmap
            }
            return bitmap
        }

        private fun getScaleSize(width: Int, height: Int): Pair<Int, Int> {
            val newWidth: Float
            val newHeight: Float
            val widthRatio = width.toFloat() / outputWidth
            val heightRatio = height.toFloat() / outputHeight
            val adjustWidth = widthRatio < heightRatio
            if (adjustWidth) {
                newHeight = outputHeight.toFloat()
                newWidth = newHeight / height * width
            } else {
                newWidth = outputWidth.toFloat()
                newHeight = newWidth / width * height
            }
            return newWidth.roundToInt() to newHeight.roundToInt()
        }
    }
}