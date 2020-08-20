package com.example.newswavtest.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.GistListEntity

@Dao
interface GistDetailDao {
    @Query("SELECT * FROM gist_detail where id = :id")
    fun getGistDetailLD(id: String): LiveData<GistDetailEntity>

    @Query("UPDATE gist_detail SET isStarred = :isStarred WHERE id =:id")
    suspend fun updateGistIsStarred(id: String, isStarred: Boolean)

    @Query("UPDATE gist_detail SET isStarred = NOT isStarred WHERE id =:id")
    suspend fun toggleGistIsStarred(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gistDetailEntity: GistDetailEntity)

    @Query("DELETE FROM gist_detail WHERE id = :gistId")
    suspend fun deleteById(gistId: String)
}
