package com.example.application.ui.leaderboard.function.data

data class UserInfo(
    var username : String
)

data class Rankings(
    var consecutive_attendance_rank : Int,
    var consecutive_goals_rank : Int
)

data class MyRankingResponse(
   val user_info: UserInfo,
   val rankings: Rankings
)
