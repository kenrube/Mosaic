package com.kenrube.mosaic.opengl

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.net.Uri
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import androidx.exifinterface.media.ExifInterface
import com.kenrube.mosaic.data.resource.ShaderRepository
import com.kenrube.mosaic.domain.model.FilterType
import com.kenrube.mosaic.opengl.filter.*
import com.kenrube.mosaic.utils.coroutine.DispatchersProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

@AndroidEntryPoint
class ShaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    @Inject
    lateinit var dispatchersProvider: DispatchersProvider

    @Inject
    lateinit var shaderRepository: ShaderRepository

    private val surfaceView = GLSurfaceView(context, attrs).apply {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
    }

    var filter = ShaderFilter(
        shaderRepository.getShader(FilterType.NONE).vertexShader,
        shaderRepository.getShader(FilterType.NONE).fragmentShader
    )
        private set(filter) {
            field = filter
            renderer.setFilter(filter)
            surfaceView.requestRender()
        }

    var filterType: FilterType = FilterType.NONE
        set(filterType) {
            field = filterType
            val shader = shaderRepository.getShader(filterType)
            filter = when (filterType) {
                FilterType.NONE -> ShaderFilter(shader.vertexShader, shader.fragmentShader)
                FilterType.PIXELATION -> PixelationFilter(shader.vertexShader, shader.fragmentShader)
                FilterType.SATURATION -> SaturationFilter(shader.vertexShader, shader.fragmentShader)
                FilterType.SOLARIZE -> SolarizeFilter(shader.vertexShader, shader.fragmentShader)
                FilterType.SWIRL -> SwirlFilter(shader.vertexShader, shader.fragmentShader)
            }
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

    fun requestRender() {
        surfaceView.requestRender()
    }

    suspend fun setImage(uri: Uri) = withContext(dispatchersProvider.io()) {
        val bitmap = when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> FileImageLoader(uri).load()
            ContentResolver.SCHEME_CONTENT -> ContentImageLoader(uri).load()
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}. " +
                    "Should be 'file' or 'content'")
        }
        deleteImage()
        setImage(bitmap)
    }

    private fun setImage(bitmap: Bitmap) {
        currentBitmap = bitmap
        renderer.setImageBitmap(bitmap, false)
        surfaceView.requestRender()
    }

    private fun deleteImage() {
        renderer.deleteImage()
        currentBitmap = null
        surfaceView.requestRender()
    }

    // TODO: Rewrite it w/o ton of callbacks
    suspend fun captureImage(onComplete: (Bitmap) -> Unit) = withContext(dispatchersProvider.io()) {
        renderer.captureBitmap { bitmap ->
            onComplete.invoke(bitmap)
        }
        surfaceView.requestRender()
    }

    private inner class FileImageLoader(uri: Uri) : ImageLoader() {
        private val imagePath = uri.path!!

        override fun decodeBitmap(options: BitmapFactory.Options?): Bitmap? =
            BitmapFactory.decodeFile(imagePath, options)

        override fun getImageRotationDegrees(): Int =
            ExifInterface(imagePath).rotationDegrees
    }

    private inner class ContentImageLoader(uri: Uri) : ImageLoader() {
        private val pfd = context.contentResolver.openFileDescriptor(uri, "r")!!

        override fun decodeBitmap(options: BitmapFactory.Options?): Bitmap? {
            // Unexpectedly, BitmapFactory#decodeStream and BitmapFactory#decodeByteArray couldn't
            // decode bitmap; message in Logcat (w/o detailed stacktrace):
            // 'D/skia: --- Failed to create image decoder with message 'unimplemented''
            return BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
        }

        override fun getImageRotationDegrees(): Int =
            pfd.use { ExifInterface(it.fileDescriptor).rotationDegrees }
    }

    @Suppress("DEPRECATION") // To ignore deprecations of WindowManager's methods
    private abstract inner class ImageLoader {
        private val outputWidth = context.getSystemService<WindowManager>()!!.defaultDisplay.width
        // Approximate height because here GLSurfaceView doesn't measured its height yet
        private val outputHeight = context.getSystemService<WindowManager>()!!.defaultDisplay.height

        abstract fun decodeBitmap(options: BitmapFactory.Options? = null): Bitmap?

        abstract fun getImageRotationDegrees(): Int

        fun load(): Bitmap {
            var options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            decodeBitmap(options)

            val ratio1 = options.outWidth / outputWidth
            val ratio2 = options.outHeight / outputHeight
            val ratioMax = max(ratio1, ratio2)

            options = BitmapFactory.Options().apply { inSampleSize = ratioMax }
            var bitmap = decodeBitmap(options)!!
            bitmap = rotateImage(bitmap)
            bitmap = scaleBitmap(bitmap)
            return bitmap
        }

        private fun rotateImage(bitmap: Bitmap): Bitmap {
            var rotatedBitmap = bitmap
            val orientation = getImageRotationDegrees()
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
            if (!workBitmap.sameAs(bitmap)) {
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