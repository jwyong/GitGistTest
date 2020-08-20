package com.example.newswavtest.utils

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.newswavtest.api.ApiInterface
import com.example.newswavtest.api.RetrofitClient

open class BaseVM : ViewModel() {
    val apiService: ApiInterface = RetrofitClient.getClient().create(ApiInterface::class.java)
    val isLoadingMLD = MediatorLiveData<Boolean>()
}