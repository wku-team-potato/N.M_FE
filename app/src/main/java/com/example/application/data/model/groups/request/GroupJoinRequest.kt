package com.example.application.data.model.groups.request

import com.example.application.data.model.groups.response.GroupMemberResponse


data class GroupJoinRequest(
    val is_public: Boolean,
    val group_id: Int,
    val data: GroupMemberResponse? = null,
    val error: GroupJoinErrorMessage? = null
)

data class GroupJoinErrorMessage(
    val group_id: List<String> = emptyList(),
    val detail: String? = null
)
