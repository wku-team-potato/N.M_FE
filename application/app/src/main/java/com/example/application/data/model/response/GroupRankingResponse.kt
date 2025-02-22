package com.example.application.data.model.response

data class GroupRankingResponse(
    val rank : Int,
    val group_name: String,
    val total_points: Int,
    val updated_at: String
)
