package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.data.repositories.ColorPaletteRepositoryImpl
import com.paintcolor.drawing.paint.data.repositories.FileRepositoryImpl
import com.paintcolor.drawing.paint.data.repositories.PaintBrushRepositoryImpl
import com.paintcolor.drawing.paint.data.repositories.RecentColorRepositoryImpl
import com.paintcolor.drawing.paint.data.repositories.SketchImageRepositoryImpl
import com.paintcolor.drawing.paint.data.repositories.TemplateRepositoryImpl
import com.paintcolor.drawing.paint.domain.repository.ColorPaletteRepository
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import com.paintcolor.drawing.paint.domain.repository.PaintBrushRepository
import com.paintcolor.drawing.paint.domain.repository.RecentColorRepository
import com.paintcolor.drawing.paint.domain.repository.SketchImageRepository
import com.paintcolor.drawing.paint.domain.repository.TemplateRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<ColorPaletteRepository> { ColorPaletteRepositoryImpl(ioDispatcher = get(named("IO")), localDataSource = get()) }
    single<TemplateRepository> { TemplateRepositoryImpl(ioDispatcher = get(named("IO")), templateRemoteDataSource = get(), filesUtils = get(), context = get()) }
    single<PaintBrushRepository> { PaintBrushRepositoryImpl(ioDispatcher = get(named("IO"))) }
    single<RecentColorRepository> { RecentColorRepositoryImpl(ioDispatcher = get(named("IO")), recentColorLocalDataSource = get()) }
    single<FileRepository> { FileRepositoryImpl(context = get(),ioDispatcher = get(named("IO"))) }
    single<SketchImageRepository> { SketchImageRepositoryImpl(context = get(), defaultDispatcher = get(named("Default"))) }
}