package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.response.StoreResponse
import com.example.application.data.service.StoreService

class StoreRepository(private val storeService: StoreService) {
    suspend fun getItems(): List<StoreResponse>? {
        Log.d("StoreRepository", "Fetching items from API")
        val response = storeService.getStoreItems()
        if (response.isSuccessful) {
            Log.d("StoreRepository", "API Success: ${response.body()}")
            return response.body()
        } else {
            Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
            return null
        }
    }
}