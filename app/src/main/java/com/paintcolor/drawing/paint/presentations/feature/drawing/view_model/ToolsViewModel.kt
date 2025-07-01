package com.paintcolor.drawing.paint.presentations.feature.drawing.view_model

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.domain.model.ToolsDrawing
import com.paintcolor.drawing.paint.presentations.feature.drawing.ui_state.ToolsUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class ToolsViewModel(
    private val ioDispatcher: CoroutineDispatcher
): BaseMvvmViewModel<ToolsUiState>() {
    override fun initState(): ToolsUiState = ToolsUiState()

    fun initTools() {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val listTools = mutableListOf<ToolsDrawing>().apply {
                add(ToolsDrawing(nameResource = R.string.flip, iconResource = R.drawable.ic_flip, isViewedReward = true))
                add(ToolsDrawing(nameResource = R.string.lock, iconResource = R.drawable.ic_lock))
                add(ToolsDrawing(nameResource = R.string.record, iconResource = R.drawable.ic_record))
                add(ToolsDrawing(nameResource = R.string.opacity, iconResource = R.drawable.ic_opacity))
                add(ToolsDrawing(nameResource = R.string.flash, iconResource = R.drawable.ic_flash))
            }
            dispatchStateUi(ToolsUiState(listTools = listTools))
        }
    }

    fun selectTools(toolsDrawing: ToolsDrawing) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val updatedListTools = currentState.listTools.map {
                when (toolsDrawing.nameResource) {
                    R.string.record -> {
                        when (it.nameResource) {
                            R.string.record -> it.copy(isSelected = !toolsDrawing.isSelected)
                            R.string.opacity -> it.copy(isSelected = false)
                            else -> it
                        }
                    }
                    R.string.opacity -> {
                        when (it.nameResource) {
                            R.string.opacity -> it.copy(isSelected = !toolsDrawing.isSelected)
                            R.string.record -> it.copy(isSelected = false)
                            else -> it
                        }
                    }
                    else -> {
                        if (it.iconResource == toolsDrawing.iconResource) {
                            it.copy(isSelected = !toolsDrawing.isSelected)
                        } else it
                    }
                }
            }
            updateTool(toolsDrawing, updatedListTools)
        }
    }

    private fun updateTool(toolsDrawing: ToolsDrawing, listTools: List<ToolsDrawing>) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val updatedListTools = listTools.map {
                if (it.nameResource == toolsDrawing.nameResource) {
                    it.copy(isViewedReward = toolsDrawing.isViewedReward)
                } else it
            }
            dispatchStateUi(currentState.copy(listTools = updatedListTools))
        }
    }

    fun updateCountWatchedVideoReward(count: Int) {
        dispatchStateUi(currentState.copy(countWatchedVideoReward = count))
    }

    fun turnOffAllRewardAds() {
        viewModelScope.launch(exceptionHandler) {
            val updatedListTools = currentState.listTools.map {
                it.copy(
                    isViewedReward = true,
                )
            }
            dispatchStateUi(currentState.copy(listTools = updatedListTools))
        }
    }
}