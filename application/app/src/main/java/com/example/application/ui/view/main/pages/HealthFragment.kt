package com.example.application.ui.view.main.pages

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.application.R
import com.example.application.common.extensions.displayText
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.ProfileRepository
import com.example.application.databinding.FragmentHealthBinding
import com.example.application.databinding.LayoutCalendarDayBinding
import com.example.application.ui.view.meals.CalorieActivity
import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.ui.meals.function.data.MealDetailResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.data.MealTotalResponse
import com.example.application.ui.meals.function.repository.HealthRepository
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.HealthViewModel
import com.example.application.ui.meals.function.viewmodel.HealthViewModelFactory
import com.example.application.utils.HealthPermissions
import com.example.application.utils.RetrofitInstance
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.kizitonwose.calendar.view.WeekScrollListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class HealthFragment : BaseFragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding
        get() = _binding!!

//    private lateinit var mealViewModel: MealViewModel
//    private lateinit var healthViewModel: HealthViewModel
//    private val sharedMealViewModel: SharedMealViewModel by activityViewModels()

    private var selectedDate = MutableStateFlow(LocalDate.now())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private lateinit var healthViewModel: HealthViewModel
    private lateinit var healthConnectClient: HealthConnectClient

    private var base_kcal = 0
    private var currentMealSummary: MealSummaryResponse? = null
    private var currentUserInfo: ProfileResponse? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val formattedApiDate = selectedDate.value.format(apiDateFormatter)
        Log.d("HealthFragment", "Formatted API Date: $formattedApiDate")

//        initObservers()
        healthViewModel.getUserInfo()
        healthViewModel.getMealSummary(formattedApiDate)
        healthViewModel.getWeightList()
