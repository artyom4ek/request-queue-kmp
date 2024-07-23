package io.day.requestqueuekmp.data.network

import io.day.requestqueuekmp.common.Url
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlin.math.pow

class ApiServiceImpl : ApiService {

    override suspend fun sendHttpRequest(request: Url): HttpResponse {
        var retryCount = 0
        while (true) {
            try {
                val response = client.request { url(request) }
                if (response.status == HttpStatusCode.OK) {
                    return response
                } else {
                    throw Exception("Request failed with status: ${response.status}")
                }
            } catch (e: Exception) {
                retryCount++

                // Attempt again with exponential backoff mechanism
                val delayTime = INITIAL_DELAY * 2.0.pow(retryCount.toDouble()).toLong()
                delay(delayTime)
            }
        }
    }

    companion object {
        private const val INITIAL_DELAY = 10_0000
    }
}
