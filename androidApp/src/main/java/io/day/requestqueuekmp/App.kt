package io.day.requestqueuekmp

import android.app.Application
import io.day.requestqueuekmp.data.di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(commonModule))
        }
    }
}