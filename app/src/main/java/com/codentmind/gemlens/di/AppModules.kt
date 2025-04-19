package com.codentmind.gemlens.di

import com.codentmind.gemlens.data.dataSource.remote.provideApiService
import com.codentmind.gemlens.data.dataSource.remote.provideKtorClient
import com.codentmind.gemlens.data.dataSource.db.MessageDatabase
import com.codentmind.gemlens.data.repository.GeminiAIRepoImpl
import com.codentmind.gemlens.data.repository.MessageRepositoryImpl
import com.codentmind.gemlens.domain.repository.GeminiAIRepo
import com.codentmind.gemlens.domain.repository.MessageRepository
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Module for ViewModels
val viewModelModule = module {
    viewModelOf(::MessageViewModel)
}

// Module for Repositories
val repositoryModule = module {
    singleOf(::GeminiAIRepoImpl) { bind<GeminiAIRepo>() }
    singleOf(::MessageRepositoryImpl) { bind<MessageRepository>() }
}

// Module for Database
val databaseModule = module {
    single { MessageDatabase.getInstance(androidApplication()) }
}

// Module for Network
val networkModule = module {
    single { provideKtorClient() }
    single { provideApiService(get()) }
}
