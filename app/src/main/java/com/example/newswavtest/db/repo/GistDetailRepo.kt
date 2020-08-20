package com.example.newswavtest.db.repo

import com.example.newswavtest.db.dao.GistDetailDao
import com.example.newswavtest.db.dao.GistListDao
import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.GistListEntity

class GistDetailRepo private constructor(private val gistDetailDao: GistDetailDao) {
    companion object {

        // For Singleton instantiation
        @Volatile private var instance: GistDetailRepo? = null

        fun getInstance(gistDetailDao: GistDetailDao) =
            instance ?: synchronized(this) {
                instance ?: GistDetailRepo(gistDetailDao).also { instance = it }
            }
    }

    /**
     * room funcs
     **/
    suspend fun insertGistDetail(gistDetailEntity: GistDetailEntity?) {
        gistDetailEntity?.let {
            gistDetailDao.insert(it)
        }
    }

    suspend fun toggleGistStar(gistId: String) {
            gistDetailDao.toggleGistIsStarred(gistId)
    }

    fun getGistDetailLD(id: String) = gistDetailDao.getGistDetailLD(id)

    suspend fun deleteGistById(gistId: String) = gistDetailDao.deleteById(gistId)
}