package com.example.bikeapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StravaApi {
    @POST("oauth/token")
    suspend fun exchangeToken(@Body tokenRequest: TokenRequest): Response<TokenResponse>

    @POST("oauth/token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<RefreshTokenResponse>

    @GET("athlete")
    suspend fun getAthlete(@Header("Authorization") authorization: String): Response<Athlete>

    @GET("activities/{id}")
    suspend fun getActivity(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Response<ActivityResponse>

    // "athlete/activities?before=epoch&after=epoch&page=int&per_page=int"
    @GET("athlete/activities")
    suspend fun getAthleteActivities(
        @Header("Authorization") authorization: String,
        @Query("perPage") perPage: Int?,
        @Query("page") page: Int?
//        @Query("before") before: String?,
//        @Query("after") after: String?,
    ): Response<List<ActivityResponse>>
}