package com.paintcolor.drawing.paint.presentations.feature.color_palette.select

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.custom.CustomColorPalettesFragment
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.plain.PlainColorPalettesFragment

class ColorPalettesContainerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) CustomColorPalettesFragment() else PlainColorPalettesFragment()
    }
}