package io.day.requestqueuekmp.di

import io.day.requestqueuekmp.domain.repository.RequestQueueRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinHelper : KoinComponent {
    private val requestQueueRepository : RequestQueueRepository by inject()
    fun getRequestQueueRepository(): RequestQueueRepository = requestQueueRepository
}