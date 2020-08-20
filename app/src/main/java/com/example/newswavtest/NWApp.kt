package com.example.newswavtest

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.newswavtest.utils.ForegroundListener
import com.example.newswavtest.utils.NetworkUtil

open class NWApp : Application() {
    override fun onCreate() {
        // setup internet observer
        NetworkUtil.setupInternetObserver(this)

        super.onCreate()
    }

    private fun initForegroundListener() {
        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(ForegroundListener())
    }
}