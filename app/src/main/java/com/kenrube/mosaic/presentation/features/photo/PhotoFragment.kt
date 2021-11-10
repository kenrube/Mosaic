package com.kenrube.mosaic.presentation.features.photo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kenrube.mosaic.R
import com.kenrube.mosaic.databinding.FragmentPhotoBinding
import com.kenrube.mosaic.utils.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding get() = _binding!!

    private val args: PhotoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_image)
        sharedElementReturnTransition = null
        postponeEnterTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUri = args.photoUri
        binding.photo.transitionName = photoUri.toString()
        GlideApp.with(this)
            .load(photoUri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.photo)
    }
}
