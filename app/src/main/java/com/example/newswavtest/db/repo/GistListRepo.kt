package com.example.newswavtest.db.repo

import com.example.newswavtest.db.dao.GistListDao
import com.example.newswavtest.db.entity.GistListEntity

class GistListRepo private constructor(private val gistListDao: GistListDao) {

    suspend fun insertGistsList(gistList: List<GistListEntity>?) {
        if (gistList?.isNotEmpty() == true) gistListDao.insertAll(gistList)
    }

    fun getGistListLD() = gistListDao.getGistListLD()

    suspend fun deleteGistById(gistId: String) = gistListDao.deleteById(gistId)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: GistListRepo? = null

        fun getInstance(gistListDao: GistListDao) =
                instance ?: synchronized(this) {
                    instance ?: GistListRepo(gistListDao).also { instance = it }
                }
    }
}