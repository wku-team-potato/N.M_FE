package com.example.application.ui.leaderboard.function.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.ui.leaderboard.function.data.MyRankingResponse
import com.example.application.ui.leaderboard.function.data.TopRankingResponse
import com.example.application.ui.leaderboard.function.repository.LeaderBoardRepository
import kotlinx.coroutines.launch

class LeaderBoardViewModel(private val repository : LeaderBoardRepository) : ViewModel() {

    private val _myRanking = MutableLiveData<MyRankingResponse> ()
    val myRanking : LiveData<MyRankingResponse> = _myRanking

    private val _topRankings = MutableLiveData<TopRankingResponse> ()
    val topRankings : LiveData<TopRankingResponse> = _topRankings

    fun loadMyRanking() {
        viewModelScope.launch {
            try {
                val response = repository.getMyRanking()
                Log.d("LeaderBoardViewModel", "My Ranking API Response: $response")
                _myRanking.value = response
            } catch (e : Exception){
                Log.e("LeaderBoardViewModel", "My Ranking API Error", e)
            }
        }
    }

    fun loadTopRankings(){
        viewModelScope.launch {
            try {
                val response = repository.getTopRankings()
                Log.d("LeaderBoardViewModel", "Top Ranking API Response: $response")
                _topRankings.value = response
            } catch (e : Exception){
                Log.e("LeaderBoardViewModel", "Top RankingAPI Error", e)
            }
        }
    }
}