//        initViewModel()
//        initUi()
//        initObservers()
//        setupListeners()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initUi()
        initObservers()
        setupListeners()
        healthViewModel.getUserInfo()

        binding.editWeightHeight.setOnClickListener {
            showMyPage{ updated ->
                if(updated){
                    healthViewModel.getUserInfo()
                    healthViewModel.getWeightList()
//                    initObservers()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            readStepsData()
            healthViewModel.getWeightList()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDate.collectLatest { newDate ->
                    healthViewModel.getMealSummary(newDate.format(apiDateFormatter))
                }
            }
        }
    }

    private fun mealSummaryUI(melSummaryResponse: MealSummaryResponse, userInfo: ProfileResponse) {
        val breakFast: MealDetailResponse = melSummaryResponse.breakfast
        val lunch: MealDetailResponse = melSummaryResponse.lunch
        val dinner: MealDetailResponse = melSummaryResponse.dinner
        val summary: MealTotalResponse = melSummaryResponse.summary

        val weight = userInfo.weight
        val height = userInfo.height
        base_kcal = (15.3 * weight + 679).toInt()
        val base_carbohydrate = (base_kcal * 0.5 / 4).toInt()
        val base_protein = (base_kcal * 0.3 / 4).toInt()
        val base_fat = (base_kcal * 0.2 / 9).toInt()

        val s_c_percent = summary.calorie / base_kcal.toDouble() * 100
        val s_ca_percent = summary.carbohydrate / base_carbohydrate.toDouble() * 100
        val s_p_percent = summary.protein / base_protein.toDouble() * 100
        val s_f_percent = summary.fat / base_fat.toDouble() * 100

        updatePercentView(binding.kcalPercent, s_c_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
        updatePercentView(binding.caboPercent, s_ca_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
        updatePercentView(binding.proteinPercent, s_p_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
        updatePercentView(binding.fatPercent, s_f_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)

        val base_calorie_text = "${NumberFormat.getInstance().format(summary.calorie.toInt())} / ${NumberFormat.getInstance().format(base_kcal)}kcal"
        val base_carbohydrate_text = "${NumberFormat.getInstance().format(summary.carbohydrate.toInt())} / ${NumberFormat.getInstance().format(base_carbohydrate)}g"
        val base_protein_text = "${NumberFormat.getInstance().format(summary.protein.toInt())} / ${NumberFormat.getInstance().format(base_protein)}g"
        val base_fat_text = "${NumberFormat.getInstance().format(summary.fat.toInt())} / ${NumberFormat.getInstance().format(base_fat)}g"

        binding.kcalText.text = updateGraphicView(base_calorie_text, R.color.health_cal, 36)
        binding.caboText.text = updateGraphicView(base_carbohydrate_text, R.color.health_cabo, 36)
        binding.proteinText.text = updateGraphicView(base_protein_text, R.color.health_protein, 36)
        binding.fatText.text = updateGraphicView(base_fat_text, R.color.health_fat, 36)

        updateProgressBar(binding.kcalProgress, s_c_percent, 100, R.color.health_cal, R.color.health_cal)
        updateProgressBar(binding.caboProgress, s_ca_percent, 100, R.color.health_cabo, R.color.health_cabo)
        updateProgressBar(binding.proteinProgress, s_p_percent, 100, R.color.health_protein, R.color.health_protein)
        updateProgressBar(binding.fatProgress, s_f_percent, 100, R.color.health_fat, R.color.health_fat)
    }

//    private fun mealSummaryUI(melSummaryResponse: MealSummaryResponse) {
//        val breakFast: MealDetailResponse = melSummaryResponse.breakfast
//        val lunch: MealDetailResponse = melSummaryResponse.lunch
//        val dinner: MealDetailResponse = melSummaryResponse.dinner
//        val summary: MealTotalResponse = melSummaryResponse.summary
//
//        healthViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
//            userInfo?.let {
//
//                val weight = healthViewModel.userInfo.value?.weight ?: 0f
//                val height = healthViewModel.userInfo.value?.height ?: 0f
//                base_kcal = (15.3 * weight + 679).toInt()
//                val base_carbohydrate = (base_kcal * 0.5 / 4).toInt()
//                val base_protein = (base_kcal * 0.3 / 4).toInt()
//                val base_fat = (base_kcal * 0.2 / 9).toInt()
//
////                val base_kcal = 2400
////                val base_carbohydrate = 300
////                val base_protein = 75
////                val base_fat = 60
//
//                val s_c_percent = summary.calorie / base_kcal * 100
//                val s_ca_percent = summary.carbohydrate / base_carbohydrate * 100
//                val s_p_percent = summary.protein / base_protein * 100
//                val s_f_percent = summary.fat / base_fat * 100
//
//                Log.d("HealthFragment", "Base Kcal: $s_c_percent")
//                Log.d("HealthFragment", "Base Carbohydrate: $s_ca_percent")
//                Log.d("HealthFragment", "Base Protein: $s_p_percent")
//                Log.d("HealthFragment", "Base Fat: $s_f_percent")
//
//                updatePercentView(binding.kcalPercent, s_c_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
//                updatePercentView(binding.caboPercent, s_ca_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
//                updatePercentView(binding.proteinPercent, s_p_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
//                updatePercentView(binding.fatPercent, s_f_percent, R.color.md_theme_errorContainer_mediumContrast, R.color.md_theme_background_highContrast)
//
//                val base_calorie_text = "${NumberFormat.getInstance().format(summary.calorie.toInt())} / ${NumberFormat.getInstance().format(base_kcal)}kcal"
//                val base_carbohydrate_text = "${NumberFormat.getInstance().format(summary.carbohydrate.toInt())} / ${NumberFormat.getInstance().format(base_carbohydrate)}g"
//                val base_protein_text = "${NumberFormat.getInstance().format(summary.protein.toInt())} / ${NumberFormat.getInstance().format(base_protein)}g"
//                val base_fat_text = "${NumberFormat.getInstance().format(summary.fat.toInt())} / ${NumberFormat.getInstance().format(base_fat)}g"
//
//                binding.kcalText.text = updateGraphicView(base_calorie_text, R.color.health_cal, 36)
//                binding.caboText.text = updateGraphicView(base_carbohydrate_text, R.color.health_cabo, 36)
//                binding.proteinText.text = updateGraphicView(base_protein_text, R.color.health_protein, 36)
//                binding.fatText.text = updateGraphicView(base_fat_text, R.color.health_fat, 36)
//
//
//                updateProgressBar(binding.kcalProgress, s_c_percent, 100, R.color.health_cal, R.color.health_cal)
//                updateProgressBar(binding.caboProgress, s_ca_percent, 100, R.color.health_cabo, R.color.health_cabo)
//                updateProgressBar(binding.proteinProgress, s_p_percent, 100, R.color.health_protein, R.color.health_protein)
//                updateProgressBar(binding.fatProgress, s_f_percent, 100, R.color.health_fat, R.color.health_fat)
//            }
//        }
//
//
//
//    }

    private fun updatePercentView(textView: TextView, percent: Double, overLimitColor: Int, defaultColor: Int) {
        textView.text = "${percent.toInt()}%"
        textView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (percent > 100) overLimitColor else defaultColor
            )
        )
    }

    private fun updateGraphicView(
        text: String,
        colorRes: Int,
        textSizeSp: Int
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        val slashIndex = text.indexOf("/")

        if (slashIndex > 0) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), colorRes)),
                0,
                slashIndex - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                AbsoluteSizeSpan(textSizeSp, true),
                0,
                slashIndex - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    } // updateGraphicView

    private fun updateProgressBar(
        progressBar: ProgressBar,
        progressValue: Double,
        maxValue: Int,
        overLimitColor: Int,
        defaultColor: Int
    ) {
        val progress = progressValue.coerceIn(0.0, maxValue.toDouble()).toInt() // 안전한 범위 값으로 설정

        // 기존 애니메이션을 취소하고 초기화
        progressBar.clearAnimation()
        progressBar.progress = 0 // Progress를 초기화
        progressBar.max = maxValue

        // ProgressBar 색상 설정
        val color = if (progress > maxValue) overLimitColor else defaultColor
        progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))

        Log.d("Animate", "Progress: $progress, Max: $maxValue")

        // 애니메이션 실행
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress).apply {
            Log.d("HealthFragment", "Progress: $progress, Max: $maxValue")
            Log.d("HealthFragment", "Duration: ${calculateDuration(progress, maxValue)}")
            duration = calculateDuration(progress, maxValue)
            interpolator = LinearInterpolator()
        }

        animator.start()

        Log.d("HealthFragment", "Progress: $progress, Max: $maxValue")
    } // updateProgressBar 초과시 색상 변경 및 흔들림 애니메이션 추가

    private fun calculateDuration(progress: Int, maxValue: Int): Long {
        val baseDuration = 1000L
        val minDuration = 300L
        return (baseDuration * (progress.toDouble() / maxValue)).toLong().coerceAtLeast(minDuration)
    }

    private fun initWeightsChart(weightList: List<HealthResponse>): LineData {

        Log.d("HealthFragment", "Received weight list: $weightList")

        val entries = weightList.mapIndexed { index, healthResponse ->
            Entry(index.toFloat(), healthResponse.weight.toFloat())
        }

        val dataSet = LineDataSet(entries, "몸무게 변화").apply {
            color = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary)
            lineWidth = 2f
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
            circleRadius = 4f
            setDrawCircleHole(true)
        }

        return LineData(dataSet)
    }

    private fun setupLineChart(lineChart: LineChart, weightList: List<HealthResponse>) {
        val data = initWeightsChart(weightList)

        lineChart.apply {
            // 차트 설명 비활성화
            description.isEnabled = false

            // 범례 비활성화
            legend.isEnabled = false

            // 축 설정
            axisRight.isEnabled = false // 오른쪽 축 비활성화
            axisLeft.isEnabled = false  // 왼쪽 축 비활성화

            // X축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textSize = 12f
                textColor = ContextCompat.getColor(context, R.color.md_theme_primary)
                setDrawGridLines(false)
//                labelRotationAngle = -45f
                // 날짜 포맷터 설정
                valueFormatter = DateAxisFormatter(weightList)
            }

            axisLeft.apply {
                val maxWeight = weightList.maxOf { it.weight.toFloat() }
                axisMaximum = maxWeight + maxWeight * 0.3f // 최대값의 10% 추가
            }

            // 배경 설정
            setBackgroundColor(Color.TRANSPARENT)
            setDrawGridBackground(false)

            // 터치 설정
            setTouchEnabled(false)
            isHighlightPerTapEnabled = false

            // 줌 설정
            setScaleEnabled(false)
            setPinchZoom(false)

            // 애니메이션
            animateXY(200, 800, Easing.EaseInOutQuad)
//            animateY(800, Easing.EaseInOutQuad)
        }

        // 라인 데이터 스타일링
        data.dataSets.forEach { dataset ->
            if (dataset is LineDataSet) {
                dataset.apply {
                    // 곡선으로 설정
                    mode = LineDataSet.Mode.CUBIC_BEZIER

                    // 라이트 모드 색상
                    color = ContextCompat.getColor(requireContext(), R.color.chart_line)
                    setCircleColor(ContextCompat.getColor(requireContext(), R.color.chart_point))

                    // 선 스타일
                    lineWidth = 3f
                    circleRadius = 5f

                    // 값 표시 설정
                    setDrawValues(true)
                    valueTextSize = 12f
                    valueTextColor = ContextCompat.getColor(requireContext(), R.color.chart_text)
                    valueFormatter = DateAxisFormatter(weightList)
                    valueFormatter = WeightValueFormatter()

                    // 하이라이트 설정
                    highlightLineWidth = 2f
                    highLightColor = ContextCompat.getColor(requireContext(), R.color.chart_highlight)

                    // 채우기 설정
                    setDrawFilled(true)
                    fillAlpha = 30
                    fillColor = ContextCompat.getColor(requireContext(), R.color.chart_fill)
                }
            }
        }

        lineChart.data = data
        lineChart.invalidate()
    }

    // 날짜 포맷터
    class DateAxisFormatter(private val weightList: List<HealthResponse>) : ValueFormatter() {
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // API 날짜 형식
        private val outputDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault()) // 출력 날짜 형식

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in weightList.indices) {
                val date = inputDateFormat.parse(weightList[index].created_at)
                outputDateFormat.format(date) // X축에 날짜 표시
            } else {
                ""
            }
        }
    }

    // 몸무게 값 포맷터
    class WeightValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return String.format("%.1fkg", value)
        }
    }

