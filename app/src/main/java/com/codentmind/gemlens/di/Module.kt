package com.codentmind.gemlens.di

/**
 * @Details :AppModule
 * @Author Roshan Bhagat
 */
import com.codentmind.gemlens.data.dataSource.remote.RemoteConfigHelper
import com.codentmind.gemlens.utils.AppSession
import org.koin.dsl.module

// Main Koin Module
val module = module {
    single { AppSession() }
    single { RemoteConfigHelper() }
    includes(
        viewModelModule,
        repositoryModule,
        databaseModule,
        networkModule,
        dispatcherModule,
    )
}