package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.data.local.ColorPaletteLocalDataSource
import com.paintcolor.drawing.paint.data.local.RecentColorLocalDataSource
import com.paintcolor.drawing.paint.data.remote.TemplateRemoteDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single { TemplateRemoteDataSource(get()) }
    single { ColorPaletteLocalDataSource(get()) }
    single { RecentColorLocalDataSource(get()) }
}

