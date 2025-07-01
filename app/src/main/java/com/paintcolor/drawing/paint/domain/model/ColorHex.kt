package com.paintcolor.drawing.paint.domain.model

import androidx.annotation.ColorInt
import com.paintcolor.drawing.paint.widget.toColorInt

data class ColorHex(val hex: String) {
    @ColorInt
    fun toColorInt(): Int = hex.toColorInt()
}