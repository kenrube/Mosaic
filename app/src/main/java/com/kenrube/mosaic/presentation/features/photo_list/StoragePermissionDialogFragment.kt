package com.kenrube.mosaic.presentation.features.photo_list

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.kenrube.mosaic.R

class StoragePermissionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.photos_access_dialog_message)
            .setPositiveButton(R.string.photos_access_open_settings) { _, _ ->
                setFragmentResult(
                    USER_AGREED_TO_OPEN_SETTINGS_REQUEST_KEY,
                    bundleOf(USER_AGREED_TO_OPEN_SETTINGS_RESULT_KEY to true)
                )
            }
            .setNegativeButton(R.string.photos_access_close) { _, _ -> /* dismiss */ }
            .create()

    companion object {
        const val USER_AGREED_TO_OPEN_SETTINGS_REQUEST_KEY = "UserAgreedToOpenSettingsRequestKey"
        const val USER_AGREED_TO_OPEN_SETTINGS_RESULT_KEY = "UserAgreedToOpenSettingsResultKey"
    }
}