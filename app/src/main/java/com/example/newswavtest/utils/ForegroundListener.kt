package com.example.newswavtest.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ForegroundListener : LifecycleObserver {
    companion object {
        var appOnForeground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun appOnForeground() {
        appOnForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appOnBackground() {
        appOnForeground = false
    }
}