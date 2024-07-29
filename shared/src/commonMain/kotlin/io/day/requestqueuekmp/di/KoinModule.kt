package io.day.requestqueuekmp.di

import io.day.requestqueuekmp.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Called by Android
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(dataModule)
}

// Called by iOS
fun initKoin() = initKoin {}