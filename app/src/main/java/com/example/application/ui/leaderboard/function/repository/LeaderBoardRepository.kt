package com.example.application.ui.leaderboard.function.repository

import com.example.application.ui.leaderboard.function.data.MyRankingResponse
import com.example.application.ui.leaderboard.function.data.TopRankingResponse
import com.example.application.ui.leaderboard.function.service.LeaderBoardService

class LeaderBoardRepository(private val service: LeaderBoardService) {

    suspend fun getMyRanking(): MyRankingResponse {
        return service.getMyRanking()
    }

    suspend fun getTopRankings(): TopRankingResponse {
        return service.getTopRankings()
    }
}