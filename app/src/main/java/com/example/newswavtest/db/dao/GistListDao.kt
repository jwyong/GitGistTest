package com.example.newswavtest.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newswavtest.db.entity.GistListEntity

@Dao
interface GistListDao {
    @Query("SELECT * FROM gist_list ORDER BY updated_at DESC, created_at DESC")
    fun getGistListLD(): DataSource.Factory<Int, GistListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gistList: List<GistListEntity>)

    @Query("DELETE FROM gist_list WHERE id = :gistId")
    suspend fun deleteById(gistId: String)

}
