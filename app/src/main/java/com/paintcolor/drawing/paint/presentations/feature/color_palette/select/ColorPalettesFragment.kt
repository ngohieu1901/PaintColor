package com.paintcolor.drawing.paint.presentations.feature.color_palette.select

import androidx.core.content.ContextCompat
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentColorPalettesBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap

class ColorPalettesFragment: BaseFragment<FragmentColorPalettesBinding>(FragmentColorPalettesBinding::inflate) {

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.color_palettes_view)
    }

    override fun setupView() {

        binding.apply {
            vpContainer.adapter = ColorPalettesContainerAdapter(requireActivity())
            vpContainer.isUserInputEnabled = false
            ivBack.tap {
                popBackStack()
            }
            tvCustom.tap {
                if (vpContainer.currentItem != 0) vpContainer.currentItem = 0
                tvCustom.setBackgroundResource(R.drawable.bg_item_bottom_color_palettes)
                tvCustom.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvPlain.setBackgroundResource(R.drawable.bg_item_transparent)
                tvPlain.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_717171))
            }
            tvPlain.tap {
                if (vpContainer.currentItem == 0) vpContainer.currentItem = 1
                tvPlain.setBackgroundResource(R.drawable.bg_item_bottom_color_palettes)
                tvPlain.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvCustom.setBackgroundResource(R.drawable.bg_item_transparent)
                tvCustom.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_717171))
            }
        }
    }

    override fun dataCollect() {

    }
}