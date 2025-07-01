package com.paintcolor.drawing.paint.presentations.feature.color_palette.select

import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.domain.model.SingleColor

data class ColorPalettesUiState(
    val listColorAdd: List<String> = emptyList(),
    val listRecentColor: List<String> = emptyList(),
    val listAllColorPalettes: List<ColorPalette> = emptyList(),
    val listColorSelected: List<SingleColor> = emptyList(),
    val colorPaletteSelected: ColorPalette = ColorPalette(),
) {
    val colorSelected = listColorSelected.find { it.isSelected }?.colorCode ?: "#FFFFFF"
}