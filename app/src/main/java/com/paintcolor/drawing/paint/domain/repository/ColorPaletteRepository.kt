package com.paintcolor.drawing.paint.domain.repository

import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import kotlinx.coroutines.flow.Flow

interface ColorPaletteRepository {
    suspend fun getAllPlainColorPalettes(): DataResult<List<ColorPalette>>
    suspend fun insertAllColorPalettes(listColorPalettes: List<ColorPalette>)
    suspend fun insertColorPalette(colorPalettes: ColorPalette)
    suspend fun updateColorPalette(colorPalettes: ColorPalette)
    suspend fun deleteColorPalette(colorPalettes: ColorPalette)
    suspend fun collectColorPalettesPlainFlow(): Flow<List<ColorPalette>>
    suspend fun collectAllColorPalettesFlow(): Flow<List<ColorPalette>>
}