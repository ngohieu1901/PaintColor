package com.paintcolor.drawing.paint.presentations.feature.color_palette.start

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.domain.repository.ColorPaletteRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ColorPaletteStartViewModel(
    private val colorPaletteRepository: ColorPaletteRepository
): BaseMvvmViewModel<ColorPaletteStartUiState>() {
    override fun initState(): ColorPaletteStartUiState = ColorPaletteStartUiState()

    private fun insertAllColorPalettes() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when (val result = colorPaletteRepository.getAllPlainColorPalettes()) {
                is DataResult.Success -> {
                    colorPaletteRepository.insertAllColorPalettes(result.data)
                }

                is DataResult.Error -> {
                    dispatchStateError(result.exception)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun collectColorPalettesPlain() {
        viewModelScope.launch(exceptionHandler) {
            colorPaletteRepository.collectColorPalettesPlainFlow().distinctUntilChanged().collectLatest {
                if (it.isEmpty()) {
                    insertAllColorPalettes()
                } else {
                    dispatchStateUi(currentState.copy(listColorPalette = it))
                }
            }
        }
    }

    fun setSelectedPalette(colorPalette: ColorPalette) {
        viewModelScope.launch(exceptionHandler) {
            val updatedList = currentState.listColorPalette.map {
                it.copy(isSelected = colorPalette.nameResource == it.nameResource)
            }
            dispatchStateUi(currentState.copy(listColorPalette = updatedList))
        }
    }
}