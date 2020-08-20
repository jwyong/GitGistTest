package com.example.newswavtest.db.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.newswavtest.api.ApiInterface
import com.example.newswavtest.api.RetrofitClient
import com.example.newswavtest.db.dao.OfflineSyncDao
import com.example.newswavtest.db.entity.OfflineSyncEntity
import com.example.newswavtest.utils.NetworkUtil
import com.example.newswavtest.utils.OffSyncOperationNames
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfflineSyncRepo private constructor(private val offlineSyncDao: OfflineSyncDao) {
    private val apiService: ApiInterface = RetrofitClient.getClient().create(ApiInterface::class.java)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: OfflineSyncRepo? = null

        fun getInstance(offlineSyncDao: OfflineSyncDao) =
            instance ?: synchronized(this) {
                instance ?: OfflineSyncRepo(offlineSyncDao).also { instance = it }
            }
    }

    /**
     * api funcs
     **/

    /**
     * room funcs
     **/
    @Synchronized
    suspend fun insertOfflineSync(offlineSyncEntity: OfflineSyncEntity) {
        offlineSyncDao.insert(offlineSyncEntity)
    }

    fun getOfflineSyncListLD(): LiveData<List<Int>> = offlineSyncDao.getOfflineSyncListLD()

    /**
     * sync funcs
     **/
    // get list of operations from offlineSync table and sync to server
    private var mutex = Mutex()
    private var checkAndSyncScope = MainScope()
    fun checkAndSyncToServer(from: String) {
        checkAndSyncScope.launch {
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    // only proceed if got internet
                    if (NetworkUtil.isInternetAvailable.value == true) {
                        // get list of grouped gist ids in offline table which are NOT syncing
                        val gistIdList = offlineSyncDao.getGroupedGistIdList()

                        // do sync work for each gistId in separated scopes
                        gistIdList.forEach { gistId ->
                            async {
                                withContext(Dispatchers.IO) {
                                    // get full list of operations to sync
                                    val operationList = offlineSyncDao.getListForSyncing(gistId)

                                    // update all items in this list to "inSyncQueue" (USE OFFLINE SYNC ID
                                    // NOT GIST ID, coz we want to make sure only items in this query are updated)
                                    val osIdList = operationList.map {
                                        it.id
                                    }
                                    offlineSyncDao.updateInSyncQueueBulk(osIdList, true)

                                    // upload to respective endpoints for each operation
                                    operationList.forEach { offlineSyncEntity ->
                                        updateToServer(offlineSyncEntity)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Synchronized
    private suspend fun updateToServer(offlineSyncEntity: OfflineSyncEntity) {
        val response = when (offlineSyncEntity.operationName) {
            OffSyncOperationNames.putGistStar.name -> apiService.putGistStar(offlineSyncEntity.gistId)
            OffSyncOperationNames.deleteGistStar.name -> apiService.deleteGistStar(offlineSyncEntity.gistId)
            OffSyncOperationNames.deleteGist.name -> apiService.deleteGist(offlineSyncEntity.gistId)
            else -> null
        }

        Log.d("JAY_LOG", "OfflineSyncRepo: gistId = ${offlineSyncEntity.gistId}, offlineId = ${offlineSyncEntity.id}")

        // remove operation from offlineSync table if synced
        if (response?.isSuccessful == true) {
            Log.d("JAY_LOG", "OfflineSyncRepo: updateToServer success, resp = $response")
            offlineSyncDao.delete(offlineSyncEntity.id)

        } else {
            Log.d("JAY_LOG", "OfflineSyncRepo: updateToServer failed, error = ${response?.errorBody()?.string()}, " +
                    "operation = ${offlineSyncEntity.operationName}")

            // abort whole operation if failed, need to start sync from beginning to preserve sequence
            offlineSyncDao.updateInSyncQueueAll(false)
            checkAndSyncScope.cancel()
            checkAndSyncScope = MainScope()
        }

    }
}