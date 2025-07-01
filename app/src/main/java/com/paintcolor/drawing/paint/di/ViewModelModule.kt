package com.paintcolor.drawing.paint.di

import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesViewModel
import com.paintcolor.drawing.paint.presentations.feature.color_palette.start.ColorPaletteStartViewModel
import com.paintcolor.drawing.paint.presentations.feature.coloring.ColoringViewModel
import com.paintcolor.drawing.paint.presentations.feature.coloring_picture.ColoringPictureViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerViewModel
import com.paintcolor.drawing.paint.presentations.feature.custom_your_palette.CustomYourPaletteViewModel
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.ToolsViewModel
import com.paintcolor.drawing.paint.presentations.feature.interest.InterestViewModel
import com.paintcolor.drawing.paint.presentations.feature.main.MainViewModel
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationViewModel
import com.paintcolor.drawing.paint.presentations.feature.paint_brush.PaintBrushViewModel
import com.paintcolor.drawing.paint.presentations.feature.screen_base.language_start.LanguageStartViewModel
import com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall.UninstallViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.CameraViewModel
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.SketchImageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UninstallViewModel(ioDispatcher = get(named("IO"))) }
    viewModel { LanguageStartViewModel(ioDispatcher = get(named("IO"))) }
    viewModel { InterestViewModel(ioDispatcher = get(named("IO"))) }
    viewModel { ColorPaletteStartViewModel(colorPaletteRepository = get()) }
    viewModel { ContainerViewModel() }
    viewModel { MainViewModel() }
    viewModel { PaintBrushViewModel(ioDispatcher = get(named("IO")), getAllPaintBrushUseCase =  get()) }
    viewModel { ColoringViewModel(ioDispatcher = get(named("IO")), templateRepository = get()) }
    viewModel { ColoringPictureViewModel(ioDispatcher = get(named("IO")), fileRepository = get()) }
    viewModel { ColorPalettesViewModel(ioDispatcher = get(named("IO")), get(), get(), get()) }
    viewModel { CustomYourPaletteViewModel(ioDispatcher = get(named("IO")), colorPaletteRepository = get()) }
    viewModel { FilesViewModel(fileRepository = get()) }
    viewModel { ShareViewModel(fileRepository = get()) }
    viewModel { ToolsViewModel(ioDispatcher = get(named("IO"))) }
    viewModel { MyCreationViewModel(fileRepository = get()) }
    viewModel { CameraViewModel(ioDispatcher = get(named("IO"))) }
    viewModel { SketchImageViewModel(ioDispatcher = get(named("IO")), sketchImageRepository = get(), fileRepository = get()) }
}