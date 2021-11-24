package com.kenrube.mosaic.presentation.features.photo_list

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.kenrube.mosaic.R
import com.kenrube.mosaic.utils.setNavigationResult

class StoragePermissionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.photos_access_dialog_message)
            .setPositiveButton(R.string.photos_access_open_settings) { _, _ ->
                setNavigationResult(CLOSE_STORAGE_PERMISSION_DIALOG_KEY, true)
            }
            .setNegativeButton(R.string.photos_access_close) { _, _ -> /* dismiss */ }
            .create()

    companion object {
        const val CLOSE_STORAGE_PERMISSION_DIALOG_KEY = "OnBackPressedKey"
    }
}