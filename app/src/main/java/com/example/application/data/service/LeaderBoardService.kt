package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.MyRankingResponse
import com.example.application.data.model.response.TopRankingResponse
import retrofit2.http.GET

interface LeaderBoardService {
    @GET(Config.MyLeaderBoard_ENDPOINT)
    suspend fun getMyRanking(): MyRankingResponse

    @GET(Config.TopLeaderBoard_ENDPOINT)
    suspend fun getTopRankings(): TopRankingResponse
}