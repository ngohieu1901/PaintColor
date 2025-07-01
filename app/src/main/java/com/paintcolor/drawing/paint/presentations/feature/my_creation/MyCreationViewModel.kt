package com.paintcolor.drawing.paint.presentations.feature.my_creation

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.domain.model.ColoringMyCreation
import com.paintcolor.drawing.paint.domain.model.SketchMyCreation
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class MyCreationViewModel(
    private val fileRepository: FileRepository
) : BaseMvvmViewModel<MyCreationUiState>() {
    override fun initState(): MyCreationUiState = MyCreationUiState()

    fun getAllFilesColoringAndDrawing() {
        viewModelScope.launch {
            dispatchStateLoading(true)
            try {
                supervisorScope {
                    val deferredColoring = async { fileRepository.getSavedFilesColoring() }
                    val deferredDrawing = async { fileRepository.getSavedFilesDrawing() }

                    val oldColoring = currentState.listColoring
                    val oldSketch = currentState.listSketch

                    deferredColoring.await().fold(
                        onSuccess = { newList ->
                            val mergedList = newList.map { newItem ->
                                val oldItem = oldColoring.find { it.imagePath == newItem.imagePath }
                                newItem.copy(
                                    isViewedRewardShare = oldItem?.isViewedRewardShare ?: false,
                                    isViewedRewardDownload = oldItem?.isViewedRewardDownload ?: false
                                )
                            }
                            dispatchStateUi(currentState.copy(listColoring = mergedList))
                        },
                        onFailure = { dispatchStateError(it) }
                    )

                    deferredDrawing.await().fold(
                        onSuccess = { newList ->
                            val mergedList = newList.map { newItem ->
                                val oldItem = oldSketch.find { it.filePath == newItem.filePath }
                                newItem.copy(
                                    isViewedRewardShare = oldItem?.isViewedRewardShare ?: false,
                                    isViewedRewardDownload = oldItem?.isViewedRewardDownload ?: false
                                )
                            }
                            dispatchStateUi(currentState.copy(listSketch = mergedList))
                        },
                        onFailure = { dispatchStateError(it) }
                    )
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }


    fun updateStateItemColoring(coloringMyCreation: ColoringMyCreation) {
        viewModelScope.launch(exceptionHandler) {
            val updatedList = currentState.listColoring.map {
                if (it.imagePath == coloringMyCreation.imagePath) {
                    coloringMyCreation
                } else {
                    it
                }
            }
            dispatchStateUi(currentState.copy(listColoring = updatedList))
        }
    }

    fun getColoringMyCreation(imagePath: String): ColoringMyCreation? {
        return currentState.listColoring.find { it.imagePath == imagePath }
    }

    fun updateStateItemSketch(sketchMyCreation: SketchMyCreation) {
        viewModelScope.launch(exceptionHandler) {
            val updatedList = currentState.listSketch.map {
                if (it.filePath == sketchMyCreation.filePath) {
                    sketchMyCreation
                } else {
                    it
                }
            }
            dispatchStateUi(currentState.copy(listSketch = updatedList))
        }
    }

    fun setFragmentSelected(fragmentName: String) {
        dispatchStateUi(currentState.copy(fragmentSelected = fragmentName))
    }

    fun updateCountWatchedVideoReward(count: Int) {
        dispatchStateUi(currentState.copy(countWatchedVideoReward = count))
    }

    fun turnOffAllRewardAds() {
        viewModelScope.launch(exceptionHandler) {
            val updatedListColoringMyCreation = currentState.listColoring.map {
                it.copy(
                    isViewedRewardShare = true,
                    isViewedRewardDownload = true
                )
            }
            val updatedListSketchMyCreation = currentState.listSketch.map {
                it.copy(
                    isViewedRewardShare = true,
                    isViewedRewardDownload = true
                )
            }
            dispatchStateUi(currentState.copy(listColoring = updatedListColoringMyCreation, listSketch = updatedListSketchMyCreation))
        }
    }
}