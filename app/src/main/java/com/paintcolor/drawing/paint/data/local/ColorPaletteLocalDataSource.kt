package com.paintcolor.drawing.paint.data.local

import com.paintcolor.drawing.paint.data.database.dao.ColorPaletteDao
import com.paintcolor.drawing.paint.data.database.entities.ColorPaletteEntity

class ColorPaletteLocalDataSource(private val colorPaletteDao: ColorPaletteDao) {

    suspend fun insertAllColorPalettes(listColorPalettes: List<ColorPaletteEntity>) {
        colorPaletteDao.insertAll(listColorPalettes)
    }

    suspend fun insertColorPalette(colorPaletteEntity: ColorPaletteEntity) {
        colorPaletteDao.insert(colorPaletteEntity)
    }

    suspend fun updateColorPalette(colorPaletteEntity: ColorPaletteEntity) {
        colorPaletteDao.update(colorPaletteEntity)
    }

    suspend fun deleteColorPalette(colorPaletteEntity: ColorPaletteEntity) {
        colorPaletteDao.delete(colorPaletteEntity)
    }

    fun collectColorPalettesPlainFlow() = colorPaletteDao.collectColorPalettesPlainFlow()

    fun collectAllColorPalettes() = colorPaletteDao.collectAllColorPalettesFlow()
}