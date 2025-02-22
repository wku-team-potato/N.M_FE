package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.request.PersonalInfoRequest
import com.example.application.data.model.response.PersonalInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface PersonalInfoService {
    @PUT(Config.PersonalInfoCreate_ENDPOINT)
    suspend fun personalInfoUser(
        @Body PersonalInfoRequest : PersonalInfoRequest
    ) : Response<PersonalInfoResponse>
}