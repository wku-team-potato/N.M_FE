package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.model.response.ProfileResponse_2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileService {
    @GET(Config.Profile_ENDPOINT)
    suspend fun getProfile() : Response<List<ProfileResponse>>

    @PATCH(Config.ProfileUpdate_ENDPOINT)
    suspend fun updateProfileInfo(@Body updatedData: ProfileResponse): Response<ProfileResponse>

    @GET(Config.PROPILE_BY_ID_ENDPOINT)
    suspend fun getProfileById(@Path("user_id") userId: Int): Response<ProfileResponse_2>
}