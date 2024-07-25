package io.day.requestqueuekmp.data.common

import dev.tmapps.konnection.Konnection

object NetworkStatus {
    var isNetworkAvailable: Boolean = Konnection.createInstance().isConnected()
}