package com.paintcolor.drawing.paint.presentations.feature.color_palette.select.custom

import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentCustomColorPalettesBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.DeletePaletteDialog
import com.paintcolor.drawing.paint.presentations.components.popup_window.MoreActionColorPalettePopup
import com.paintcolor.drawing.paint.presentations.feature.color_palette.ColorPalettesAdapter
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesFragmentDirections
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesViewModel
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel

class CustomColorPalettesFragment :
    BaseFragment<FragmentCustomColorPalettesBinding>(FragmentCustomColorPalettesBinding::inflate) {
    private val viewModel by koinNavGraphViewModel<ColorPalettesViewModel>(R.id.nav_coloring)

    private lateinit var customColorPalettesAdapter: ColorPalettesAdapter
    private lateinit var colorAddAdapter: ColorAddAndRecentAdapter
    private lateinit var colorRecentAdapter: ColorAddAndRecentAdapter

    override fun initData() {
        viewModel.insertListColorAdd()
        viewModel.collectAllColorRecent()
        viewModel.collectAllColorPalettes()

        customColorPalettesAdapter = ColorPalettesAdapter(
            isShowDotMore = true,
            onSelectPalette = { colorPalette, pos ->
                viewModel.setSelectedPalette(colorPalette)
                binding.rvPalettes.scrollToPosition(pos)
            },
            onClickMoreOption = { colorPalette, view ->
                val popupWindow = MoreActionColorPalettePopup(
                    requireContext(),
                    isDisableDelete = colorPalette.isSelected,
                    onEdit = {
                        requireContext().logEvent(EventName.color_palette_edit_click)
                        safeNavigate(ColorPalettesFragmentDirections.actionColorPalettesFragmentToCustomYourPaletteFragment(colorPalette))
                    },
                    onDelete = {
                        DeletePaletteDialog(onDelete = {
                            viewModel.deleteColorPalette(colorPalette)
                        }).show(childFragmentManager,"DeletePaletteDialog")
                    }
                )
                showPopupWindow(view, popupWindow)
            })

        colorAddAdapter = ColorAddAndRecentAdapter()
        colorRecentAdapter = ColorAddAndRecentAdapter()
    }

    override fun setupView() {
        binding.rvPalettes.adapter = customColorPalettesAdapter
        binding.rvColorAdd.adapter = colorAddAdapter
        binding.rvColorRecent.adapter = colorRecentAdapter
        binding.clAdd.tap {
            requireContext().logEvent(EventName.colorpalette_custom_add_click)
            safeNavigate(ColorPalettesFragmentDirections.actionColorPalettesFragmentToCustomYourPaletteFragment(null))
        }
        binding.clRecent.tap {
            viewModel.setSelectRecentColor()
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
                launch {
                    viewModel.uiStateStore.collectLatest {
                        colorAddAdapter.submitList(it.listColorAdd)
                        colorRecentAdapter.submitList(it.listRecentColor)
                        customColorPalettesAdapter.submitList(it.listAllColorPalettes.filter { palette -> palette.isCustomPalette })

                        if (it.colorPaletteSelected.nameResource == R.string.recent) {
                            binding.mcvRecent.strokeColor = ContextCompat.getColor(requireContext(), R.color.app_color)
                        } else {
                            binding.mcvRecent.strokeColor = ContextCompat.getColor(requireContext(), R.color.white)
                        }
                    }
                }
            }
        }
    }
}