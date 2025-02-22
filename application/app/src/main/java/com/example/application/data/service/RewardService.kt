package com.example.application.data.service

import com.example.application.data.model.response.PurchaseHistoryResponse
import com.example.application.utils.Config
import com.example.application.data.model.response.RewardResponse
import retrofit2.Response
import retrofit2.http.GET

interface RewardService {
    @GET(Config.REWARD_ENDPOINT)
    suspend fun getRewards():List<RewardResponse>

    @GET(Config.PurchaseHistory_ENDPOINT)
    suspend fun getPurchaseHistory() : Response<List<PurchaseHistoryResponse>>
}