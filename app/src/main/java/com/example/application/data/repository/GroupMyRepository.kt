package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.groups.request.GroupCreateRequest
import com.example.application.data.model.groups.request.GroupJoinRequest
import com.example.application.data.model.groups.request.GroupPublicRequest
import com.example.application.data.model.groups.response.GroupCreateResponse
import com.example.application.data.model.groups.response.GroupDetailResponse
import com.example.application.data.model.groups.response.GroupJoinResponse
import com.example.application.data.model.groups.response.GroupLeaveResponse
import com.example.application.data.model.groups.response.GroupMemberResponse
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.data.service.GroupMyService
import org.json.JSONException
import org.json.JSONObject

class GroupMyRepository(private val service: GroupMyService) {
    suspend fun getMyGroups(): List<GroupResponse> {
        return service.getMyGroups()
    }

    suspend fun getAllGroups(): List<GroupResponse> {
        return service.getAllGroups()
    }

    suspend fun getSearchGroups(search: String): List<GroupResponse> {
        return service.searchGroups(search)
    }

    suspend fun leaveGroup(groupId: Int) : GroupLeaveResponse {
        return service.leaveGroup(groupId)
    }

    suspend fun createGroup(groupCreateRequest: GroupCreateRequest): GroupCreateResponse {
        return service.createGroup(groupCreateRequest)
    }

    suspend fun joinGroup(groupJoinRequest: GroupJoinRequest): GroupJoinResponse {
        return service.joinGroup(groupJoinRequest)
    }

    suspend fun groupDetail(groupId: Int): GroupDetailResponse {
        return service.groupDetail(groupId)
    }

    suspend fun getMyGroupInfo(groupId: Int): List<GroupMemberResponse> {
        return service.getMyGroupInfo(groupId)
    }

    suspend fun updateGroupPublic(groupId: Int, isPublic: GroupPublicRequest): String {
        val response = service.updateGroupPublic(groupId, isPublic)
        if (response.isSuccessful) {
            return "Group updated successfully"
        } else {
            throw Exception("그룹 업데이트 실패: ${response.code()}")
        }
    }



//    suspend fun leaveGroup(groupId: Int) : String {
//        Log.d("GroupMyRepository", "Deleting group with id: $groupId")
//        val response = service.leaveGroup(groupId)
//        Log.d("GroupMyRepository", "Response: $response")
//        if (response.isSuccessful) {
//            return "Group left successfully"
//        } else {
//            throw Exception("그룹 탈퇴 실패: ${response.code()}")
//        }
//    }
//
//    suspend fun createGroup(groupCreateRequest: GroupCreateRequest): String {
//        val response = service.createGroup(groupCreateRequest)
//
//        if (response.isSuccessful) {
//            return response.body().toString()
//        } else if (response.code() == 400) {
//            // 에러 메시지 파싱
//            val errorBody = response.errorBody()?.string()
//            val errorMessage = parseErrorMessage(errorBody)
//            throw Exception("그룹 생성 실패: $errorMessage")
//        } else {
//            throw Exception("그룹 생성 실패: ${response.code()}")
//        }
//    }
//
//    suspend fun deleteGroup(groupId: Int) : String {
//        Log.d("GroupMyRepository", "Deleting group with id: $groupId")
//        val response = service.deleteGroup(groupId)
//        Log.d("GroupMyRepository", "Response: $response")
//        if (response.isSuccessful) {
//            return "Group deleted successfully"
//        } else {
//            throw Exception("삭제 실패: ${response.code()}")
//        }
//    }
//
//    private fun parseErrorMessage(errorBody: String?): String {
//        return try {
//            val jsonObject = JSONObject(errorBody)
//            val nameErrors = jsonObject.optJSONArray("name")
//            if (nameErrors != null && nameErrors.length() > 0) {
//                nameErrors.getString(0)  // "group with this name already exists." 반환
//            } else {
//                "Unknown error occurred."
//            }
//        } catch (e: JSONException) {
//            "Error parsing error response."
//        }
//    }
}