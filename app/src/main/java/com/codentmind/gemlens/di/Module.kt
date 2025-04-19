package com.codentmind.gemlens.di

/**
 * @Details :AppModule
 * @Author Roshan Bhagat
 */
import com.codentmind.gemlens.utils.AppSession
import org.koin.dsl.module

// Main Koin Module
val module = module {
    single { AppSession() }
    includes(
        viewModelModule,
        repositoryModule,
        databaseModule,
        networkModule,
        dispatcherModule,
    )
}