//    private fun initObservers() {
//        healthViewModel.mealSummary.observe(viewLifecycleOwner) { mealSummary ->
//
//            if (mealSummary != null) {
//                mealSummaryUI(mealSummary)
//            } else {
//                Log.e("HealthFragment", "Meal summary is null")
//            }
//
//        }
//
//        healthViewModel.weightList.observe(viewLifecycleOwner) { weightList ->
//            Log.d("HealthFragment", "Received weight list: $weightList")
//            if (!weightList.isNullOrEmpty()) {
//                setupLineChart(binding.weightChart, weightList.asReversed())
//            } else {
//                Log.e("HealthFragment", "Weight list is empty or null")
//            }
//        }
//
//        healthViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
//            userInfo?.let {
//                // weight 텍스트 업데이트
//                binding.weightText.text = String.format("%.1f kg", it.weight)
//                Log.d("HealthFragment", "Updated weight: ${it.weight}")
//                viewLifecycleOwner.lifecycleScope.launch {
//                    healthViewModel.getWeightList()
//                }
//            }
//        }
//    }

    private fun initObservers() {
        // userInfo 관찰
        healthViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            userInfo?.let {
                binding.weightText.text = String.format("%.1f kg", it.weight)
                // 필요하다면 here healthViewModel.getWeightList() 호출

                currentUserInfo = it
                checkAndCallMealSummaryUI() // 두 값이 모두 있으면 mealSummaryUI 호출
            }
        }

        // mealSummary 관찰
        healthViewModel.mealSummary.observe(viewLifecycleOwner) { mealSummary ->
            mealSummary?.let {
                currentMealSummary = it
                checkAndCallMealSummaryUI() // 두 값이 모두 있으면 mealSummaryUI 호출
            }
        }

        healthViewModel.weightList.observe(viewLifecycleOwner) { weightList ->
            // weightList UI 업데이트
            if (!weightList.isNullOrEmpty()) {
                setupLineChart(binding.weightChart, weightList.asReversed())
            }
        }
    }

    private fun checkAndCallMealSummaryUI() {
        val userInfo = currentUserInfo
        val mealSummary = currentMealSummary

        if (userInfo != null && mealSummary != null) {
            // userInfo와 mealSummary를 모두 파라미터로 전달
            mealSummaryUI(mealSummary, userInfo)
        }
    }


    private fun initViewModel() {
        val healthRepository = HealthRepository(RetrofitInstance.healthService)
        val mealRepository = MealRepository(RetrofitInstance.mealService)
        val profileRepository = ProfileRepository(RetrofitInstance.profileService)
        val healthFactory = HealthViewModelFactory(healthRepository, mealRepository, profileRepository)
        healthViewModel = ViewModelProvider(this, healthFactory).get(HealthViewModel::class.java)
    }


    private fun setupListeners() {
        binding.buttonDecreaseWeight.setOnClickListener {
            val currentWeight = healthViewModel.userInfo.value?.weight ?: 0f
            if (currentWeight > 0f) {
                val updatedWeight = BigDecimal(currentWeight.toString())
                    .subtract(BigDecimal("0.1")) // 0.1을 뺌
                    .setScale(2, RoundingMode.HALF_UP) // 소수점 두 자리 반올림
                    .toFloat()
                healthViewModel.updateUserInfo(updatedWeight)
            } else {
                Toast.makeText(requireContext(), "Weight cannot be less than 0", Toast.LENGTH_SHORT).show()
            }
            initObservers()
        }

        binding.buttonIncreaseWeight.setOnClickListener {
            val currentWeight = healthViewModel.userInfo.value?.weight ?: 0f
            val updatedWeight = BigDecimal(currentWeight.toString())
                .add(BigDecimal("0.1")) // 0.1을 뺌
                .setScale(2, RoundingMode.HALF_UP) // 소수점 두 자리 반올림
                .toFloat()
            healthViewModel.updateUserInfo(updatedWeight)
            initObservers()
        }
    }

    private fun initUi() = with(binding) {

        initCalendar()
//        initWeekCalendar()
//        initMonthCalendar()

        binding.buttonNutrition.setOnClickListener{
            startActivity(Intent(requireContext(), CalorieActivity::class.java).apply {
                Log.d("HealthFragment", base_kcal.toString())
                putExtra("baseCalorie", base_kcal.toString())
                putExtra("selectedDate", selectedDate.value.format(apiDateFormatter))
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }

//        initPieChart("단백질", proteinChart)
//        initPieChart("탄수화물", carbohydratesChart)
//        initPieChart("지방", fatChart)
//        initLineChart(weightChart)
//
//        calorieContainer.setOnClickListener {
//            startActivity(Intent(requireContext(), CalorieActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            })
//        }
    }

//    private fun initWeekCalendar() = with(binding) {
//        class DayViewContainer(view: View) : ViewContainer(view) {
//            val bind = LayoutCalendarDayBinding.bind(view)
//            lateinit var day: WeekDay
//
//            init {
//                view.setOnClickListener {
//                    if (day.date.isAfter(LocalDate.now())) return@setOnClickListener
//
//                    if (selectedDate.value != day.date) {
//                        val oldDate = selectedDate.value
//                        selectedDate.value = day.date
//
//                        weekCalendarView.notifyDateChanged(day.date)
//                        monthCalendarView.notifyDateChanged(day.date)
//
//                        oldDate?.let {
//                            weekCalendarView.notifyDateChanged(it)
//                            monthCalendarView.notifyDateChanged(it)
//                        }
//                    }
//                }
//            }
//
//            fun bind(day: WeekDay) {
//                this.day = day
//                bind.exSevenDateText.text = dateFormatter.format(day.date)
//                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()
//
//                val colorRes = if (day.date == selectedDate.value) {
//                    R.color.md_theme_inversePrimary
//                } else {
//                    R.color.md_theme_onPrimary
//                }
//                bind.exSevenDateText.setTextColor(ContextCompat.getColor(view.context, colorRes))
//                bind.exSevenSelectedView.isVisible = day.date == selectedDate.value
//            }
//        }
//
//        weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
//        }
//
//        val currentMonth = YearMonth.now()
//        weekCalendarView.setup(
//            currentMonth.minusMonths(5).atStartOfMonth(),
//            currentMonth.plusMonths(5).atEndOfMonth(),
//            firstDayOfWeekFromLocale(),
//        )
//        weekCalendarView.scrollToDate(LocalDate.now())
//        weekCalendarView.weekScrollListener = object : WeekScrollListener {
//            override fun invoke(p1: Week) {
//                val calendar = Calendar.getInstance().apply {
//                    set(Calendar.DAY_OF_MONTH, 1)
//                    set(Calendar.YEAR, p1.days.first().date.year)
//                    set(Calendar.MONTH, p1.days.first().date.monthValue - 1)
//                }
//
//                yearMonthTextView.text =
//                    SimpleDateFormat("MMM", Locale.US).format(calendar.time)
//            }
//        }
//    }
//
//    private fun initMonthCalendar() = with(binding) {
//        class DayViewContainer(view: View) : ViewContainer(view) {
//            val bind = LayoutCalendarDayBinding.bind(view)
//            lateinit var day: CalendarDay
//
//            init {
//                view.setOnClickListener {
//                    if (day.date.isAfter(LocalDate.now())) return@setOnClickListener
//
//                    val oldDate = selectedDate.value
//                    selectedDate.value = day.date
//
//                    weekCalendarView.notifyDateChanged(day.date)
//                    monthCalendarView.notifyDateChanged(day.date)
//
//                    oldDate?.let {
//                        weekCalendarView.notifyDateChanged(it)
//                        monthCalendarView.notifyDateChanged(it)
//                    }
//                }
//            }
//
//            fun bind(day: CalendarDay) {
//                this.day = day
//                bind.exSevenDateText.text = dateFormatter.format(day.date)
//                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()
//
//                val colorRes = if (day.date == selectedDate.value) {
//                    R.color.md_theme_inversePrimary
//                } else {
//                    R.color.md_theme_onPrimary
//                }
//                bind.exSevenDateText.setTextColor(ContextCompat.getColor(view.context, colorRes))
//                bind.exSevenSelectedView.isVisible = day.date == selectedDate.value
//            }
//        }
//
//        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, data: CalendarDay) = container.bind(data)
//        }
//
//        val currentMonth = YearMonth.now()
//        monthCalendarView.setup(
//            currentMonth.minusMonths(5),
//            currentMonth.plusMonths(5),
//            firstDayOfWeekFromLocale(),
//        )
//        monthCalendarView.scrollToDate(LocalDate.now())
//        monthCalendarView.monthScrollListener = object : MonthScrollListener {
//            override fun invoke(p1: CalendarMonth) {
//                val calendar = Calendar.getInstance().apply {
//                    set(Calendar.DAY_OF_MONTH, 1)
//                    set(Calendar.YEAR, p1.yearMonth.year)
//                    set(Calendar.MONTH, p1.yearMonth.monthValue - 1)
//                }
//
//                yearMonthTextView.text =
//                    SimpleDateFormat("MMM", Locale.US).format(calendar.time)
//            }
//        }
//    }

    private fun initCalendar() = with(binding) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = LayoutCalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    val today = LocalDate.now()

                    if (day.date.isAfter(today)) {
                        Toast.makeText(requireContext(), "해당 날짜는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (selectedDate.value != day.date) {
                        val oldDate = selectedDate.value
                        selectedDate.value = day.date
                        calendarView.notifyDateChanged(day.date)
                        oldDate?.let { calendarView.notifyDateChanged(it) }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        readStepsData()
                    }
                    Log.d("HealthFragment", selectedDate.value.toString())
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

                val colorRes = if (day.date == selectedDate.value) {
                    R.color.md_theme_inversePrimary
                } else {
                    R.color.md_theme_onPrimary
                }
                bind.exSevenDateText.setTextColor(ContextCompat.getColor(view.context, colorRes))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate.value
            }
        }

        calendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        val currentMonth = YearMonth.now()
        calendarView.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        calendarView.scrollToDate(LocalDate.now())
    }

