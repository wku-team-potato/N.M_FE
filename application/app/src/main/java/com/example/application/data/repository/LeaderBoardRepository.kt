package com.example.application.data.repository

import com.example.application.data.model.response.GroupRankingResponse
import com.example.application.data.model.response.MyRankingResponse
import com.example.application.data.model.response.TopRankingResponse
import com.example.application.data.service.LeaderBoardService

class LeaderBoardRepository(private val service: LeaderBoardService) {

    suspend fun getGroupRankings(): List<GroupRankingResponse> {
        return service.getGroupRankings()
    }

    suspend fun getMyRanking(): MyRankingResponse {
        return service.getMyRanking()
    }

    suspend fun getTopRankings(): TopRankingResponse {
        return service.getTopRankings()
    }
}