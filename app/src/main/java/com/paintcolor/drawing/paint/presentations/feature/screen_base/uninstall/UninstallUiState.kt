package com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall

import com.paintcolor.drawing.paint.domain.model.AnswerModel

data class UninstallUiState (
    var listAnswer: List<AnswerModel> = emptyList(),
)