package com.example.application.ui.store.functions.service

import com.example.application.Config
import com.example.application.ui.store.functions.data.StoreResponse
import retrofit2.Response
import retrofit2.http.GET

interface StoreService {
    @GET(Config.Store_ENDPOINT)
    suspend fun getStoreItems(): Response<List<StoreResponse>>
}