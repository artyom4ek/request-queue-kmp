package io.day.requestqueuekmp.data.repository

import dev.tmapps.konnection.Konnection
import io.day.requestqueuekmp.common.HighPriorityQueueSize
import io.day.requestqueuekmp.common.LowPriorityQueueSize
import io.day.requestqueuekmp.common.QueueSize
import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.common.QueuePriority.HIGH
import io.day.requestqueuekmp.common.QueuePriority.LOW
import io.day.requestqueuekmp.common.Url
import io.day.requestqueuekmp.data.common.NetworkStatus.isConnectionAvailable
import io.day.requestqueuekmp.data.network.ApiService
import io.day.requestqueuekmp.domain.repository.RequestQueueRepository
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class RequestQueueRepositoryImpl(
    private val apiService: ApiService
) : RequestQueueRepository {

    private val mutex = Mutex()
    private val highPriorityQueue = mutableListOf<Url>()
    private val lowPriorityQueue = mutableListOf<Url>()
    private var processingJob: Job? = null

    private var onQueueSizeChanged: ((size: Int, priority: QueuePriority) -> Unit)? = null
    private var onNetworkError: ((message: String) -> Unit)? = null
    private var onConnectionChanged: ((status: Boolean) -> Unit)? = null

    private val scope by lazy { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    override fun setOnQueueSizeChangedCallback(callback: (size: Int, priority: QueuePriority) -> Unit) {
        onQueueSizeChanged = callback
    }

    override fun setOnNetworkErrorCallback(callback: (message: String) -> Unit) {
        onNetworkError = callback
    }

    override fun setOnConnectionChangedCallback(callback: (status: Boolean) -> Unit) {
        onConnectionChanged = callback
    }

    override fun getConnectionStatus() = isConnectionAvailable

    override fun getQueueSize(): QueueSize = QueueSize(
        HighPriorityQueueSize(highPriorityQueue.size),
        LowPriorityQueueSize(lowPriorityQueue.size)
    )

    init {
        scope.launch {
            observeConnectionState()
        }
    }

    private suspend fun observeConnectionState() {
        Konnection.createInstance().observeHasConnection().collect { isAvailable ->
            setConnectionAvailability(isAvailable)
            onConnectionChanged?.invoke(isAvailable)
        }
    }

    private fun setConnectionAvailability(isAvailable: Boolean) {
        isConnectionAvailable = isAvailable
        if (isAvailable) {
            startProcessing()
        }
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
        if (processingJob?.isActive != true && isConnectionAvailable) {
            processingJob = scope.launch {
                processQueue()
            }
        }
    }

    private suspend fun processQueue() {
        while (true) {
            if (!isConnectionAvailable) {
                delayUntilNetworkAvailable()
            }

            val requestUrl = mutex.withLock {
                // Get items from the high priority queue first
                highPriorityQueue.getOrElse(0) { lowPriorityQueue.getOrNull(0) }
            }

            requestUrl?.let { url ->
                val httpResponse = sendHttpRequestWithBackoff(url)

                delay(2000) // Delay for test

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
        return withContext(Dispatchers.IO) {
            apiService.sendHttpRequest(request) {
                onNetworkError?.invoke(it)
            }
        }
    }

    private suspend fun delayUntilNetworkAvailable() {
        while (!isConnectionAvailable) {
            delay(1000)
        }
    }
}