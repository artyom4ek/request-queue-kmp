package io.day.requestqueuekmp.domain.repository

import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.common.Url

interface RequestQueueRepository {

    /**
     * Adds a request with the specified URL to the request queue.
     *
     * @param url The URL of the request to be added to the queue.
     * @param queuePriority The priority of the request in the queue.
     * Each API request will enter its queue or default to high priority queue if queue unknown.
     */
    fun addRequest(url: Url, queuePriority: QueuePriority = QueuePriority.HIGH)

    /**
     * Sets a callback function that will be invoked whenever the size of the request queue changes.
     *
     * @param callback A function that takes the current queue size and the priority of the request that caused the change.
     * The callback will be called with these parameters whenever the queue size changes.
     */
    fun setOnQueueSizeChangedCallback(callback: (size: Int, priority: QueuePriority) -> Unit)
}