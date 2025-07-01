package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMviViewModel
import com.paintcolor.drawing.paint.coloring_source.Image
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ColoringPictureViewModel(
    private val fileRepository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher
): BaseMviViewModel<ColoringPictureUiState, ColoringPictureUiIntent>() {
    override fun initState(): ColoringPictureUiState = ColoringPictureUiState()

    private val _saveImageResult = MutableSharedFlow<Result<String>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val saveImageResult: SharedFlow<Result<String>> = _saveImageResult.asSharedFlow()

    override fun processIntent(intent: ColoringPictureUiIntent) {
        when (intent) {
            is ColoringPictureUiIntent.CacheCanvasState -> cacheCanvasState(
                intent.bitmap,
                intent.undoStack,
                intent.redoStack
            )
            is ColoringPictureUiIntent.MarkCanvasInitialized -> markCanvasInitialized()
            is ColoringPictureUiIntent.SaveImage -> saveImage(intent.bitmap, intent.oldFilePath)
        }
    }

    private fun saveImage(bitmap: Bitmap, oldFilePath: String ?= null) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            dispatchStateLoading(true)
            val result = fileRepository.saveImage(Constants.FolderName.COLORING_DATA, bitmap, oldFilePath)
            _saveImageResult.tryEmit(result)
            dispatchStateLoading(false)
        }
    }

    private fun cacheCanvasState(bitmap: Bitmap?, undo: List<Image>, redo: List<Image>) {
        dispatchStateUi(
            currentState.copy(
                cachedBitmap = bitmap,
                undoStack = undo,
                redoStack = redo
            )
        )
    }

    private fun markCanvasInitialized() {
        dispatchStateUi(currentState.copy(isCanvasInitialized = true))
    }
}