//    private fun initPieChart(title: String, chart: PieChart) {
//        with(chart) {
//            setUsePercentValues(true)
//            legend.isEnabled = false
//            description.isEnabled = false
//
//            centerText = title
//            setCenterTextSize(18f)
//            setDrawCenterText(true)
//
//            isDrawHoleEnabled = true
//            setHoleColor(Color.TRANSPARENT)
//            setTransparentCircleColor(Color.WHITE)
//            setTransparentCircleAlpha(110)
//            holeRadius = 75f
//            transparentCircleRadius = 80f
//
//            setRotationAngle(0f)
//            isRotationEnabled = false
//            isHighlightPerTapEnabled = false
//        }
//
//        setPieChartData(title, chart)
//    }

//    private fun setPieChartData(title: String, chart: PieChart) {
//        val entries = ArrayList<PieEntry>()
//        entries.add(PieEntry(25f))
//        entries.add(PieEntry(75f))
//
//        val dataSet = PieDataSet(entries, title)
//        dataSet.setDrawIcons(false)
//        dataSet.sliceSpace = 3f
//        dataSet.iconsOffset = MPPointF(0f, 40f)
//        dataSet.selectionShift = 5f
//
//        dataSet.colors = listOf(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.md_theme_errorContainer_mediumContrast
//            ),
//            ContextCompat.getColor(requireContext(), R.color.md_theme_primary),
//        )
//
//        //dataSet.setSelectionShift(0f);
//        val data = PieData(dataSet)
//        data.setDrawValues(false)
//        chart.data = data
//
//        // undo all highlights
//        chart.highlightValues(null)
//        chart.invalidate()
//    }

