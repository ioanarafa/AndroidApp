package com.example.usersapicompose.data.api

import com.example.usersapicompose.data.model.RandomUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("api/")
    suspend fun getUsers(
        @Query("results") results: Int,
        @Query("nat") nat: String,
        @Query("inc") inc: String
    ): RandomUserResponse
}
