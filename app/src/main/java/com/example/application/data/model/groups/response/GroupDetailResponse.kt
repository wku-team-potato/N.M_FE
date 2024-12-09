package com.example.application.data.model.groups.response

data class GroupDetailResponse(
    val group: GroupResponseWithMembers
)

data class GroupResponseWithMembers(
    val id: Int,
    val name: String,
    val description: String,
    val creator: Int,
    val created_at: String,
    val updated_at: String,
    val members: List<GroupMemberResponse>
)

data class GroupMemberResponse(
    val id: Int,
    val joined_at: String,
    val is_admin: Boolean,
    val is_public: Boolean,
    val user_id: Int,
    val group_id: Int
)