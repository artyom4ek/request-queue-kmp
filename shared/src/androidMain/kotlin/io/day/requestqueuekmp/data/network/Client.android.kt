package io.day.requestqueuekmp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual val client: HttpClient
    get() = HttpClient(OkHttp)
