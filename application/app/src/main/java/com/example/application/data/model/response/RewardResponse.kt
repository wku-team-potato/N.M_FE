package com.example.application.data.model.response

data class RewardResponse(
    var points_changed : Int,
    var description : String,
    var created_at : String
)

data class PurchaseHistoryResponse(
    val id : Int,
    val user_id : String,
    val item_id : String,
    val created_at: String,
)
