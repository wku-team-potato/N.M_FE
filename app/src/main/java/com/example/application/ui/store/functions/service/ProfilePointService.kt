package com.example.application.ui.store.functions.service

import com.example.application.Config
import com.example.application.ui.store.functions.data.BuyRequest
import com.example.application.ui.store.functions.data.BuyResponse
import com.example.application.ui.store.functions.data.ProfilePointResponse
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