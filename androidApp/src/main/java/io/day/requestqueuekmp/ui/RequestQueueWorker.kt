package io.day.requestqueuekmp.ui

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.common.Url
import io.day.requestqueuekmp.data.repository.RequestQueueRepositoryImpl
import io.day.requestqueuekmp.ui.common.QUEUE_PRIORITY
import io.day.requestqueuekmp.ui.common.URL

class RequestQueueWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(URL)
        val queuePriority = inputData.getString(QUEUE_PRIORITY)
        val priority = QueuePriority.valueOf(queuePriority ?: throw IllegalArgumentException("Invalid task type"))
        val repository = RequestQueueRepositoryImpl
        repository.addRequest(url as Url, priority)
        return Result.success()
    }
}