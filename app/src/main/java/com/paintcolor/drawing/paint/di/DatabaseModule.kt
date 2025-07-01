package com.paintcolor.drawing.paint.di

import androidx.room.Room
import com.paintcolor.drawing.paint.data.database.AppDatabase
import com.paintcolor.drawing.paint.data.database.dao.ColorPaletteDao
import com.paintcolor.drawing.paint.data.database.dao.RecentColorDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }

    single<ColorPaletteDao> {
        get<AppDatabase>().colorPaletteDao()
    }

    single<RecentColorDao> {
        get<AppDatabase>().recentColorDao()
    }
}