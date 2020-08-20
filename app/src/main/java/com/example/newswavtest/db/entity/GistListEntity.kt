package com.example.newswavtest.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "gist_list"
)
data class GistListEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var created_at: String? = null,
    var updated_at: String? = null,
    var description: String? = null
)