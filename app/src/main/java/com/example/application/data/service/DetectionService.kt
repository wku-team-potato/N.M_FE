package com.example.application.data.service

import com.example.application.data.model.response.DetectionResponse
import com.example.application.utils.Config
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DetectionService {
    @Multipart
    @POST(Config.AI_ENDPOINT)
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<DetectionResponse>
}