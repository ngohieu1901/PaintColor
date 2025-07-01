package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.data.resources.ResourceProviderImpl
import com.paintcolor.drawing.paint.domain.resource.ResourceProvider
import org.koin.dsl.module

val resourceModule = module {
    single<ResourceProvider> { ResourceProviderImpl(get()) }
}