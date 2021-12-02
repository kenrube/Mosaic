package com.kenrube.mosaic.presentation.features.photo

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kenrube.mosaic.R
import com.kenrube.mosaic.databinding.BottomSheetFilterIntensityBinding
import com.kenrube.mosaic.utils.setNavigationResult

class FilterIntensityBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterIntensityBinding? = null
    private val binding: BottomSheetFilterIntensityBinding get() = _binding!!

    private val args: FilterIntensityBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            // Recommended way to handle backpress (ComponentActivity#onBackPressedDispatcher)
            // doesn't work in dialogs (https://issuetracker.google.com/issues/149173280)
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KEYCODE_BACK && event.action == ACTION_UP) {
                    binding.close.performClick()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterIntensityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialIntensity = args.initialIntensity
        val behavior = (dialog as BottomSheetDialog).behavior

        binding.close.setOnClickListener {
            setNavigationResult(INTENSITY_KEY, initialIntensity)
            setFragmentResult(
                CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY,
                bundleOf(CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY to true)
            )
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.apply.setOnClickListener {
            setNavigationResult(INTENSITY_KEY, binding.intensitySlider.progress)
            setFragmentResult(
                CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY,
                bundleOf(CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY to true)
            )
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.intensitySlider.onProgressChanged { intensity ->
            binding.intensity.text = intensity.toString()
            setNavigationResult(INTENSITY_KEY, intensity)
        }

        binding.intensitySlider.progress = initialIntensity
        binding.intensity.text = initialIntensity.toString()
    }

    override fun onResume() {
        super.onResume()
        // Workaround to prevent repeated enter animation after resume (see also #onPause).
        // If we'll use Window#setWindowAnimations here, animation will happen
        // (thx, Window#dispatchWindowAttributesChanged)
        dialog?.window?.attributes?.windowAnimations = R.style.Animation_App_BottomSheetDialog
    }

    override fun onPause() {
        super.onPause()
        // Workaround to prevent repeated enter animation after resume (see also #onResume)
        dialog?.window?.setWindowAnimations(-1)
    }

    private fun SeekBar.onProgressChanged(action: (Int) -> Unit) {
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                action.invoke(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
    }

    companion object {
        const val INTENSITY_KEY = "IntensityKey"
        const val CLOSE_FILTER_INTENSITY_DIALOG_REQUEST_KEY = "CloseFilterIntensityDialogRequestKey"
        const val CLOSE_FILTER_INTENSITY_DIALOG_RESULT_KEY = "CloseFilterIntensityDialogResultKey"
    }
}
