package com.paintcolor.drawing.paint.presentations.feature.color_palette.start

import com.paintcolor.drawing.paint.domain.model.ColorPalette

data class ColorPaletteStartUiState(
    val listColorPalette: List<ColorPalette> = emptyList(),
)