package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.BuyResponse
import com.example.application.data.model.response.ProfilePointResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ProfilePointService {
    @GET(Config.Point_ENDPOINT)
    suspend fun getTotalPoints(): Response<ProfilePointResponse>

    @POST(Config.PointBuy_ENDPOINT)
    suspend fun buyItem(@Path("id") Id : Int) : Response<BuyResponse>
}