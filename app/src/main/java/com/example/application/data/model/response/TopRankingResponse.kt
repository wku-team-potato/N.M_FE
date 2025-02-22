package com.example.application.data.model.response

import com.google.gson.annotations.SerializedName

interface Rankable {
    val rank: Int
    val username: String
    val days: Int
}

data class CumulativeAttendanceRank(
    override val rank: Int,
    override val username: String,
    @SerializedName("누적 출석")
    override val days: Int
) : Rankable

data class ConsecutiveAttendanceRank(
    override val rank: Int,
    override val username: String,
    @SerializedName("연속 출석")
    override val days: Int
) : Rankable

data class CumulativeGoalsRank(
    override val rank: Int,
    override val username: String,
    @SerializedName("누적 목표 달성")
    override val days: Int
) : Rankable

data class ConsecutiveGoalsRank(
    override val rank: Int,
    override val username: String,
    @SerializedName("연속 목표 달성")
    override val days: Int
) : Rankable

data class TopRankingResponse(
    @SerializedName("cumulative_attendance_rank")
    val cumulative_attendance_rank: List<CumulativeAttendanceRank>,
    @SerializedName("consecutive_attendance_rank")
    val consecutive_attendance_rank: List<ConsecutiveAttendanceRank>,
    @SerializedName("cumulative_goals_rank")
    val cumulative_goals_rank: List<CumulativeGoalsRank>,
    @SerializedName("consecutive_goals_rank")
    val consecutive_goals_rank: List<ConsecutiveGoalsRank>,
)