package com.example.application.ui.leaderboard.function.service

import com.example.application.Config
import com.example.application.ui.leaderboard.function.data.MyRankingResponse
import com.example.application.ui.leaderboard.function.data.TopRankingResponse
import retrofit2.http.GET

interface LeaderBoardService {
    @GET(Config.MyLeaderBoard_ENDPOINT)
    suspend fun getMyRanking(): MyRankingResponse

    @GET(Config.TopLeaderBoard_ENDPOINT)
    suspend fun getTopRankings(): TopRankingResponse
}