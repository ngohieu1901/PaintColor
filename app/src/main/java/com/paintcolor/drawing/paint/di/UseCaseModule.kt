package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.domain.use_cases.GetAllPaintBrushUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllPaintBrushUseCase(paintBrushRepository = get()) }
}
