package com.example.newswavtest.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "gist_detail"
//    foreignKeys = [ForeignKey(entity = GistListEntity::class, parentColumns = ["id"], childColumns = ["id"], onDelete = ForeignKey.CASCADE)]
)
data class GistDetailEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var url: String? = null,
    var forks_url: String? = null,
    var commits_url: String? = null,
    var node_id: String? = null,
    var git_pull_url: String? = null,
    var git_push_url: String? = null,
    var html_url: String? = null,
//    var files : Files,
    var public: Boolean? = null,
    var created_at: String? = null,
    var updated_at: String? = null,
    var description: String? = null,
    var comments: Int? = null,
    var user: String? = null,
    var comments_url: String? = null,
//    var owner : Owner,
    var truncated: Boolean? = null,
    var isStarred: Boolean? = null

)