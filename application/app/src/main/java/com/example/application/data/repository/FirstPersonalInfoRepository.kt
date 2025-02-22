package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.response.FirstPersonalInfoResponse
import com.example.application.data.service.FirstPersonalInfoService

class FirstPersonalInfoRepository(
    private val firstPersonalInfoService: FirstPersonalInfoService,
) {
    suspend fun getFirstInfo(): FirstPersonalInfoResponse? {
        return try {
            val response = firstPersonalInfoService.getFirstPersonalInfoUser()
            Log.d("FirstPersonalInfoRepo", "Response Code: ${response.code()}")

            if (response.isSuccessful) {
                val firstItem = response.body()?.firstOrNull()
                Log.d("FirstPersonalInfoRepo", "Retrieved item: $firstItem")
                firstItem
            } else {
                Log.e("FirstPersonalInfoRepo", "API call failed with code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FirstPersonalInfoRepo", "Exception during API call", e)
            null
        }
    }
}