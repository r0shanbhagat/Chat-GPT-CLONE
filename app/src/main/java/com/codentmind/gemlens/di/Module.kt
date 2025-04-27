package com.codentmind.gemlens.di

/**
 * @Details :AppModule
 * @Author Roshan Bhagat
 */
import com.codentmind.gemlens.data.dataSource.remote.RemoteConfigHelper
import com.codentmind.gemlens.utils.AppSession
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

// Main Koin Module
val module = module {
    single { RemoteConfigHelper() }
    single { AppSession(androidApplication()) }
    includes(
        viewModelModule,
        repositoryModule,
        databaseModule,
        networkModule,
        dispatcherModule,
    )
}