package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.response.DetectionResponse
import com.example.application.data.service.DetectionService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.io.File

class DetectionRepository(private val api: DetectionService) {

    fun uploadImage(filePath: String, callback: (DetectionResponse?) -> Unit) {
        val file = File(filePath)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        Log.d("API_DEBUG", "Uploading image: ${file.name}, Size: ${file.length()} bytes")

        val call = api.uploadImage(imagePart)
        call.enqueue(object : Callback<DetectionResponse> {
            override fun onResponse(
                call: Call<DetectionResponse>,
                response: Response<DetectionResponse>
            ) {
                Log.d("API_DEBUG", "Request URL: ${call.request().url}")
                Log.d("API_DEBUG", "Response Code: ${response.code()}")
                Log.d("API_DEBUG", "Response Headers: ${response.headers()}")

                if (response.isSuccessful) {
                    Log.d("API_DEBUG", "Response Body: ${response.body()}")
                    callback(response.body())
                } else {
                    Log.e(
                        "API_DEBUG",
                        "Error Response Body: ${response.errorBody()?.string()}"
                    )
                    callback(null)
                }
            }

            override fun onFailure(call: Call<DetectionResponse>, t: Throwable) {
                Log.e("API_DEBUG", "Request URL: ${call.request().url}")
                Log.e("API_DEBUG", "Error: ${t.message}")
                callback(null)
            }
        })
    }
}