package com.paintcolor.drawing.paint.presentations.feature.paint_brush

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.PaintBrush
import com.paintcolor.drawing.paint.domain.use_cases.GetAllPaintBrushUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PaintBrushViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val getAllPaintBrushUseCase: GetAllPaintBrushUseCase
): BaseMvvmViewModel<PaintBrushUiState>() {
    override fun initState(): PaintBrushUiState = PaintBrushUiState()

    val paintBrushUiStateFlow get() = uiStateStore.uiStateFlow
    val currentPaintBrush get() = currentState.paintBrushSelected

    init {
        getAllPaintBrush()
    }

    private fun getAllPaintBrush() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            when(val result = getAllPaintBrushUseCase()) {
                is DataResult.Success -> {
                    dispatchStateUi(
                        PaintBrushUiState(
                            listAllPaintBrush = result.data,
                            listPaintBrushInColoringPicture = result.data.take(4)
                        )
                    )
                }
                is DataResult.Error -> {
                    dispatchStateError(result.exception)
                }
            }
        }.invokeOnCompletion {
            if (it != null) dispatchStateError(it)
        }
    }

    fun changeSizeBrush(size: Int) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val updatedListBrush = currentState.listAllPaintBrush.map {
                if (it.isSelected) it.copy(size = size) else it
            }
            dispatchStateUi(currentState.copy(listAllPaintBrush = updatedListBrush))
        }
    }

    fun selectPaintBrush(paintBrush: PaintBrush) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val updatedListAllPaintBrush = currentState.listAllPaintBrush.map {
                it.copy(isSelected = it.nameResource == paintBrush.nameResource)
            }

            updateStatePaintBrush(paintBrush, updatedListAllPaintBrush)
        }
    }

    private fun updateStatePaintBrush(paintBrush: PaintBrush, listAllPaintBrush: List<PaintBrush>) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val updatedListAllPaintBrush = listAllPaintBrush.map {
                if (it.nameResource == paintBrush.nameResource) {
                    it.copy(isViewedReward = true)
                } else {
                    it
                }
            }

            val selected = updatedListAllPaintBrush.first { it.isSelected }

            val others = updatedListAllPaintBrush
                .filterNot { it.isSelected }
                .take(3)

            val listPaintBrushInColoring = listOf(selected) + others
            dispatchStateUi(
                currentState.copy(
                    listAllPaintBrush = updatedListAllPaintBrush,
                    listPaintBrushInColoringPicture = listPaintBrushInColoring
                )
            )
        }
    }

    fun updateCountWatchedVideoReward(count: Int) {
        dispatchStateUi(currentState.copy(countWatchedVideoReward = count))
    }

    fun turnOffAllRewardAds() {
        viewModelScope.launch {
            val updatedListAllPaintBrush = currentState.listAllPaintBrush.map {
                it.copy(isViewedReward = true)
            }
            val updatedListPaintBrushInColoringPicture = currentState.listPaintBrushInColoringPicture.map {
                it.copy(isViewedReward = true)
            }
            dispatchStateUi(
                currentState.copy(
                    listAllPaintBrush = updatedListAllPaintBrush,
                    listPaintBrushInColoringPicture = updatedListPaintBrushInColoringPicture
                )
            )
        }
    }
}