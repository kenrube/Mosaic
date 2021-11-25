package com.kenrube.mosaic.presentation.features.photo

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kenrube.mosaic.R
import com.kenrube.mosaic.databinding.FragmentPhotoBinding
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.INTENSITY_KEY
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY
import com.kenrube.mosaic.presentation.features.photo.adapter.FilterListAdapter
import com.kenrube.mosaic.presentation.features.photo.adapter.FilterItemDecoration
import com.kenrube.mosaic.presentation.features.photo.adapter.UiFilter
import com.kenrube.mosaic.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding get() = _binding!!

    private val args: PhotoFragmentArgs by navArgs()
    private val navController by lazy { findNavController() }

    private val animTime: Long by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    private val intensityObserver = Observer<Int> { intensity ->
        // todo apply filter with intensity
    }

    private val closeFilterIntensityDialogResultListener = FragmentResultListener { _, bundle ->
        val isDialogClosed = bundle.getBoolean(CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY)
        if (isDialogClosed) {
            showViews()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_image)
        sharedElementReturnTransition = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        getNavigationResultLiveData<Int>(INTENSITY_KEY)!!
            .observe(viewLifecycleOwner, intensityObserver)
    }

    private fun setupUI() {
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

        setupRecyclerView()
        binding.close.setOnClickListener {
            navController.navigateUp()
        }
        binding.share.setOnClickListener {
            // todo share image
        }
        binding.save.setOnClickListener {
            // todo save image
        }
    }

    private fun setupRecyclerView() {
        binding.filterList.apply {
            setHasFixedSize(true)
            addItemDecoration(FilterItemDecoration(context.dpToPx(16), context.dpToPx(6)))
            adapter = FilterListAdapter { item, isFirstClick ->
                if (isFirstClick) {
                    // todo apply filter with default intensity
                } else {
                    hideViews()
                    openFilterIntensityDialog()
                }
            }
            (adapter as FilterListAdapter).submitList(
                listOf(
                    UiFilter(0, Uri.EMPTY, getString(R.string.filter_pixelation)),
                    UiFilter(1, Uri.EMPTY, getString(R.string.filter_saturation)),
                    UiFilter(2, Uri.EMPTY, getString(R.string.filter_solarize)),
                    UiFilter(3, Uri.EMPTY, getString(R.string.filter_swirl)),
                )
            )
        }
    }

    private fun hideViews() {
        with(binding.actions) {
            animate()
                .translationY(-height.toFloat())
                .alpha(0f)
                .setDuration(animTime)
        }.start()
        with(binding.filterList) {
            animate()
                .translationY(height.toFloat())
                .alpha(0f)
                .setDuration(animTime)
        }.start()
    }

    private fun showViews() {
        binding.actions.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(animTime)
            .start()
        binding.filterList.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(animTime)
            .start()
    }

    private fun openFilterIntensityDialog() {
        parentFragmentManager.setFragmentResultListener(
            CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY,
            viewLifecycleOwner,
            closeFilterIntensityDialogResultListener
        )

        val action = PhotoFragmentDirections.openFilterIntensityDialogAction(20)
        navController.navigate(action)
    }
}
