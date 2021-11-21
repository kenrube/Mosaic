package com.kenrube.mosaic.presentation.features.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kenrube.mosaic.databinding.BottomSheetFilterIntensityBinding

class FilterIntensityBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterIntensityBinding? = null
    private val binding: BottomSheetFilterIntensityBinding get() = _binding!!

    private val args: FilterIntensityBottomSheetArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }

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
            sendIntensity(initialIntensity)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.apply.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.intensitySlider.onProgressChanged { intensity ->
            binding.intensity.text = intensity.toString()
            sendIntensity(intensity)
        }

        binding.intensitySlider.progress = initialIntensity
    }

    private fun sendIntensity(intensity: Int) {
        navController.currentBackStackEntry!!.savedStateHandle.set(INTENSITY_KEY, intensity)
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
    }
}
