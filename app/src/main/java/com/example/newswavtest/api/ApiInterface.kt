package com.example.newswavtest.api

import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.GistListEntity
import com.example.newswavtest.utils.ACCESS_TOKEN
import com.example.newswavtest.utils.USER_NAME
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {
    @GET("/users/$USER_NAME/gists")
    suspend fun getGistsList(
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<List<GistListEntity>>

    @GET("/gists/{id}")
    suspend fun getGistDetail(
        @Path(value = "id", encoded = false) id: String?,
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<GistDetailEntity>

    @GET("/gists/{id}/star")
    suspend fun getIsGistStarred(
        @Path(value = "id", encoded = false) id: String?,
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<String>

    /**
     * offline sync required operations
     **/
    @Headers("Content-Length: 0", "Accept: application/vnd.github.v3+json")
    @PUT("/gists/{id}/star")
    suspend fun putGistStar(
        @Path(value = "id") id: String?,
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<String>

    @DELETE("/gists/{id}/star")
    suspend fun deleteGistStar(
        @Path(value = "id", encoded = false) id: String?,
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<String>

    @DELETE("/gists/{id}")
    suspend fun deleteGist(
        @Path(value = "id", encoded = false) id: String?,
        @Query("access_token") accessToken: String = ACCESS_TOKEN
    ): Response<String>
}