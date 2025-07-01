package com.paintcolor.drawing.paint.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.core.DataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        val context: Context = androidContext()
        PreferenceDataStoreFactory.create(
            produceFile = {
                File(context.filesDir, "datastore/data_prefs.preferences_pb")
            }
        )
    }
}