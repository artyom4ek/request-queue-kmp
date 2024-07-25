package io.day.requestqueuekmp.data.common

import dev.tmapps.konnection.Konnection

object NetworkStatus {
    var isConnectionAvailable: Boolean = Konnection.createInstance().isConnected()
}