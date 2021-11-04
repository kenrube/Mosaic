package com.kenrube.mosaic.presentation.features.photo_list

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenrube.mosaic.databinding.FragmentPhotoListBinding
import com.kenrube.mosaic.presentation.features.photo_list.StoragePermissionDialogFragment.Companion.BUNDLE_KEY
import com.kenrube.mosaic.presentation.features.photo_list.StoragePermissionDialogFragment.Companion.REQUEST_KEY
import com.kenrube.mosaic.presentation.permissions.PermissionStatus
import com.kenrube.mosaic.utils.dpToPx
import com.kenrube.mosaic.utils.openAppSystemSettings
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
        binding.recyclerView.apply {
            adapter = PhotoListAdapter()
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = getChildAdapterPosition(view)
                    val defaultMargin = context.dpToPx(4)
                    val left = if (position % 3 == 0) 0 else defaultMargin
                    val right = if ((position + 1) % 3 == 0) 0 else defaultMargin
                    outRect.set(left, defaultMargin, right, defaultMargin)
                }
            })
        }
    }

    private fun observeViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                binding.progress.isVisible = it.loading
                binding.photosAccessWarning.isVisible = it.showingPermissionWarning
                binding.recyclerView.apply {
                    isVisible = it.photos.isNotEmpty()
                    (adapter as PhotoListAdapter).submitList(it.photos)
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
}
