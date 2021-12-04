package com.kenrube.mosaic.presentation.features.photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.kenrube.mosaic.R
import com.kenrube.mosaic.databinding.FragmentPhotoBinding
import com.kenrube.mosaic.domain.model.FilterType
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY
import com.kenrube.mosaic.presentation.features.photo.FilterIntensityBottomSheet.Companion.INTENSITY_KEY
import com.kenrube.mosaic.presentation.features.photo.adapter.FilterItemDecoration
import com.kenrube.mosaic.presentation.features.photo.adapter.FilterListAdapter
import com.kenrube.mosaic.presentation.features.photo.adapter.UiFilter
import com.kenrube.mosaic.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding get() = _binding!!

    private val viewModel: PhotoViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private val args: PhotoFragmentArgs by navArgs()

    private val animTime: Long by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    private var intensity: Int = FilterType.NONE.defaultIntensity

    private val intensityObserver = Observer<Int> { intensity ->
        this.intensity = intensity
        binding.photo.filter.adjust(intensity)
        binding.photo.requestRender()
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
        savedInstanceState?.run {
            intensity = getInt(SAVED_STATE_INTENSITY_KEY)
        }
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
        observeViewState()
    }

    override fun onStart() {
        super.onStart()
        getNavigationResultLiveData<Int>(INTENSITY_KEY)!!
            .observe(viewLifecycleOwner, intensityObserver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_STATE_INTENSITY_KEY, intensity)
    }

    private fun setupUI() {
        val photoUri = args.photoUri
        binding.photo.transitionName = photoUri.toString()

        setupRecyclerView()
        binding.close.setOnClickListener {
            navController.navigateUp()
        }
        binding.share.setOnClickListener {
            lifecycleScope.launch {
                binding.photo.captureImage { bitmap ->
                    viewModel.onEvent(PhotoEvent.SaveTempPhoto(bitmap))
                }
            }
        }
        binding.save.setOnClickListener {
            lifecycleScope.launch {
                binding.photo.captureImage { bitmap ->
                    viewModel.onEvent(PhotoEvent.SavePhoto(bitmap))
                }
            }
        }

        lifecycleScope.launch {
            binding.photo.setImage(photoUri)
            startPostponedEnterTransition()
        }
    }

    private fun observeViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                // These events are independent so we can process them consequentially

                val photoUri: Uri? = it.photoStored?.getContentIfNotHandled()
                photoUri?.run {
                    val message = getString(R.string.photo_saved_message, getString(R.string.app_name))
                    val action = getString(R.string.photo_open_saved_action)
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                        .setAction(action) { requireContext().openImageForViewing(photoUri) }
                        .show()
                }

                val photoNotStored: Boolean = it.photoNotStored?.getContentIfNotHandled() == Unit
                if (photoNotStored) {
                    val message = getString(R.string.photo_not_saved_message)
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }

                val tempPhotoUri: Uri? = it.tempPhotoStored?.getContentIfNotHandled()
                tempPhotoUri?.run {
                    requireContext().shareImage(this)
                }

                val tempPhotoNotStored: Boolean = it.tempPhotoNotStored?.getContentIfNotHandled() == Unit
                if (tempPhotoNotStored) {
                    val message = getString(R.string.photo_temp_not_saved_message)
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.filterList.apply {
            setHasFixedSize(true)
            addItemDecoration(FilterItemDecoration(context.dpToPx(16), context.dpToPx(6)))
            adapter = FilterListAdapter { filter, isFirstClick ->
                val filterType = filter.id
                if (isFirstClick) {
                    binding.photo.filterType = filterType
                    intensity = filterType.defaultIntensity
                    binding.photo.filter.adjust(filterType.defaultIntensity)
                    binding.photo.requestRender()
                } else {
                    hideViews()
                    openFilterIntensityDialog()
                }
            }
            (adapter as FilterListAdapter).submitList(
                listOf(
                    UiFilter(FilterType.SATURATION, R.drawable.sunflower_saturation, R.string.filter_saturation),
                    UiFilter(FilterType.SOLARIZE, R.drawable.sunflower_solarize, R.string.filter_solarize),
                    UiFilter(FilterType.PIXELATION, R.drawable.sunflower_pixelation, R.string.filter_pixelation),
                    UiFilter(FilterType.SWIRL, R.drawable.sunflower_swirl, R.string.filter_swirl),
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

        val action = PhotoFragmentDirections.openFilterIntensityDialogAction(intensity)
        navController.navigate(action)
    }

    companion object {
        private const val SAVED_STATE_INTENSITY_KEY = "SavedStateIntensityKey"
    }
}
