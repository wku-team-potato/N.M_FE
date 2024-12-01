package com.example.application.ui.view.main.pages

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.application.R
import com.example.application.common.extensions.displayText
import com.example.application.databinding.FragmentHealthBinding
import com.example.application.databinding.LayoutCalendarDayBinding
import com.example.application.utils.HealthPermissions
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class HealthFragment : BaseFragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding
        get() = _binding!!

    private var selectedDate = MutableStateFlow(LocalDate.now())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    private lateinit var healthConnectClient: HealthConnectClient

//    private val requestPermission = registerForActivityResult(
//        PermissionController.createRequestPermissionResultContract()
//    ) { granted ->
//        if (granted.containsAll(HealthPermissions.PERMISSIONS)) {
//            Log.d("HealthFragment", "Permission granted")
//            CoroutineScope(Dispatchers.Main).launch {
//                readStepsData()
//            }
//        } else {
//            Log.d("HealthFragment", "Permission denied")
//        }
//    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
//            connectHealthData()
            readStepsData()
        }

        initUi()

//        lifecycleScope.launch {
//            selectedDate.collectLatest {
//                binding.noRecordContainer.isVisible = (it != LocalDate.now())
//            }
//        }
    }

    private fun initUi() = with(binding) {

        initCalendar()

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

    private fun initCalendar() = with(binding) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = LayoutCalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
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

    private fun initPieChart(title: String, chart: PieChart) {
        with(chart) {
            setUsePercentValues(true)
            legend.isEnabled = false
            description.isEnabled = false

            centerText = title
            setCenterTextSize(18f)
            setDrawCenterText(true)

            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 75f
            transparentCircleRadius = 80f

            setRotationAngle(0f)
            isRotationEnabled = false
            isHighlightPerTapEnabled = false
        }

        setPieChartData(title, chart)
    }

    private fun setPieChartData(title: String, chart: PieChart) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(25f))
        entries.add(PieEntry(75f))

        val dataSet = PieDataSet(entries, title)
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        dataSet.colors = listOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_errorContainer_mediumContrast
            ),
            ContextCompat.getColor(requireContext(), R.color.md_theme_primary),
        )

        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setDrawValues(false)
        chart.data = data

        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }

    private fun initLineChart(chart: LineChart) {
        with(chart) {
            chart.setBackgroundColor(Color.TRANSPARENT)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false

            chart.setTouchEnabled(true)
            chart.setDrawGridBackground(false)

            chart.isDragEnabled = true
            chart.setScaleEnabled(true)
            chart.setPinchZoom(true)

            xAxis.setDrawLabels(false)
            xAxis.axisMinimum = 0f
            xAxis.granularity = 1f

            axisRight.isEnabled = false

            axisLeft.axisMaximum = 79f
            axisLeft.axisMinimum = 76f
        }

        setLineChartData(chart)
    }

    private fun setLineChartData(chart: LineChart) {
        val values = ArrayList<Entry>()
        values.add(Entry(0f, 79f))
        values.add(Entry(1f, 78f))
        values.add(Entry(2f, 77f))
        values.add(Entry(3f, 76f))
        values.add(Entry(4f, 78f))

        val set1 = LineDataSet(values, "몸무게")
        set1.setDrawIcons(false)
        set1.enableDashedLine(10f, 5f, 0f)

        // black lines and points
        set1.color = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
        set1.setCircleColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))

        // line thickness and point size
        set1.lineWidth = 1f
        set1.circleRadius = 3f

        // draw points as solid circles
        set1.setDrawCircleHole(false)

        // text size of values
        set1.setDrawValues(false)
        set1.valueTextSize = 9f

        // draw selection line as dashed
        set1.isHighlightEnabled = false
        set1.disableDashedHighlightLine()
        // set1.enableDashedHighlightLine(10f, 5f, 0f)

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the data sets

        // create a data object with the data sets
        val data = LineData(dataSets)

        // set data
        chart.data = data
    }

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

            CoroutineScope(Dispatchers.Main).launch {
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