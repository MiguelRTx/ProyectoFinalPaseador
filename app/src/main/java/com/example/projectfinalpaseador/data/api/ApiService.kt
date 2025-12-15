// data/api/ApiService.kt
package com.example.projectfinalpaseador.data.api

import com.example.projectfinalpaseador.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ AUTH ============
    @POST("auth/walkerlogin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("auth/walkerregister")
    suspend fun register(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("price_hour") priceHour: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<RegisterResponse>

    // ============ AVAILABILITY ============
    @POST("walkers/availability")
    suspend fun setAvailability(
        @Header("Authorization") token: String,
        @Body request: AvailabilityRequest
    ): Response<Unit>

    // ============ LOCATION ============
    @POST("walkers/location")
    suspend fun sendLocation(
        @Header("Authorization") token: String,
        @Body request: LocationRequest
    ): Response<Unit>

    // ============ WALKS ============
    @GET("walks/pending")
    suspend fun getPendingWalks(
        @Header("Authorization") token: String
    ): Response<List<Walk>>

    @GET("walks/accepted")
    suspend fun getAcceptedWalks(
        @Header("Authorization") token: String
    ): Response<List<Walk>>

    @GET("walks")
    suspend fun getWalkHistory(
        @Header("Authorization") token: String
    ): Response<List<Walk>>

    @GET("walks/{id}")
    suspend fun getWalkDetail(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<Walk>

    @POST("walks/{id}/accept")
    suspend fun acceptWalk(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<Unit>

    @POST("walks/{id}/reject")
    suspend fun rejectWalk(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<Unit>

    @POST("walks/{id}/start")
    suspend fun startWalk(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<Unit>

    @POST("walks/{id}/end")
    suspend fun endWalk(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<Unit>

    @Multipart
    @POST("walks/{id}/photo")
    suspend fun uploadWalkPhoto(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    // ============ WALKER PHOTO ============
    @Multipart
    @POST("walkers/photo")
    suspend fun uploadWalkerPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    // ============ WALK PHOTOS ============
    @GET("walks/{id}/photos")
    suspend fun getWalkPhotos(
        @Header("Authorization") token: String,
        @Path("id") walkId: Int
    ): Response<List<WalkPhoto>>

    // ============ USER INFO ============
    @GET("me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<WalkerProfile>

    // ============ REVIEWS ============
    // Nota: Este endpoint no está en el Postman, pero es necesario para mostrar reviews del paseador
    @GET("reviews")
    suspend fun getReviews(
        @Header("Authorization") token: String
    ): Response<List<Review>>

    @GET("reviews/{id}")
    suspend fun getReviewDetail(
        @Header("Authorization") token: String,
        @Path("id") reviewId: Int
    ): Response<Review>
}
