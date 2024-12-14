package com.example.application.data.model.groups.response

data class GroupRankResponse(
    val rank: Int,
    val group_name: String,
    val total_points: Int,
    val updated_at: String
)