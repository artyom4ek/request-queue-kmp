package io.day.requestqueuekmp.ui

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.common.Url
import io.day.requestqueuekmp.domain.repository.RequestQueueRepository
import io.day.requestqueuekmp.ui.common.QUEUE_PRIORITY
import io.day.requestqueuekmp.ui.common.URL
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RequestQueueWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val requestQueueRepository: RequestQueueRepository by inject()

    override suspend fun doWork(): Result {
        val url = inputData.getString(URL)
        val queuePriority = inputData.getString(QUEUE_PRIORITY)
        val priority = QueuePriority.valueOf(queuePriority ?: throw IllegalArgumentException("Invalid task type"))
        requestQueueRepository.addRequest(url as Url, priority)
        return Result.success()
    }
}