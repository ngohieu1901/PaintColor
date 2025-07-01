package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.utils.FilesUtils
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import org.koin.dsl.module

val utilsModule = module {
    single<SharePrefUtils> {
        SharePrefUtils(get())
    }
    single<FilesUtils> {
        FilesUtils(get())
    }
}