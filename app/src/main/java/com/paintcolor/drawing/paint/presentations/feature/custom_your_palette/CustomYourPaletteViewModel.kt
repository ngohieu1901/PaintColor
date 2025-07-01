package com.paintcolor.drawing.paint.presentations.feature.custom_your_palette

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.domain.model.ColorCustom
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.domain.repository.ColorPaletteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class CustomYourPaletteViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val colorPaletteRepository: ColorPaletteRepository
): BaseMvvmViewModel<CustomYourPaletteUiState>() {
    override fun initState(): CustomYourPaletteUiState = CustomYourPaletteUiState()

    fun insertListColor(colorPalette: ColorPalette? = null) {
        viewModelScope.launch(ioDispatcher) {
            val listColorCustom = mutableListOf<ColorCustom>()

            if (colorPalette == null) {
                listColorCustom.add(ColorCustom(isItemAdd = true))
                repeat(8) {
                    listColorCustom.add(ColorCustom())
                }
            } else {
                val colors = colorPalette.listColor.filter { it.isNotBlank() }
                colors.forEach { hexColor ->
                    listColorCustom.add(
                        ColorCustom(
                            colorCode = hexColor,
                            isItemAdd = false
                        )
                    )
                }

                if (listColorCustom.size < 9) {
                    listColorCustom.add(ColorCustom(isItemAdd = true))
                }

                while (listColorCustom.size < 9) {
                    listColorCustom.add(ColorCustom())
                }
            }

            dispatchStateUi(currentState.copy(listColor = listColorCustom))
        }
    }

    fun selectColor(colorCustom: ColorCustom) {
        viewModelScope.launch(ioDispatcher) {
            val updatedList = currentState.listColor.map {
                it.copy(isItemSelected = colorCustom.id == it.id)
            }
            dispatchStateUi(currentState.copy(listColor = updatedList))
        }
    }

    fun addColor(colorCustom: ColorCustom) {
        viewModelScope.launch(ioDispatcher) {
            val currentList = currentState.listColor

            val updatedList = currentList.map { item ->
                when {
                    item.id == colorCustom.id -> item.copy(
                        colorCode = colorCustom.colorCode,
                        isItemSelected = false,
                        isItemAdd = false
                    )
                    else -> item.copy(
                        isItemSelected = false,
                        isItemAdd = false
                    )
                }
            }.toMutableList()

            val updatedIndex = updatedList.indexOfFirst { it.id == colorCustom.id }

            if (updatedIndex != -1 && updatedIndex + 1 < updatedList.size) {
                val nextItem = updatedList[updatedIndex + 1]
                updatedList[updatedIndex + 1] = nextItem.copy(isItemAdd = true)
            }

            dispatchStateUi(currentState.copy(listColor = updatedList))
        }
    }

    fun updateColorSelected(colorCode: String) {
        viewModelScope.launch(ioDispatcher + exceptionHandler + NonCancellable) {
            val updatedList = currentState.listColor.map {
                if (it.isItemSelected) it.copy(colorCode = colorCode) else it
            }
            dispatchStateUi(currentState.copy(listColor = updatedList))
        }
    }

    fun insertColorPaletteToDb(colorPalette: ColorPalette) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            colorPaletteRepository.insertColorPalette(colorPalette)
        }
    }

    fun updateColorPalette(colorPalette: ColorPalette) {
        viewModelScope.launch(exceptionHandler) {
            colorPaletteRepository.updateColorPalette(colorPalette)
        }
    }
}