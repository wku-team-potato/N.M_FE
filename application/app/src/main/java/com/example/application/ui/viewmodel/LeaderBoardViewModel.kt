package com.example.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.ConsecutiveAttendanceRank
import com.example.application.data.model.response.ConsecutiveGoalsRank
import com.example.application.data.model.response.CumulativeAttendanceRank
import com.example.application.data.model.response.CumulativeGoalsRank
import com.example.application.data.model.response.GroupRankingResponse
import com.example.application.data.model.response.MyRankingResponse
import com.example.application.data.model.response.Rankable
import com.example.application.data.model.response.TopRankingResponse
import com.example.application.data.repository.LeaderBoardRepository
import kotlinx.coroutines.launch

class LeaderBoardViewModel(private val repository: LeaderBoardRepository) : ViewModel() {

    // My Ranking
    private val _myRanking = MutableLiveData<MyRankingResponse>()
    val myRanking: LiveData<MyRankingResponse> = _myRanking

    // Top Rankings (Full Data)
    private val _topRankings = MutableLiveData<TopRankingResponse>()
    val topRankings: LiveData<TopRankingResponse> = _topRankings

    private val _groupRankings = MutableLiveData<List<GroupRankingResponse>>()
    val groupRankings: LiveData<List<GroupRankingResponse>> = _groupRankings

    // Category-specific visible data
    private val _consecutiveAttendanceList = MutableLiveData<List<ConsecutiveAttendanceRank>>()
    val consecutiveAttendanceList: LiveData<List<ConsecutiveAttendanceRank>> = _consecutiveAttendanceList

    private val _cumulativeAttendanceList = MutableLiveData<List<CumulativeAttendanceRank>>()
    val cumulativeAttendanceList: LiveData<List<CumulativeAttendanceRank>> = _cumulativeAttendanceList

    private val _consecutiveGoalsList = MutableLiveData<List<ConsecutiveGoalsRank>>()
    val consecutiveGoalsList: LiveData<List<ConsecutiveGoalsRank>> = _consecutiveGoalsList

    private val _cumulativeGoalsList = MutableLiveData<List<CumulativeGoalsRank>>()
    val cumulativeGoalsList: LiveData<List<CumulativeGoalsRank>> = _cumulativeGoalsList

    private val _groupRankingsList = MutableLiveData<List<GroupRankingResponse>>()
    val groupRankingsList: LiveData<List<GroupRankingResponse>> = _groupRankingsList

    private val _isConsecutiveAttendanceExpanded = MutableLiveData<Boolean>(false)
    val isConsecutiveAttendanceExpanded: LiveData<Boolean> = _isConsecutiveAttendanceExpanded

    private val _isCumulativeAttendanceExpanded = MutableLiveData<Boolean>(false)
    val isCumulativeAttendanceExpanded: LiveData<Boolean> = _isCumulativeAttendanceExpanded

    private val _isConsecutiveGoalsExpanded = MutableLiveData<Boolean>(false)
    val isConsecutiveGoalsExpanded: LiveData<Boolean> = _isConsecutiveGoalsExpanded

    private val _isCumulativeGoalsExpanded = MutableLiveData<Boolean>(false)
    val isCumulativeGoalsExpanded: LiveData<Boolean> = _isCumulativeGoalsExpanded

    private val _isGroupRankingExpanded = MutableLiveData<Boolean>(false)
    val isGroupRankingExpanded: LiveData<Boolean> = _isGroupRankingExpanded

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Current display counts
    private var consecutiveAttendanceDisplayCount = 3
    private var cumulativeAttendanceDisplayCount = 3
    private var consecutiveGoalsDisplayCount = 3
    private var cumulativeGoalsDisplayCount = 3
    private var groupRankingDisplayCount = 3

    private val maxDisplayCount = 10 // Maximum items to display

    fun loadGroupRankings() {
        viewModelScope.launch {
            try {
                val response = repository.getGroupRankings()
                Log.d("LeaderBoardViewModel", "Group Ranking API Response: $response")
                _groupRankings.value = response

                val filteredGroupRanking = response.filter { it.rank > 3  && it.total_points != 0}
                _groupRankingsList.value = filteredGroupRanking.take(7)
            } catch (e: Exception) {
                Log.e("LeaderBoardViewModel", "Group Ranking API Error", e)
            }
        }
    }

