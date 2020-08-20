package com.example.newswavtest.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.GistListEntity
import com.example.newswavtest.db.entity.OfflineSyncEntity

@Dao
interface OfflineSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(offlineSyncEntity: OfflineSyncEntity)

    @Query("SELECT id FROM offline_sync")
    fun getOfflineSyncListLD(): LiveData<List<Int>>

    @Query("SELECT gistId FROM offline_sync where NOT inSyncQueue GROUP BY gistId")
    suspend fun getGroupedGistIdList(): List<String>

    @Query("SELECT * FROM offline_sync where NOT inSyncQueue AND gistId = :gistId ORDER BY id")
    suspend fun getListForSyncing(gistId: String): List<OfflineSyncEntity>

    @Query("UPDATE offline_sync SET inSyncQueue = :inSyncQueue WHERE id IN (:offlineSyncId)")
    fun updateInSyncQueueBulk(offlineSyncId:List<Int>, inSyncQueue: Boolean)

    @Query("UPDATE offline_sync SET inSyncQueue = :inSyncQueue")
    fun updateInSyncQueueAll(inSyncQueue: Boolean)

    @Query("DELETE FROM offline_sync WHERE id = :offlineSyncId AND inSyncQueue = 1")
    fun delete(offlineSyncId:Int)

}
