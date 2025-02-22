package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.StoreResponse
import retrofit2.Response
import retrofit2.http.GET

interface StoreService {
    @GET(Config.Store_ENDPOINT)
    suspend fun getStoreItems(): Response<List<StoreResponse>>
}