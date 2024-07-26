package io.day.requestqueuekmp.data.di

import io.day.requestqueuekmp.data.network.ApiService
import io.day.requestqueuekmp.data.network.ApiServiceImpl
import io.day.requestqueuekmp.data.repository.RequestQueueRepositoryImpl
import io.day.requestqueuekmp.domain.repository.RequestQueueRepository
import org.koin.dsl.module

val commonModule = module {
    single<ApiService> { ApiServiceImpl() }
    single<RequestQueueRepository> { RequestQueueRepositoryImpl(get()) }
}