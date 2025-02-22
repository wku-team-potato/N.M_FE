package com.example.application.data.model.groups.response

data class GroupCreateResponse(
    val success: Boolean,
    val message: String,
    val data: GroupResponse? = null,  // 성공 응답 시 사용
    val errors: Map<String, List<String>>? = null // 실패 응답 시 사용
)