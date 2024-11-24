package com.example.application.ui.leaderboard.function.data

import com.google.gson.annotations.SerializedName

data class ConsecutiveAttendanceRank(
    val rank: Int,
    val username: String,
    @SerializedName("연속 출석") val consecutiveAttendanceDays: Int
)

data class ConsecutiveGoalsRank(
    val rank: Int,
    val username: String,
    @SerializedName("연속 목표 달성")val consecutiveGoalsAchieved: Int
)

data class TopRankingResponse(
    @SerializedName("consecutive_attendance_rank")
    val consecutive_attendance_rank: List<ConsecutiveAttendanceRank>,

    @SerializedName("consecutive_goals_rank")
    val consecutive_goals_rank: List<ConsecutiveGoalsRank>
)