package io.day.requestqueuekmp.common

import kotlin.jvm.JvmInline

@JvmInline
value class HighPriorityQueueSize(val value: Int)

@JvmInline
value class LowPriorityQueueSize(val value: Int)

data class QueueSize(
    val highPriorityQueueSize: HighPriorityQueueSize,
    val lowPriorityQueueSize: LowPriorityQueueSize
)