package com.example.application.data.model.response

data class UserInfo(
    var username : String,
    var consecutive_attendance_days : Int,
    var cumulative_attendance_days : Int,
    var consecutive_goals_achieved : Int,
    var cumulative_goals_achieved : Int
)

data class Rankings(
    var consecutive_attendance_rank : Int,
    var cumulative_attendance_rank : Int,
    var consecutive_goals_rank : Int,
    var cumulative_goals_rank : Int
)

data class MyRankingResponse(
    val user_info: UserInfo,
    val rankings: Rankings
)
