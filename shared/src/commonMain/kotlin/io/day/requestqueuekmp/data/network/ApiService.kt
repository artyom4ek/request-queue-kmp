package io.day.requestqueuekmp.data.network

import io.day.requestqueuekmp.common.Url
import io.ktor.client.statement.HttpResponse

/**
 * Sends an HTTP request to the specified URL and returns the response.
 *
 * @param request The URL to which the request will be sent.
 * @return An `HttpResponse` object containing the response status, headers, and body.
 **/
interface ApiService {
    suspend fun sendHttpRequest(request: Url): HttpResponse
}