package com.example.newswavtest.ui.gist_detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newswavtest.R
import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.OfflineSyncEntity
import com.example.newswavtest.db.repo.GistDetailRepo
import com.example.newswavtest.db.repo.GistListRepo
import com.example.newswavtest.db.repo.OfflineSyncRepo
import com.example.newswavtest.utils.BaseVM
import com.example.newswavtest.utils.NetworkUtil
import com.example.newswavtest.utils.OffSyncOperationNames
import com.example.newswavtest.utils.SPAM_DELAY_MILLIS
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class GistsDetailVMFactory(
    private val gistListRepo: GistListRepo,
    private val gistDetailRepo: GistDetailRepo,
    private val offlineSyncRepo: OfflineSyncRepo
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GistDetailVM(gistListRepo, gistDetailRepo, offlineSyncRepo) as T
    }
}

class GistDetailVM internal constructor(
    private val gistListRepo: GistListRepo,
    private val gistDetailRepo: GistDetailRepo,
    private val offlineSyncRepo: OfflineSyncRepo
) : BaseVM() {
    lateinit var gistId: String
    var gistDetailEntityLD: LiveData<GistDetailEntity>? = null

    fun getGistDetailLD() {
        gistDetailEntityLD = gistDetailRepo.getGistDetailLD(gistId)
    }

    /**
     * api calls
     **/
    // update latest data from api to db
    private var getGistDetailJob: Job? = null
    fun getGistDetailFromApi(context: Context) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            isLoadingMLD.value = false
            return
        }

        isLoadingMLD.value = true

        // cancel any running jobs first
        getGistDetailJob?.cancel()
        getGistDetailJob = viewModelScope.launch {
            // get star value first
            val starResp = apiService.getIsGistStarred(gistId)

            // status code 204 = starred, 404 = not starred (not found)
            val isStarred = when (starResp.code()) {
                204 -> true
                404 -> false
                else -> null
            }

            if (isStarred == null) { // resp error - toast
                isLoadingMLD.value = false

                Toast.makeText(
                    context,
                    context.getString(R.string.error_generic),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                // resp successful - try get full details
                val response = apiService.getGistDetail(gistId)

                if (response.isSuccessful) {
                    isLoadingMLD.value = false

                    // insert resp + isStarred to db
                    response.body()?.isStarred = isStarred
                    gistDetailRepo.insertGistDetail(response.body())
                } else
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_generic),
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    /**
     * user interactions
     **/
    // user handler to control user spam
    private val onClickHandler = Handler()
    fun starBtnOnClick(v: View) {
        // update db for EVERY CLICK
        gistDetailEntityLD?.value?.let { gistDetailEntity ->
            viewModelScope.launch {
                // record current star value first
                val currentStar = gistDetailEntity.isStarred

                // toggle star val in db
                gistDetailRepo.toggleGistStar(gistId)

                // add operation to offline sync table
                when (currentStar) {
                    // currently starred - UNSTAR
                    true -> offlineSyncRepo.insertOfflineSync(
                        OfflineSyncEntity(
                            gistId = gistId,
                            operationName = OffSyncOperationNames.deleteGistStar.name,
                            inSyncQueue = false
                        )
                    )

                    // currently NOT starred - STAR
                    false -> offlineSyncRepo.insertOfflineSync(
                        OfflineSyncEntity(
                            gistId = gistId,
                            operationName = OffSyncOperationNames.putGistStar.name,
                            inSyncQueue = false
                        )
                    )
                }
            }

            // handle spam - we don't want to trigger network syncing process for every spammed click
            onClickHandler.removeCallbacksAndMessages(null)
            onClickHandler.postDelayed({
                // trigger operations for checking and syncing operations in offline sync table
                offlineSyncRepo.checkAndSyncToServer("starBtnOnClick")
            }, SPAM_DELAY_MILLIS)
        }
    }

    fun deleteBtnOnClick(v: View) {
        // show dialog first
        val builder: AlertDialog.Builder = AlertDialog.Builder(v.context)
        builder.apply {
            setCancelable(true)
            setTitle(R.string.dialog_delete_title)
            setMessage(R.string.dialog_delete_msg)
            setPositiveButton(R.string.yes) { dialog, which ->
                viewModelScope.launch {
                    // delete record from gistDetail
                    gistListRepo.deleteGistById(gistId)
                    gistDetailRepo.deleteGistById(gistId)

                    // add to offlineSync table
                    offlineSyncRepo.insertOfflineSync(
                        OfflineSyncEntity(
                            gistId = gistId,
                            operationName = OffSyncOperationNames.deleteGist.name,
                            inSyncQueue = false
                        )
                    )

                    // sync to server
                    offlineSyncRepo.checkAndSyncToServer("deleteBtnOnClick")

                    // close activity
                    (v.context as Activity).finish()
                }
            }
            setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
        }
        builder.create().show()
    }
}