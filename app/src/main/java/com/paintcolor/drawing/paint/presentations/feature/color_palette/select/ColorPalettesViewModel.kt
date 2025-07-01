package com.paintcolor.drawing.paint.presentations.feature.color_palette.select

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.domain.model.RecentColor
import com.paintcolor.drawing.paint.domain.model.SingleColor
import com.paintcolor.drawing.paint.domain.repository.ColorPaletteRepository
import com.paintcolor.drawing.paint.domain.repository.RecentColorRepository
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

class ColorPalettesViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val colorPaletteRepository: ColorPaletteRepository,
    private val recentColorRepository: RecentColorRepository,
    private val sharePrefUtils: SharePrefUtils,
) : BaseMvvmViewModel<ColorPalettesUiState>() {
    override fun initState(): ColorPalettesUiState = ColorPalettesUiState()

    val colorPalettesUiStateFlow get() = uiStateStore.uiStateFlow

    fun insertListColorAdd() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val listColorEmpty = mutableListOf<String>().apply {
                repeat(9) {
                    add("")
                }
            }
            dispatchStateUi(currentState.copy(listColorAdd = listColorEmpty))
        }
    }

    private fun insertAllColorPalettes() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when (val result = colorPaletteRepository.getAllPlainColorPalettes()) {
                is DataResult.Success -> {
                    colorPaletteRepository.insertAllColorPalettes(result.data)
                    Timber.d("insertAllColorPalettes Success")
                }

                is DataResult.Error -> {
                    dispatchStateError(result.exception)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun collectAllColorPalettes() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            colorPaletteRepository.collectAllColorPalettesFlow().collect {
                if (it.isNotEmpty()) {
                    Timber.d("collectAllColorPalettesFlow isNotEmpty")

                    val listAllColorPalettes = if (currentState.listAllColorPalettes.isEmpty()) {
                        it.filter { colorPalette -> !colorPalette.isCustomPalette }
                            .mapIndexed { index, colorPalette ->
                                colorPalette.copy(isSelected = index == 0)
                            }
                    } else {
                        it.map { colorPalette ->
                            colorPalette.copy(isSelected = colorPalette.id == currentState.colorPaletteSelected.id)
                        }
                    }

                    val colorPaletteSelected = if (sharePrefUtils.isUseColorPaletteStart) {
                        listAllColorPalettes.first { colorPalette -> colorPalette.isSelected }
                    } else {
                        listAllColorPalettes.first { colorPalette -> colorPalette.id == sharePrefUtils.idPaletteStartSelected || colorPalette.isSelected }
                    }

                    sharePrefUtils.isUseColorPaletteStart = true

                    val listColorSelected =
                        if (colorPaletteSelected != currentState.colorPaletteSelected) {
                            colorPaletteSelected.listColor.mapIndexed { index, colorRes ->
                                SingleColor(colorCode = colorRes, isSelected = index == 0)
                            }
                        } else {
                            currentState.listColorSelected
                        }

                    dispatchStateUi(
                        currentState.copy(
                            listAllColorPalettes = listAllColorPalettes,
                            colorPaletteSelected = colorPaletteSelected,
                            listColorSelected = listColorSelected
                        )
                    )
                } else {
                    Timber.d("collectAllColorPalettesFlow isEmpty")
                    insertAllColorPalettes()
                }
            }
        }
    }

    fun collectAllColorRecent() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            recentColorRepository.collectAllRecentColor().distinctUntilChanged().collectLatest { recentList ->
                val listColorCode = buildList {
                    addAll(recentList.map { it.colorCode })
                    repeat(9 - size) { add("") }
                }

                dispatchStateUi(currentState.copy(listRecentColor = listColorCode))
            }
        }
    }

    fun selectColor(singleColor: SingleColor) {
        viewModelScope.launch(exceptionHandler) {
            val updatedListColor = currentState.listColorSelected.map {
                it.copy(isSelected = it.id == singleColor.id)
            }
            dispatchStateUi(currentState.copy(listColorSelected = updatedListColor))
        }
    }

    fun nextColorPalette() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val currentIndex = currentState.listAllColorPalettes.indexOfFirst { it.isSelected }
            val nextIndex = (currentIndex + 1) % currentState.listAllColorPalettes.size
            updateSelectedPalette(currentState.listAllColorPalettes[nextIndex])
        }
    }

    fun previousColorPalette() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val currentIndex = currentState.listAllColorPalettes.indexOfFirst { it.isSelected }
            val previousIndex = if (currentIndex - 1 >= 0) {
                currentIndex - 1
            } else {
                currentState.listAllColorPalettes.lastIndex
            }
            updateSelectedPalette(currentState.listAllColorPalettes[previousIndex])
        }
    }

    fun setSelectedPalette(colorPalette: ColorPalette?) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            updateSelectedPalette(colorPalette)
        }
    }

    fun setSelectRecentColor() {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            if (currentState.listRecentColor.any { it.isNotBlank() }) {
                updateSelectedPalette(null)
            }
        }
    }

    private fun updateSelectedPalette(colorPalette: ColorPalette?) {
        dispatchStateLoading(true)

        if (colorPalette != null) {
            val updatedListAllColorPalettes = currentState.listAllColorPalettes.map {
                it.copy(isSelected = colorPalette.id == it.id)
            }

            val listColorSelected = if (colorPalette != currentState.colorPaletteSelected) {
                colorPalette.listColor.mapIndexed { index, colorCode ->
                    SingleColor(colorCode = colorCode, isSelected = index == 0)
                }
            } else {
                currentState.listColorSelected
            }

            dispatchStateUi(
                currentState.copy(
                    listAllColorPalettes = updatedListAllColorPalettes,
                    colorPaletteSelected = updatedListAllColorPalettes.first { it.isSelected },
                    listColorSelected = listColorSelected
                )
            )
        } else { // recent
            val updatedListAllColorPalettes = currentState.listAllColorPalettes.map {
                it.copy(isSelected = false)
            }
            val listColorSelected = currentState.listRecentColor.mapIndexed { index, colorCode ->
                SingleColor(colorCode = colorCode, isSelected = index == 0)
            }
            val recentColorPalette = ColorPalette(
                nameResource = R.string.recent,
                listColor = currentState.listRecentColor,
                isSelected = true,
                isCustomPalette = false,
            )
            dispatchStateUi(
                currentState.copy(
                    listAllColorPalettes = updatedListAllColorPalettes,
                    colorPaletteSelected = recentColorPalette,
                    listColorSelected = listColorSelected.filter { color -> color.colorCode.isNotBlank() }
                )
            )
        }

        dispatchStateLoading(false)
    }

    fun deleteColorPalette(colorPalette: ColorPalette) {
        viewModelScope.launch(exceptionHandler) {
            colorPaletteRepository.deleteColorPalette(colorPalette)
        }
    }

    fun insertRecentColorToDb(recentColor: RecentColor) {
        viewModelScope.launch {
            recentColorRepository.insertRecentColor(recentColor)
        }
    }
}