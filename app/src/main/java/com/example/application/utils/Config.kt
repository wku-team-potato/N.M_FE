package com.example.application.utils

object Config {
    const val BASE_URL = "http://4.217.250.21:80/api/v1/"
    const val AI_BASE_URL = "http://123.111.25.252:80/api/v1/"

    const val SIGNIN_ENDPOINT = "auth/login/"
    const val SIGNUP_ENDPOINT = "auth/signup/"
    const val PersonalInfo_ENDPOINT = "profile/heightweightrecord/list/"
    const val PersonalInfoCreate_ENDPOINT = "profile/heightweight/update/"
    const val Store_ENDPOINT = "store/items/list/"
    const val Point_ENDPOINT = "profile/totalpoints/retrieve/"
    const val PointBuy_ENDPOINT = "store/items/{id}/buy/"
    const val Profile_ENDPOINT = "profile/heightweightrecord/list/"
    const val ProfileUpdate_ENDPOINT = "profile/usernameheightweight/update/"
    const val REWARD_ENDPOINT = "point/transaction/"
    const val PurchaseHistory_ENDPOINT = "store/purchaserecords/"
    const val MyLeaderBoard_ENDPOINT = "profile/rankings/my/"
    const val TopLeaderBoard_ENDPOINT = "profile/rankings/top3/"
    const val MealSummary_ENDPOINT = "meal/summary/{date}/"
    const val MealDetail_ENDPOINT = "meal/list/{date}"
    const val MealUpdate_ENDPOINT = "meal/update/{id}"
    const val MealDelete_ENDPOINT = "meal/delete/{id}"
    const val MealSearch_ENDPOINT = "nutrition/foods/search/name/{food_name}/"
    const val MealAdd_ENDPOINT = "meal/create/"

    const val MEALSEARCH_ID_ENDPOINT = "nutrition/foods/search/id/{food_id}/"

    const val AI_ENDPOINT = "detection/"
    const val LOGOUT = "auth/logout/"

    const val GROUP_MY_ENDPOINT = "groups/list/"

    const val PROPILE_BY_ID_ENDPOINT = "profile/userprofile/{user_id}/"

    const val GROUP_CREATE_ENDPOINT = "groups/create/"

    const val GROUP_DELETE_ENDPOINT = "groups/delete/{id}/"

    const val GROUP_LEAVE_ENDPOINT = "groups/leave/{group_id}/"

    const val GROUP_ALL_ENDPOINT = "groups/all/"

    const val GROUP_SEARCH_ENDPOINT = "groups/search/{search}/"

    const val GROUP_JOIN_ENDPOINT = "groups/join/"
}