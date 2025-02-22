package com.example.application.data.model.groups.response

data class GroupResponse(
    val id: Int,
    val name: String,
    val description: String,
    val creator: Int,
    val created_at: String,
    val updated_at: String
)