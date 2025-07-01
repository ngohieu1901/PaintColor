package com.paintcolor.drawing.paint.presentations.feature.color_palette.select.plain

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentPlainColorPalettesBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.feature.color_palette.ColorPalettesAdapter
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesViewModel
import com.paintcolor.drawing.paint.widget.logEvent
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel

class PlainColorPalettesFragment :
    BaseFragment<FragmentPlainColorPalettesBinding>(FragmentPlainColorPalettesBinding::inflate) {

    private val viewModel by koinNavGraphViewModel<ColorPalettesViewModel>(R.id.nav_coloring)
    private lateinit var colorPalettesAdapter: ColorPalettesAdapter

    override fun initData() {
        colorPalettesAdapter =
            ColorPalettesAdapter(isShowDotMore = false, onSelectPalette = { colorPalette, pos ->
                requireContext().logEvent(EventName.color_palette_color_click, bundle = Bundle().apply {
                    when (colorPalette.nameResource) {
                        R.string.basic -> {
                            putString(ParamName.color_type, "basic")
                        }
                        R.string.skin_tone -> {
                            putString(ParamName.color_type, "skin_tone")
                        }
                        R.string.rainbow -> {
                            putString(ParamName.color_type, "rainbow")
                        }
                        R.string.pastel -> {
                            putString(ParamName.color_type, "pastel")
                        }
                        R.string.cool_tone -> {
                            putString(ParamName.color_type, "cool_tone")
                        }
                        R.string.warm_tone -> {
                            putString(ParamName.color_type, "warm_tone")
                        }
                        R.string.neutral -> {
                            putString(ParamName.color_type, "neutral")
                        }
                        R.string.lip -> {
                            putString(ParamName.color_type, "lip")
                        }
                        R.string.hair -> {
                            putString(ParamName.color_type, "hair")
                        }
                    }
                })
                viewModel.setSelectedPalette(colorPalette)
                binding.rvPalettes.scrollToPosition(pos)
            })
    }

    override fun setupView() {
        binding.rvPalettes.adapter = colorPalettesAdapter
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collect {
                        colorPalettesAdapter.submitList(it.listAllColorPalettes.filter { palette -> !palette.isCustomPalette })
                    }
                }
                launch {
                    viewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
            }
        }
    }

}