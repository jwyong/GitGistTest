package com.example.newswavtest.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.popsicaltest.utils.InjectorUtils
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object NetworkUtil {
    // checks if network is available (for retrofit)
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    // setup internet observer single instance
    var internetObserver: Disposable? = null
    var isInternetAvailable = MutableLiveData(false)

    @Synchronized
    fun setupInternetObserver(context: Context) {
        val offlineSyncRepo = InjectorUtils.getOfflineSyncDao(context)

        Log.d("JAY_LOG", "NetworkUtil: setupInternetObserver ")
            internetObserver = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { internetConnected ->
                    Log.d("JAY_LOG", "NetworkUtil:  internetConnected = $internetConnected")

                    isInternetAvailable.value = internetConnected

                    if (internetConnected)
                        offlineSyncRepo.checkAndSyncToServer("setupInternetObserver")
                }
    }

}