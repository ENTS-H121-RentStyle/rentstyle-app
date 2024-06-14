package com.example.rentstyle.model.remote.retrofit

import com.example.rentstyle.model.remote.response.Pref
import com.example.rentstyle.model.remote.response.PrefResponse
import com.example.rentstyle.model.remote.response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("user")
    suspend fun createNewUser (
        @Body user: User,
    ) : Response<Unit>

    @POST("pref")
    suspend fun uploadUserPreference (
        @Body pref: Pref
    ) : Response<Unit>

    @GET("pref/{userId}")
    suspend fun checkUserPreference (
        @Path("userId") userId: String
    ) : Response<Unit>
}