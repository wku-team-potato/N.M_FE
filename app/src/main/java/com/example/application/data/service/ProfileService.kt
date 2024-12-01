package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileService {
    @GET(Config.Profile_ENDPOINT)
    suspend fun getProfile() : Response<List<ProfileResponse>>

    @PATCH(Config.ProfileUpdate_ENDPOINT)
    suspend fun updateProfileInfo(@Body updatedData: ProfileResponse): Response<ProfileResponse>
}