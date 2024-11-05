package com.example.application.ui.main.pages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.application.R
import com.example.application.common.extensions.displayText
import com.example.application.databinding.FragmentHealthBinding
import com.example.application.databinding.LayoutCalendarDayBinding
import com.example.application.ui.meals.CalorieActivity
import com.example.application.ui.meals.MealsActivity
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class HealthFragment : BaseFragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding
        get() = _binding!!

    private var selectedDate = MutableStateFlow(LocalDate.now())
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

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

        initUi()

        lifecycleScope.launch {
            selectedDate.collectLatest {
                binding.noRecordContainer.isVisible = (it != LocalDate.now())
            }
        }
    }

    private fun initUi() = with(binding) {
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
                showMyPage()
            }

            return@setOnMenuItemClickListener true
        }

        initCalendar()
        initPieChart("단백질", proteinChart)
        initPieChart("탄수화물", carbohydratesChart)
        initPieChart("지방", fatChart)
        initLineChart(weightChart)

        calorieContainer.setOnClickListener {
            startActivity(Intent(requireContext(), CalorieActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
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
}