package com.paintcolor.drawing.paint.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paintcolor.drawing.paint.data.database.converter.ColorListConverter
import com.paintcolor.drawing.paint.data.database.dao.ColorPaletteDao
import com.paintcolor.drawing.paint.data.database.dao.RecentColorDao
import com.paintcolor.drawing.paint.data.database.entities.ColorPaletteEntity
import com.paintcolor.drawing.paint.data.database.entities.RecentColorEntity

@Database(entities = [ColorPaletteEntity::class, RecentColorEntity::class],version = 3)
@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun colorPaletteDao(): ColorPaletteDao
    abstract fun recentColorDao(): RecentColorDao
}