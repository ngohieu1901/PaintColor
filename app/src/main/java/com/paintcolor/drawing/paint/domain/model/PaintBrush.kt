package com.paintcolor.drawing.paint.domain.model

import com.paintcolor.drawing.paint.coloring_source.StyleTools

data class PaintBrush(
    val nameResource: Int,
    val imageResource: Int,
    val imageLargeResource: Int,
    var size: Int,
    val styleTools: StyleTools,
    val isSelected: Boolean,
    val isViewedReward: Boolean = false,
)