package com.paintcolor.drawing.paint.presentations.feature.main

import com.paintcolor.drawing.paint.presentations.components.views.BottomNavView.Companion.COLORING_FRAGMENT

data class MainUiState(
    val fragmentSelected: String = COLORING_FRAGMENT
)