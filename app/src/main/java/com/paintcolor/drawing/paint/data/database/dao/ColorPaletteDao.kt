package com.paintcolor.drawing.paint.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paintcolor.drawing.paint.data.database.entities.ColorPaletteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPaletteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(palette: ColorPaletteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(palettes: List<ColorPaletteEntity>)

    @Update
    suspend fun update(palette: ColorPaletteEntity)

    @Delete
    suspend fun delete(palette: ColorPaletteEntity)

    @Query("SELECT * FROM ColorPaletteEntity WHERE isCustomPalette = 0")
    fun collectColorPalettesPlainFlow(): Flow<List<ColorPaletteEntity>>

    @Query("SELECT * FROM ColorPaletteEntity")
    fun collectAllColorPalettesFlow(): Flow<List<ColorPaletteEntity>>

}