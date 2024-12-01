package com.example.application.ui.view.main.pages

import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.data.model.response.Rankable
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.FragmentLeaderboardBinding
import com.example.application.data.repository.LeaderBoardRepository
import com.example.application.ui.view.main.ConsecutiveAttendAdapter
import com.example.application.ui.view.main.ConsecutiveGoalAdapter
import com.example.application.ui.view.main.CumulativeAttendAdapter
import com.example.application.ui.view.main.CumulativeGoalAdapter
import com.example.application.ui.viewmodel.LeaderBoardViewModel
import com.example.application.ui.viewmodel.LeaderBoardViewModelFactory

class LeaderboardFragment : BaseFragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderBoardViewModel: LeaderBoardViewModel

    private lateinit var cumulateAttendAdapter: CumulativeAttendAdapter
    private lateinit var cumulateGoalAdapter: CumulativeGoalAdapter
    private lateinit var consecutiveAttendanceAdapter: ConsecutiveAttendAdapter
    private lateinit var consecutiveGoalAdapter: ConsecutiveGoalAdapter

    private var user_name: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadRankings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        observeViewModel()
    }

    private fun initAdapter() {
        cumulateAttendAdapter = CumulativeAttendAdapter()
        cumulateGoalAdapter = CumulativeGoalAdapter()
        consecutiveAttendanceAdapter = ConsecutiveAttendAdapter()
        consecutiveGoalAdapter = ConsecutiveGoalAdapter()
    }

    private fun initRecyclerView() {
        binding.cumAttendRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cumulateAttendAdapter
        }
        binding.conAttendRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = consecutiveAttendanceAdapter
        }
        binding.cumGoalRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cumulateGoalAdapter
        }
        binding.conGoalRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = consecutiveGoalAdapter
        }
    }

    private fun initViewModel() {
        val repository = LeaderBoardRepository(RetrofitInstance.leaderBoardService)
        val factory = LeaderBoardViewModelFactory(repository)
        leaderBoardViewModel = ViewModelProvider(this, factory).get(LeaderBoardViewModel::class.java)
    }

    private fun observeViewModel() = with(binding) {
        // 마이 랭킹
        leaderBoardViewModel.myRanking.observe(viewLifecycleOwner) { ranking ->
            ranking?.let {
                user_name = it.user_info.username

                if (it.user_info.consecutive_attendance_days == 0) {
                    myRankingAttend.visibility = View.VISIBLE
                    myRankingAttendIcon.visibility = View.GONE
                    myRankingAttend.text = "기록 없음"
                } else {
                    updateMyRanking(it.rankings.consecutive_attendance_rank, myRankingAttend, myRankingAttendIcon)
                }

                if (it.user_info.cumulative_attendance_days == 0) {
                    myRankingGoals.visibility = View.VISIBLE
                    myRankingGoalIcon.visibility = View.GONE
                    myRankingGoals.text = "기록 없음"
                } else {
                    updateMyRanking(it.rankings.consecutive_goals_rank, myRankingGoals, myRankingGoalIcon)
                }
            }
        }

        // Top3 랭킹
        leaderBoardViewModel.topRankings.observe(viewLifecycleOwner) { ranking ->
            ranking?.let {
                user_name?.let { name ->
                    if (it.cumulative_goals_rank[3].days == 0){
                        cumButtonMoreGoal.visibility = View.GONE
                    } else {
                        cumButtonMoreGoal.visibility = View.VISIBLE
                    }

                    if (it.cumulative_attendance_rank[3].days == 0){
                        cumButtonMoreAttend.visibility = View.GONE
                    } else {
                        cumButtonMoreAttend.visibility = View.VISIBLE
                    }

                    if (it.consecutive_goals_rank[3].days == 0){
                        conButtonMoreGoal.visibility = View.GONE
                    } else {
                        conButtonMoreGoal.visibility = View.VISIBLE
                    }

                    if (it.consecutive_attendance_rank[3].days == 0){
                        conAttendButtonMoreGoal.visibility = View.GONE
                    } else {
                        conAttendButtonMoreGoal.visibility = View.VISIBLE
                    }

                    Log.d("LeaderBoardFragment", it.consecutive_attendance_rank.size.toString())

                    updateRanking(
                        it.cumulative_attendance_rank,
                        name,
                        cumAttendFirst,
                        cumAttendFirstName,
                        cumAttendFirstData,
                        cumAttendSecond,
                        cumAttendSecondName,
                        cumAttendSecondData,
                        cumAttendThird,
                        cumAttendThirdName,
                        cumAttendThirdData
                    )

                    updateRanking(
                        it.cumulative_goals_rank,
                        name,
                        cumGoalFirst,
                        cumGoalFirstName,
                        cumGoalFirstData,
                        cumGoalSecond,
                        cumGoalSecondName,
                        cumGoalSecondData,
                        cumGoalThird,
                        cumGoalThirdName,
                        cumGoalThirdData
                    )

                    updateRanking(
                        it.consecutive_attendance_rank,
                        name,
                        conAttendFirst,
                        conAttendFirstName,
                        conAttendFirstData,
                        conAttendSecond,
                        conAttendSecondName,
                        conAttendSecondData,
                        conAttendThird,
                        conAttendThirdName,
                        conAttendThirdData
                    )

                    updateRanking(
                        it.consecutive_goals_rank,
                        name,
                        conGoalFirst,
                        conGoalFirstName,
                        conGoalFirstData,
                        conGoalSecond,
                        conGoalSecondName,
                        conGoalSecondData,
                        conGoalThird,
                        conGoalThirdName,
                        conGoalThirdData
                    )
                }
            }
        }

        leaderBoardViewModel.consecutiveAttendanceList.observe(viewLifecycleOwner) { list ->
            consecutiveAttendanceAdapter.submitList(list)
        }

        leaderBoardViewModel.cumulativeAttendanceList.observe(viewLifecycleOwner) { list ->
            cumulateAttendAdapter.submitList(list)
        }

        leaderBoardViewModel.consecutiveGoalsList.observe(viewLifecycleOwner) { list ->
            consecutiveGoalAdapter.submitList(list)
        }

        leaderBoardViewModel.cumulativeGoalsList.observe(viewLifecycleOwner) { list ->
            cumulateGoalAdapter.submitList(list)
        }

        loadRankings()
        initAdapter()
        initRecyclerView()
        setUpLoadMoreButtons()
    }

    private fun updateRanking(
        rankingData: List<Rankable>,
        userName: String,
        first: LinearLayout,
        firstName: TextView,
        firstData: TextView,
        second: LinearLayout,
        secondName: TextView,
        secondData: TextView,
        third: LinearLayout,
        thirdName: TextView,
        thirdData: TextView
    ) {
        firstName.text = ""
        firstData.text = ""
        secondName.text = ""
        secondData.text = ""
        thirdName.text = ""
        thirdData.text = ""

        if (rankingData.size >= 1) {
            if (rankingData[0].days == 0) {
                first.visibility = View.GONE
                firstName.visibility = View.GONE
                firstData.visibility = View.GONE
            } else {
                first.visibility = View.VISIBLE
                firstName.visibility = View.VISIBLE
                firstData.visibility = View.VISIBLE
            }

            firstName.text = rankingData[0].username
            firstData.text = "${rankingData[0].days}일"

            if (rankingData[0].username == userName) {
                firstName.text = "나"
                firstName.setTextColor(Color.BLUE)
            }
        }

        if (rankingData.size >= 2) {
            if (rankingData[1].days == 0) {
                second.visibility = View.GONE
                secondName.visibility = View.GONE
                secondData.visibility = View.GONE
            } else {
                second.visibility = View.VISIBLE
                secondName.visibility = View.VISIBLE
                secondData.visibility = View.VISIBLE
            }

            secondName.text = rankingData[1].username
            secondData.text = "${rankingData[1].days}일"

            if (rankingData[1].username == userName) {
                secondName.text = "나"
                secondName.setTextColor(Color.BLUE)
            }
        }

        if (rankingData.size >= 3) {
            if (rankingData[2].days == 0) {
                third.visibility = View.GONE
                thirdName.visibility = View.GONE
                thirdData.visibility = View.GONE
            } else {
                third.visibility = View.VISIBLE
                thirdName.visibility = View.VISIBLE
                thirdData.visibility = View.VISIBLE
            }

            thirdName.text = rankingData[2].username
            thirdData.text = "${rankingData[2].days}일"

            if (rankingData[2].username == userName) {
                thirdName.text = "나"
                thirdName.setTextColor(Color.BLUE)
            }
        }
    }

    private fun updateMyRanking(
        rank: Int,
        textView: TextView,
        iconView: ImageView
//        iconView: com.airbnb.lottie.LottieAnimationView
    ) {
        if (rank in 1..3) {
            textView.visibility = View.GONE
            iconView.visibility = View.VISIBLE
            when (rank) {
                1 -> iconView.setImageResource(R.drawable.ic_first)
                2 -> iconView.setImageResource(R.drawable.ic_second)
                3 -> iconView.setImageResource(R.drawable.ic_third)
            }
        } else {
            iconView.visibility = View.GONE
            textView.visibility = View.VISIBLE
            textView.text = "${rank}위"
        }
    }

    private fun loadRankings() {
        leaderBoardViewModel.loadMyRanking()
        leaderBoardViewModel.loadTopRankings()
    }

    private fun setUpLoadMoreButtons() = with(binding) {
        leaderBoardViewModel.isConsecutiveAttendanceExpanded.observe(viewLifecycleOwner) { isExpanded ->
            conAttendButtonMoreGoal.text = if (isExpanded) "닫기" else "더보기"
            if (isExpanded) {
                conAttendRecycler.visibility = View.VISIBLE
            } else {
                conAttendRecycler.visibility = View.GONE
            }

            conAttendButtonMoreGoal.setOnClickListener() {
                leaderBoardViewModel.toggleConsecutiveAttendance()
            }

            leaderBoardViewModel.isCumulativeAttendanceExpanded.observe(viewLifecycleOwner) { isExpanded ->
                cumButtonMoreAttend.text = if (isExpanded) "닫기" else "더보기"
                if (isExpanded) {
                    cumAttendRecycler.visibility = View.VISIBLE
                } else {
                    cumAttendRecycler.visibility = View.GONE
                }
            }

            cumButtonMoreAttend.setOnClickListener {
                leaderBoardViewModel.toggleCumulativeAttendance()
            }

            leaderBoardViewModel.isConsecutiveGoalsExpanded.observe(viewLifecycleOwner) { isExpanded ->
                conButtonMoreGoal.text = if (isExpanded) "닫기" else "더보기"
                if (isExpanded) {
                    conGoalRecycler.visibility = View.VISIBLE
                } else {
                    conGoalRecycler.visibility = View.GONE
                }
            }

            conButtonMoreGoal.setOnClickListener {
                leaderBoardViewModel.toggleConsecutiveGoals()
            }

            leaderBoardViewModel.isCumulativeGoalsExpanded.observe(viewLifecycleOwner) { isExpanded ->
                cumButtonMoreGoal.text = if (isExpanded) "닫기" else "더보기"
                if (isExpanded) {
                    cumGoalRecycler.visibility = View.VISIBLE
                } else {
                    cumGoalRecycler.visibility = View.GONE
                }
            }

            cumButtonMoreGoal.setOnClickListener {
                leaderBoardViewModel.toggleCumulativeGoals()
            }
        }
    }

}