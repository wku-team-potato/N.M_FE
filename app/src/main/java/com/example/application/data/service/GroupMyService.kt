package com.example.application.data.service

import com.example.application.data.model.groups.request.GroupCreateRequest
import com.example.application.data.model.groups.request.GroupJoinRequest
import com.example.application.data.model.groups.response.GroupCreateResponse
import com.example.application.data.model.groups.response.GroupJoinResponse
import com.example.application.data.model.groups.response.GroupLeaveResponse
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.utils.Config
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupMyService {
//    @GET(Config.GROUP_MY_ENDPOINT)
//    suspend fun getMyGroups(): List<GroupResponse>
//
//    @GET(Config.GROUP_ALL_ENDPOINT)
//    suspend fun getAllGroups(): List<GroupResponse>
//
//    @GET(Config.GROUP_SEARCH_ENDPOINT)
//    suspend fun searchGroups(@Path("search") search: String): List<GroupResponse>
//
//    @POST(Config.GROUP_CREATE_ENDPOINT)
//    suspend fun createGroup(
//        @Body GroupCreateRequest : GroupCreateRequest
//    ) : Response<Unit>
//
//    @DELETE(Config.GROUP_LEAVE_ENDPOINT)
//    suspend fun leaveGroup(@Path("group_id") groupId: Int): Response<Unit>
//
//    @DELETE(Config.GROUP_DELETE_ENDPOINT)
//    suspend fun deleteGroup(@Path("id") groupId: Int): Response<Unit>

    @GET(Config.GROUP_ALL_ENDPOINT)
    suspend fun getAllGroups(): List<GroupResponse>

    @GET(Config.GROUP_MY_ENDPOINT)
    suspend fun getMyGroups(): List<GroupResponse>

    @GET(Config.GROUP_SEARCH_ENDPOINT)
    suspend fun searchGroups(@Path("search") search: String): List<GroupResponse>

    @POST(Config.GROUP_CREATE_ENDPOINT)
    suspend fun createGroup(
        @Body groupCreateRequest : GroupCreateRequest
    ) : GroupCreateResponse

    @POST(Config.GROUP_JOIN_ENDPOINT)
    suspend fun joinGroup(
        @Body GroupJoinRequest : GroupJoinRequest
    ): GroupJoinResponse

    @DELETE(Config.GROUP_LEAVE_ENDPOINT)
    suspend fun leaveGroup(@Path("group_id") groupId: Int): GroupLeaveResponse

}