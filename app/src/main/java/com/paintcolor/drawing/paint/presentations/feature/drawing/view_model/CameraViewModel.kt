package com.paintcolor.drawing.paint.presentations.feature.drawing.view_model

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.domain.model.CameraMode
import com.paintcolor.drawing.paint.presentations.feature.drawing.ui_state.CameraUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CameraViewModel(
    private val ioDispatcher: CoroutineDispatcher
): BaseMvvmViewModel<CameraUiState>() {
    private var recordingJob: Job? = null

    override fun initState(): CameraUiState = CameraUiState()

    fun startRecording() {
        if (currentState.isRecording) return

        dispatchStateUi(currentState.copy(isRecording = true, recordingTime = 0))
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch(ioDispatcher) {
            while (isActive) {
                delay(1000)
                val currentSeconds = currentState.recordingTime + 1
                dispatchStateUi(currentState.copy(recordingTime = currentSeconds))
            }
        }
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        dispatchStateUi(currentState.copy(isRecording = false, recordingTime = 0))
    }

    fun setCameraMode(cameraMode: CameraMode) {
        dispatchStateUi(currentState.copy(cameraMode = cameraMode))
    }

    override fun onCleared() {
        super.onCleared()
        recordingJob?.cancel()
        recordingJob = null
    }
}