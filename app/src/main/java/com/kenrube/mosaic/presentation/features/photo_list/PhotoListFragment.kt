package com.kenrube.mosaic.presentation.features.photo_list

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kenrube.mosaic.R
import com.kenrube.mosaic.data.supportedMimeTypes
import com.kenrube.mosaic.databinding.FragmentPhotoListBinding
import com.kenrube.mosaic.presentation.features.photo_list.StoragePermissionDialogFragment.Companion.BUNDLE_KEY
import com.kenrube.mosaic.presentation.features.photo_list.StoragePermissionDialogFragment.Companion.REQUEST_KEY
import com.kenrube.mosaic.presentation.features.photo_list.adapter.PhotoListAdapter
import com.kenrube.mosaic.presentation.features.photo_list.adapter.UiModel
import com.kenrube.mosaic.presentation.permissions.PermissionStatus
import com.kenrube.mosaic.utils.dpToPx
import com.kenrube.mosaic.utils.openAppSystemSettings
import com.kenrube.mosaic.utils.widgets.EqualSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding: FragmentPhotoListBinding get() = _binding!!

    private val storagePermissionDialog: StoragePermissionDialogFragment by lazy {
        childFragmentManager.setFragmentResultListener(REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val openSettings = bundle.getBoolean(BUNDLE_KEY)
            if (openSettings) {
                requireContext().openAppSystemSettings()
            }
        }
        StoragePermissionDialogFragment()
    }

    private val viewModel: PhotoListViewModel by viewModels()

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            resolveStoragePermissionStatus(isGranted)
        }

    private val openStoragePhotoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val uri = result.data?.data
            uri?.let { navigateToPhoto(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewState()
    }

    override fun onResume() {
        super.onResume()

        val isStoragePermissionGranted = checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED
        resolveStoragePermissionStatus(isStoragePermissionGranted)
    }

    private fun setupUI() {
        setupRecyclerView()
        binding.allowPhotosAccess.setOnClickListener {
            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                showPhotoAccessRationaleDialog()
            } else {
                requestStoragePermissionLauncher.launch(READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.photoList.apply {
            setHasFixedSize(true)
            addItemDecoration(EqualSpacingItemDecoration(context.dpToPx(3)))
            adapter = PhotoListAdapter { item ->
                when (item) {
                    is UiModel.PhotoUiModel -> {
                        navigateToPhoto(item.uri)
                    }
                    is UiModel.ActionUiModel -> {
                        if (item.action == Intent.ACTION_OPEN_DOCUMENT) {
                            openStoragePhotoPicker()
                        }
                    }
                }
            }
        }
    }

    private fun observeViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                binding.progress.isVisible = it.loading
                binding.photosAccessWarning.isVisible = it.showingPermissionWarning
                binding.photoList.apply {
                    isVisible = it.photos.isNotEmpty()

                    val list = arrayListOf<UiModel>()
                    if (it.photos.isNotEmpty()) {
                        list.add(UiModel.ActionUiModel(
                            -1,
                            R.drawable.ic_baseline_collections_24,
                            R.string.photos_open_photos_action,
                            Intent.ACTION_OPEN_DOCUMENT
                        ))
                        list.addAll(it.photos.map {
                                photo -> UiModel.PhotoUiModel(photo.id, photo.uri)
                        })
                    }
                    (adapter as PhotoListAdapter).submitList(list)
                }

                // workaround to hide dialog (if it's still shown) when we granted
                // Storage permission via App Settings and returned to the app
                if (!it.loading && !it.showingPermissionWarning /* photos successfully loaded */) {
                    (childFragmentManager.findFragmentByTag(StoragePermissionDialogFragment.TAG)
                            as? DialogFragment)?.dismiss()
                }
            }
        }
    }

    private fun resolveStoragePermissionStatus(isGranted: Boolean) {
        viewModel.storagePermissionStatus = if (isGranted)
            PermissionStatus.PermissionGranted
        else
            PermissionStatus.PermissionDenied
    }

    private fun showPhotoAccessRationaleDialog() {
        storagePermissionDialog.show(childFragmentManager, StoragePermissionDialogFragment.TAG)
    }

    private fun openStoragePhotoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
        }
        openStoragePhotoPickerLauncher.launch(intent)
    }

    private fun navigateToPhoto(uri: Uri) {
        val action = PhotoListFragmentDirections.openPhotoAction(uri)
        findNavController().navigate(action)
    }
}
