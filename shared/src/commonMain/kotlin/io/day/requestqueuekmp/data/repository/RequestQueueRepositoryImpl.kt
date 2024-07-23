package io.day.requestqueuekmp.data.repository

import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.common.QueuePriority.HIGH
import io.day.requestqueuekmp.common.QueuePriority.LOW
import io.day.requestqueuekmp.common.Url
import io.day.requestqueuekmp.data.network.ApiServiceImpl
import io.day.requestqueuekmp.domain.repository.RequestQueueRepository
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RequestQueueRepositoryImpl : RequestQueueRepository {

    private val mutex = Mutex()
    private val highPriorityQueue = mutableListOf<Url>()
    private val lowPriorityQueue = mutableListOf<Url>()
    private var processingJob: Job? = null

    private val scope by lazy { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    private var onQueueSizeChanged: ((size: Int, priority: QueuePriority) -> Unit)? = null

    override fun setOnQueueSizeChangedCallback(callback: (size: Int, priority: QueuePriority) -> Unit) {
        onQueueSizeChanged = callback
    }

    private fun invokeQueueSizeChanged(isHighPriorityQueue: Boolean) {
        val (queueSize, priority) = if (isHighPriorityQueue) {
            highPriorityQueue.size to HIGH
        } else {
            lowPriorityQueue.size to LOW
        }
        onQueueSizeChanged?.invoke(queueSize, priority)
    }

    override fun addRequest(url: Url, queuePriority: QueuePriority) {
        when (queuePriority) {
            HIGH -> {
                highPriorityQueue.add(url)
                invokeQueueSizeChanged(true)
            }

            LOW -> {
                lowPriorityQueue.add(url)
                invokeQueueSizeChanged(false)
            }
        }
        startProcessing()
    }

    private fun startProcessing() {
        if (processingJob?.isActive != true) {
            processingJob = scope.launch {
                processQueue()
            }
        }
    }

    private suspend fun processQueue() {
        while (true) {
            val requestUrl = mutex.withLock {
                // Get items from the high priority queue first
                highPriorityQueue.getOrElse(0) { lowPriorityQueue.getOrNull(0) }
            }

            requestUrl?.let { url ->
                val httpResponse = sendHttpRequestWithBackoff(url)
                if (httpResponse.status == HttpStatusCode.OK) {
                    val (queueToModify, priority) = if (highPriorityQueue.isNotEmpty()) {
                        highPriorityQueue to true
                    } else {
                        lowPriorityQueue to false
                    }

                    // Remove a successfully completed request from the queue and invoke changes
                    queueToModify.takeIf { it.isNotEmpty() }?.removeAt(0)

                    invokeQueueSizeChanged(priority)
                }
            } ?: break
        }
    }

    private suspend fun sendHttpRequestWithBackoff(request: Url): HttpResponse {
        return ApiServiceImpl().sendHttpRequest(request)
    }
}