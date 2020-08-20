package com.example.popsicaltest.utils

import android.content.Context
import com.example.newswavtest.db.AppDatabase
import com.example.newswavtest.db.repo.GistDetailRepo
import com.example.newswavtest.db.repo.GistListRepo
import com.example.newswavtest.db.repo.OfflineSyncRepo
import com.example.newswavtest.ui.gist_detail.GistsDetailVMFactory
import com.example.newswavtest.ui.gists_list.GistsListVMFactory

object InjectorUtils {
    /**
     * gist list
     **/
    fun provideGistsListViewModelFactory(
        context: Context
    ): GistsListVMFactory {
        return GistsListVMFactory(
            getGistListDao(context),
            getOfflineSyncDao(context)
        )
    }

    private fun getGistListDao(context: Context): GistListRepo {
        return GistListRepo.getInstance(
            AppDatabase.getInstance(context.applicationContext).gistListDao()
        )
    }

    /**
     * gist detail
     **/
    fun provideGistDetailViewModelFactory(
        context: Context
    ): GistsDetailVMFactory {
        return GistsDetailVMFactory(
            getGistListDao(context),
            getGistDetailDao(context),
            getOfflineSyncDao(context)
        )
    }

    private fun getGistDetailDao(context: Context): GistDetailRepo {
        return GistDetailRepo.getInstance(
            AppDatabase.getInstance(context.applicationContext).gistDetailDao()
        )
    }

    /**
     * offline sync
     **/
    fun getOfflineSyncDao(context: Context): OfflineSyncRepo {
        return OfflineSyncRepo.getInstance(
            AppDatabase.getInstance(context.applicationContext).offlineSyncDao()
        )
    }
}