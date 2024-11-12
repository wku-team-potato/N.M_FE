package com.example.application.ui.auth.functions.service

import com.example.application.Config
import com.example.application.ui.auth.functions.data.PersonalInfoRequest
import com.example.application.ui.auth.functions.data.PersonalInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface PersonalInfoService {
    @PUT(Config.PersonalInfoCreate_ENDPOINT)
    suspend fun personalInfoUser(
        @Body PersonalInfoRequest : PersonalInfoRequest
    ) : Response<PersonalInfoResponse>
}