//    private fun initLineChart(chart: LineChart) {
//        with(chart) {
//            chart.setBackgroundColor(Color.TRANSPARENT)
//            chart.description.isEnabled = false
//            chart.legend.isEnabled = false
//
//            chart.setTouchEnabled(true)
//            chart.setDrawGridBackground(false)
//
//            chart.isDragEnabled = true
//            chart.setScaleEnabled(true)
//            chart.setPinchZoom(true)
//
//            xAxis.setDrawLabels(false)
//            xAxis.axisMinimum = 0f
//            xAxis.granularity = 1f
//
//            axisRight.isEnabled = false
//
//            axisLeft.axisMaximum = 79f
//            axisLeft.axisMinimum = 76f
//        }
//
//        setLineChartData(chart)
//    }
//
//    private fun setLineChartData(chart: LineChart) {
//        val values = ArrayList<Entry>()
//        values.add(Entry(0f, 79f))
//        values.add(Entry(1f, 78f))
//        values.add(Entry(2f, 77f))
//        values.add(Entry(3f, 76f))
//        values.add(Entry(4f, 78f))
//
//        val set1 = LineDataSet(values, "몸무게")
//        set1.setDrawIcons(false)
//        set1.enableDashedLine(10f, 5f, 0f)
//
//        // black lines and points
//        set1.color = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
//        set1.setCircleColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
//
//        // line thickness and point size
//        set1.lineWidth = 1f
//        set1.circleRadius = 3f
//
//        // draw points as solid circles
//        set1.setDrawCircleHole(false)
//
//        // text size of values
//        set1.setDrawValues(false)
//        set1.valueTextSize = 9f
//
//        // draw selection line as dashed
//        set1.isHighlightEnabled = false
//        set1.disableDashedHighlightLine()
//        // set1.enableDashedHighlightLine(10f, 5f, 0f)
//
//        val dataSets = ArrayList<ILineDataSet>()
//        dataSets.add(set1) // add the data sets
//
//        // create a data object with the data sets
//        val data = LineData(dataSets)
//
//        // set data
//        chart.data = data
//    }

