package com.example.newswavtest.ui.gists_list

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.newswavtest.R
import com.example.newswavtest.db.entity.GistListEntity
import com.example.newswavtest.db.repo.GistListRepo
import com.example.newswavtest.db.repo.OfflineSyncRepo
import com.example.newswavtest.utils.BaseVM
import com.example.newswavtest.utils.NetworkUtil
import kotlinx.coroutines.launch

class GistsListVMFactory(
    private val gistListRepo: GistListRepo,
    private val offlineSyncRepo: OfflineSyncRepo
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GistsListVM(gistListRepo, offlineSyncRepo) as T
    }
}

class GistsListVM internal constructor(
    private val gistListRepo: GistListRepo,
    private val offlineSyncRepo: OfflineSyncRepo
) : BaseVM() {
    // config for paged list
    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(30)
        .setPrefetchDistance(20)
        .setPageSize(10)
        .build()
    val gistListLD: LiveData<PagedList<GistListEntity>> = LivePagedListBuilder(
        gistListRepo.getGistListLD(),
        pagedListConfig
    ).build()

    // offline sync list of observing from activity
    val offlineSyncListLD = offlineSyncRepo.getOfflineSyncListLD()

    /**
     * api calls
     **/
    // update latest data from api to db
    fun getGistsListFromApi(context: Context) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            isLoadingMLD.value = false
            return
        }

        isLoadingMLD.value = true

        viewModelScope.launch {
            val response = apiService.getGistsList()

            isLoadingMLD.value = false

            if (response.isSuccessful)
                gistListRepo.insertGistsList(response.body())
            else
                Toast.makeText(context, context.getString(R.string.error_generic), Toast.LENGTH_LONG).show()
        }
    }
}