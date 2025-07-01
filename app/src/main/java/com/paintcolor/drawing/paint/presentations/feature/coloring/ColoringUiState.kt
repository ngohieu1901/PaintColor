package com.paintcolor.drawing.paint.presentations.feature.coloring

import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.TemplateCategory

data class ColoringUiState (
    val listTemplateCategory: List<TemplateCategory> = emptyList(),
    val listAllTemplate: List<Template> = emptyList(),
    val listTemplateInCategory: List<Template> = emptyList(),
    val countWatchedVideoReward: Int = 0
)