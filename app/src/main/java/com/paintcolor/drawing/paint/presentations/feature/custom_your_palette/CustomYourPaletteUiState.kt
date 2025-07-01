package com.paintcolor.drawing.paint.presentations.feature.custom_your_palette

import com.paintcolor.drawing.paint.domain.model.ColorCustom

data class CustomYourPaletteUiState (
    val listColor: List<ColorCustom> = emptyList()
)