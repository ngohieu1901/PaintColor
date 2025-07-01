package com.paintcolor.drawing.paint.application

import com.amazic.library.application.AdsApplication
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.di.dataSourceModule
import com.paintcolor.drawing.paint.di.dataStoreModule
import com.paintcolor.drawing.paint.di.databaseModule
import com.paintcolor.drawing.paint.di.dispatcherModule
import com.paintcolor.drawing.paint.di.networkModule
import com.paintcolor.drawing.paint.di.repositoryModule
import com.paintcolor.drawing.paint.di.resourceModule
import com.paintcolor.drawing.paint.di.useCaseModule
import com.paintcolor.drawing.paint.di.utilsModule
import com.paintcolor.drawing.paint.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class App : AdsApplication(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
        androidLogger()
        androidContext(this@App)
        modules(
            listOf(
                networkModule,
                databaseModule,
                dispatcherModule,
                resourceModule,
                dataStoreModule,
                utilsModule,
                dataSourceModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
            )
        )
    }

    override fun getAppTokenAdjust(): String = getString(R.string.adjust_key)

    override fun getFacebookID(): String = getString(R.string.facebook_id)
}