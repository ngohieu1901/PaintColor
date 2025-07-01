package com.paintcolor.drawing.paint.presentations.feature.drawing.view_model

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import com.paintcolor.drawing.paint.domain.repository.SketchImageRepository
import com.paintcolor.drawing.paint.presentations.feature.drawing.ui_state.SketchImageUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SketchImageViewModel(
    private val sketchImageRepository: SketchImageRepository,
    private val fileRepository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher
): BaseMvvmViewModel<SketchImageUiState>() {
    override fun initState(): SketchImageUiState = SketchImageUiState()

    private val _saveImageResult = MutableSharedFlow<Result<String>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val saveImageResult: SharedFlow<Result<String>> = _saveImageResult.asSharedFlow()

    fun loadOriginalBitmap(
        imagePath: String,
        removeBackground: Boolean
    ) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            dispatchStateLoading(true)
            try {
                val originalBitmap = sketchImageRepository.loadOriginalBitmap(imagePath)
                dispatchStateUi(
                    currentState.copy(
                        imageBitmap =
                        if (removeBackground)
                            sketchImageRepository.convertToSketch(originalBitmap)
                        else
                            originalBitmap
                    )
                )
            } catch (t: Throwable) {
                dispatchStateError(t)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun getOutputVideoFile() = fileRepository.getOutputFile()

    fun savePhotoCaptured(bitmap: Bitmap) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            dispatchStateLoading(true)
            val result = fileRepository.saveImage(Constants.FolderName.DRAWING_DATA, bitmap, null)
            _saveImageResult.tryEmit(result)
            dispatchStateLoading(false)
        }
    }

    fun flipHorizontal() {
        val input = currentState.imageBitmap
        if (input == null) {
            dispatchStateError(IllegalStateException("No bitmap loaded to flip"))
            return
        }

        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            dispatchStateLoading(true)
            try {
                val flipped = sketchImageRepository.flipHorizontal(input)
                dispatchStateUi(currentState.copy(imageBitmap = flipped))
            } catch (t: Throwable) {
                dispatchStateError(t)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}