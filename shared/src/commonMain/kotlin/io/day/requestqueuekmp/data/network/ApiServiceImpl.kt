package io.day.requestqueuekmp.data.network

import io.day.requestqueuekmp.common.Url
import io.day.requestqueuekmp.data.common.NetworkStatus.isNetworkAvailable
import io.day.requestqueuekmp.data.exception.ServerErrorException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlin.math.pow

class ApiServiceImpl : ApiService {

    override suspend fun sendHttpRequest(
        request: Url,
        networkError: (message: String) -> Unit
    ): HttpResponse {
        var retryCount = 0
        while (true) {
            try {
                val response = client.request { url(request) }
                if (response.status == HttpStatusCode.OK) {
                    return response
                } else if (response.status.value in 500..599) {
                    throw ServerErrorException("Server error: ${response.status.value}")
                } else {
                    throw Exception("Request failed with status: ${response.status.value}")
                }
            } catch (e: ServerErrorException) {
                // Notify the application if the network is available but the server is not available
                if (isNetworkAvailable) {
                    networkError.invoke(e.message ?: NETWORK_ERROR_MESSAGE)
                }
                retryAttempt(retryCount++)
            } catch (e: Exception) {
                // Notify the application if the network is available but DNS not found
                if (isNetworkAvailable) {
                    networkError.invoke(e.message ?: NETWORK_ERROR_MESSAGE)
                }
                retryAttempt(retryCount++)
            }
        }
    }

    private suspend fun retryAttempt(count: Int) {
        // Attempt again with exponential backoff mechanism
        val delayTime = INITIAL_DELAY * 2.0.pow(count.toDouble()).toLong()
        delay(delayTime)
    }

    companion object {
        private const val INITIAL_DELAY = 10_0000
        private const val NETWORK_ERROR_MESSAGE = "Network error"
    }
}
