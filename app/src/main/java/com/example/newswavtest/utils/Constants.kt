package com.example.newswavtest.utils

/**
 * room db
 **/
const val DATABASE_NAME = "newswav-db"
const val DATABASE_VERSION = 7

/**
 * github username + token
 **/
// specify username + access token here
const val USER_NAME = "jwyong"
const val ACCESS_TOKEN = "c8d5a1dba883193caa37fab79c981884b591ebba"

/**
 * intent extra keys
 **/
const val EXTRA_GIST_ID = "EXTRA_GIST_ID"

/**
 * offline sync
 **/
enum class OffSyncOperationNames {
    putGistStar,
    deleteGistStar,
    deleteGist
}

const val SPAM_DELAY_MILLIS = 500L