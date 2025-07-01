package com.paintcolor.drawing.paint.presentations.feature.my_creation

import com.paintcolor.drawing.paint.domain.model.ColoringMyCreation
import com.paintcolor.drawing.paint.domain.model.SketchMyCreation

data class MyCreationUiState(
    val listColoring: List<ColoringMyCreation> = emptyList(),
    val listSketch: List<SketchMyCreation> = emptyList(),
    val fragmentSelected: String = COLORING,
    val countWatchedVideoReward: Int = 0
){
    companion object {
        const val COLORING: String = "COLORING"
        const val SKETCH: String = "SKETCH"
    }
}