    fun loadMyRanking() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val response = repository.getMyRanking()
                Log.d("LeaderBoardViewModel", "My Ranking API Response: $response")
                _myRanking.value = response
            } catch (e: Exception) {
                Log.e("LeaderBoardViewModel", "My Ranking API Error", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun loadTopRankings() {
        viewModelScope.launch {
            try {
                val response = repository.getTopRankings()
                Log.d("LeaderBoardViewModel", "Top Ranking API Response: $response")
                _topRankings.value = response

                // Initialize visible data for each category
                val filteredConsecutiveAttendance = response.consecutive_attendance_rank.filter { it.rank > 3  && it.days != 0}
                _consecutiveAttendanceList.value = filteredConsecutiveAttendance.take(7)
                val filteredCumulativeAttendance = response.cumulative_attendance_rank.filter { it.rank > 3  && it.days != 0}
                _cumulativeAttendanceList.value = filteredCumulativeAttendance.take(7)
                val filteredConsecutiveGoals = response.consecutive_goals_rank.filter { it.rank > 3  && it.days != 0}
                _consecutiveGoalsList.value = filteredConsecutiveGoals.take(7)
                val filteredCumulativeGoals = response.cumulative_goals_rank.filter { it.rank > 3  && it.days != 0}
                _cumulativeGoalsList.value = filteredCumulativeGoals.take(7)

//                _consecutiveGoalsList.value = response.consecutive_goals_rank.take(10)
//                _cumulativeGoalsList.value = response.cumulative_goals_rank.take(10)

            } catch (e: Exception) {
                Log.e("LeaderBoardViewModel", "Top Ranking API Error", e)
            }
        }
    }

    fun toggleGroupRanking() {
        _isGroupRankingExpanded.value = !_isGroupRankingExpanded.value!!

        if (_isConsecutiveAttendanceExpanded.value == true) {
            _isConsecutiveAttendanceExpanded.value = false
        }

        if (_isCumulativeAttendanceExpanded.value == true) {
            _isCumulativeAttendanceExpanded.value = false
        }

        if (_isConsecutiveGoalsExpanded.value == true) {
            _isConsecutiveGoalsExpanded.value = false
        }

        if (_isCumulativeGoalsExpanded.value == true) {
            _isCumulativeGoalsExpanded.value = false
        }
    }

    fun toggleConsecutiveAttendance() {
        _isConsecutiveAttendanceExpanded.value = !_isConsecutiveAttendanceExpanded.value!!

        if (_isCumulativeAttendanceExpanded.value == true) {
            _isCumulativeAttendanceExpanded.value = false
        }
        if (_isConsecutiveGoalsExpanded.value == true) {
            _isConsecutiveGoalsExpanded.value = false
        }
        if (_isCumulativeGoalsExpanded.value == true) {
            _isCumulativeGoalsExpanded.value = false
        }
        if (_isGroupRankingExpanded.value == true) {
            _isGroupRankingExpanded.value = false
        }
    }

    fun toggleCumulativeAttendance() {
        _isCumulativeAttendanceExpanded.value = !_isCumulativeAttendanceExpanded.value!!

        if (_isConsecutiveAttendanceExpanded.value == true) {
            _isConsecutiveAttendanceExpanded.value = false
        }
        if (_isConsecutiveGoalsExpanded.value == true) {
            _isConsecutiveGoalsExpanded.value = false
        }
        if (_isCumulativeGoalsExpanded.value == true) {
            _isCumulativeGoalsExpanded.value = false
        }
        if (_isGroupRankingExpanded.value == true) {
            _isGroupRankingExpanded.value = false
        }
    }

    fun toggleConsecutiveGoals() {
        _isConsecutiveGoalsExpanded.value = !_isConsecutiveGoalsExpanded.value!!

        if (_isConsecutiveAttendanceExpanded.value == true) {
            _isConsecutiveAttendanceExpanded.value = false
        }
        if (_isCumulativeAttendanceExpanded.value == true) {
            _isCumulativeAttendanceExpanded.value = false
        }
        if (_isCumulativeGoalsExpanded.value == true) {
            _isCumulativeGoalsExpanded.value = false
        }
        if (_isGroupRankingExpanded.value == true) {
            _isGroupRankingExpanded.value = false
        }
    }

    fun toggleCumulativeGoals() {
        _isCumulativeGoalsExpanded.value = !_isCumulativeGoalsExpanded.value!!

        if (_isConsecutiveAttendanceExpanded.value == true) {
            _isConsecutiveAttendanceExpanded.value = false
        }
        if (_isCumulativeAttendanceExpanded.value == true) {
            _isCumulativeAttendanceExpanded.value = false
        }
        if (_isConsecutiveGoalsExpanded.value == true) {
            _isConsecutiveGoalsExpanded.value = false
        }
        if (_isGroupRankingExpanded.value == true) {
            _isGroupRankingExpanded.value = false
        }
    }

    fun loadMoreConsecutiveAttendance() {
        _topRankings.value?.let { response ->
            val filteredList = response.consecutive_attendance_rank.filter { it.rank > 3 }
            val currentList = _consecutiveAttendanceList.value ?: emptyList()
            val newItems = filteredList.take(7) // 4번 이후부터 7개만 가져옴

            // 기존 데이터 뒤에 새로운 항목을 추가
            _consecutiveAttendanceList.value = currentList + newItems
        }
    }

    fun loadMoreCumulativeAttendance() {
        _topRankings.value?.let { response ->
            val filteredList = response.cumulative_attendance_rank.filter { it.rank > 3 }
            val currentList = _cumulativeAttendanceList.value ?: emptyList()
            val newItems = filteredList.take(7) // 4번 이후부터 7개만 가져옴

            // 기존 데이터 뒤에 새로운 항목을 추가
            _cumulativeAttendanceList.value = currentList + newItems
        }
    }

    fun loadMoreConsecutiveGoals() {
        _topRankings.value?.let { response ->
            val filteredList = response.consecutive_goals_rank.filter { it.rank > 3 }
            val currentList = _consecutiveGoalsList.value ?: emptyList()
            val newItems = filteredList.take(7) // 4번 이후부터 7개만 가져옴

            // 기존 데이터 뒤에 새로운 항목을 추가
            _consecutiveGoalsList.value = currentList + newItems
        }
    }

    fun loadMoreCumulativeGoals() {
        _topRankings.value?.let { response ->
            val filteredList = response.cumulative_goals_rank.filter { it.rank > 3 }
            val currentList = _cumulativeGoalsList.value ?: emptyList()
            val newItems = filteredList.take(7) // 4번 이후부터 7개만 가져옴

            // 기존 데이터 뒤에 새로운 항목을 추가
            _cumulativeGoalsList.value = currentList + newItems
        }
    }

}