//    private fun connectHealthData() {
//
//        // HealthConnect 앱 설치 여부 확인
//        val availabilityStatus = HealthConnectClient.getSdkStatus(requireContext(), "com.google.android.apps.healthdata")
//        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
//            activity?.finish()
//            openPlayStoreForHealthConnect()
//        }
//
//        // HealthConnect 앱 업데이트 필요 여부 확인
//        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
//            activity?.finish()
//            openPlayStoreForHealthConnect()
//        }
//
//        healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
//        requestPermission.launch(HealthPermissions.PERMISSIONS)
//    }

    private suspend fun readStepsData() {
        // HealthConnect 권한이 있는지 확인
        val permissionsGranted = HealthPermissions.PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }

        if (!permissionsGranted) {
            // 권한이 없으면 권한 요청을 진행
            binding.stepCount1.text = "헬스커넥트 권한 필요"
            binding.stepCount2.text = "헬스커넥트 권한 필요"
            return
        }

        // 권한이 있으면 데이터 읽기
        try {
            healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
            Log.d("HealthFragment", selectedDate.value.toString())
            val now: LocalDateTime = LocalDateTime.now()
            val startOfDay = LocalDateTime.of(selectedDate.value, LocalTime.MIDNIGHT)

            Log.d("HealthFragment", "Now: $now")
            Log.d("HealthFragment", "Start of day: $startOfDay")

            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now),
            )

            val response = healthConnectClient.readRecords(request)
            val steps = response.records.sumOf { it.count }

            lifecycleScope.launch {
                binding.stepCount1.text = steps.toString()
                binding.stepCount2.text = steps.toString()
            }
        } catch (e: Exception) {
            Log.e("HealthFragment", "Failed to read steps data", e)
            // 오류 처리: 예를 들어 사용자에게 권한 문제나 데이터 접근 오류를 알림
        }
    }


//    private fun openPlayStoreForHealthConnect() {
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
//            setPackage("com.android.vending")
//        }
//        startActivity(intent)
//    }
}