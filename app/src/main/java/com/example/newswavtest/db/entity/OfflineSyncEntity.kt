package com.example.newswavtest.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * synced operations will no longer be in this table
 * i.e. this table should be EMPTY if there is network
 **/
@Entity(
    tableName = "offline_sync"
)
data class OfflineSyncEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // offline sync operation id

    var gistId: String = "", // gist id for operation

    // name of api operation (e.g. deleteGist, putGistStar) - for identifying which endpoint to use
    var operationName: String? = null,

    // when true, means in a queue waiting to be synced to server
    var inSyncQueue: Boolean? = null
)