package com.paintcolor.drawing.paint.presentations.feature.drawing.ui_state

import com.paintcolor.drawing.paint.domain.model.CameraMode

data class CameraUiState(
    val recordingTime : Int = 0,
    val isRecording: Boolean = false,
    val cameraMode: CameraMode = CameraMode.VIDEO
)