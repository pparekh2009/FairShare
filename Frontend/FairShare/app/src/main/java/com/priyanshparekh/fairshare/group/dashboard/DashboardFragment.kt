package com.priyanshparekh.fairshare.group.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.FragmentDashboardBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.utils.Status

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentDashboardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = (activity as GroupActivity).groupId
        val usernames = (activity as GroupActivity).usernames

        val charts = ArrayList<View>()

        dashboardViewModel.getDashboardExpenses(groupId)

        val chartPagerAdapter = ChartPagerAdapter(charts)

        binding.chartViewPager.adapter = chartPagerAdapter
        binding.chartViewPager.orientation = ORIENTATION_HORIZONTAL

        binding.dotsIndicator.attachTo(binding.chartViewPager)

        dashboardViewModel.dashboardDataStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    val dashboardDTO = status.data

                    val netBalances = dashboardDTO.netBalances
                    val dailyExpenses = dashboardDTO.cumulativeExpenses
                    val expenseShares = dashboardDTO.expenseShares

                    val barChart = setupChart1(netBalances, usernames)
                    charts.add(barChart)
                    val lineChart = setupChart2(dailyExpenses)
                    charts.add(lineChart)
                    val pieChart = setupChart3(expenseShares, usernames)
                    charts.add(pieChart)

                    chartPagerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupChart3(
        expenseShares: List<ExpenseShareEntry>,
        usernames: HashMap<Long, String>
    ): PieChart {
        val pieChart = PieChart(requireContext())

        val pieEntries = ArrayList<PieEntry>()
        for (expenseShare in expenseShares) {
            pieEntries.add(PieEntry(expenseShare.userTotal.toFloat(), usernames[expenseShare.paidBy]))
        }


        // Create a dataset
        val pieDataSet = PieDataSet(pieEntries, "")

        // Customize the dataset
        pieDataSet.sliceSpace = 3f // Space between slices
        pieDataSet.selectionShift = 5f // Slice highlight when clicked
        pieDataSet.setColors(
            resources.getColor(R.color.md_theme_primaryFixed, requireContext().theme),
            resources.getColor(R.color.md_theme_primaryFixedDim, requireContext().theme),
            resources.getColor(R.color.md_theme_primary, requireContext().theme),
            resources.getColor(R.color.md_theme_onPrimaryFixed, requireContext().theme),
        ) // Slice colors

        // Set data to the PieChart
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        // Customize the PieChart appearance (optional)
        pieChart.setUsePercentValues(true) // Show percentages
        pieChart.isDrawHoleEnabled = true // Draw a hole in the center (donut chart style)
        pieChart.setHoleColor(Color.WHITE) // Hole color
        pieChart.setTransparentCircleColor(Color.WHITE) // Color of the transparent circle
        pieChart.setTransparentCircleAlpha(110) // Transparency level of the transparent circle
        pieChart.setDrawCenterText(true) // Display text in the center of the pie chart
        pieChart.centerText = "Distribution" // Text in the center
        pieChart.setCenterTextSize(10f) // Text size in the center
        pieChart.setEntryLabelTextSize(14f)

        // Disable rotation and legend (optional)
        pieChart.isRotationEnabled = true // Allow rotation
        pieChart.isHighlightPerTapEnabled = true // Highlight the slice when clicked

        // Set legend settings
        val legend = pieChart.legend
        legend.isEnabled = true // Enable legend


        // Description settings (optional)
        val description = Description()
        description.text = "Spending Distribution by Member"
        pieChart.description = description

        val xAxis = XAxis()
        xAxis.textSize = 14f

        val yAxis = YAxis()
        yAxis.textSize = 14f

        // Refresh the chart
        pieChart.invalidate() // Redraw the chart

        return pieChart
    }

    private fun setupChart2(dailyExpenses: List<DailyExpenseEntry>): LineChart {
        val lineChart = LineChart(requireContext())

        // Set up data
        val entries = ArrayList<Entry>()
        for (i in dailyExpenses.indices) {
            entries.add(Entry(i.toFloat(), dailyExpenses[i].totalAmount.toFloat()))
        }

        // Create a dataset and give it a label
        val dataSet = LineDataSet(entries, "Expense Shares")


        // Customize the dataset (optional)
        dataSet.color = resources.getColor(R.color.md_theme_primary, requireContext().theme) // Set line color
        dataSet.circleColors = mutableListOf(resources.getColor(R.color.md_theme_tertiary, requireContext().theme))
        dataSet.valueTextColor = Color.BLACK // Set text color for values
        dataSet.lineWidth = 2f // Set line width

        // Create LineData and set it to the chart
        val lineData = LineData(dataSet)
        lineChart.setData(lineData)

        // Customize the chart appearance (optional)
        lineChart.invalidate() // Refresh the chart to show data


        // X-axis settings (optional)
        val xAxis: XAxis = lineChart.xAxis
        val labels = dailyExpenses.map { it.expenseDate ?: "" }
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Set X-axis position
        xAxis.granularity = 1f // Set the granularity for the x-axis (interval between values)


        // Y-axis settings (optional)
        val yAxis: YAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f // Set minimum Y value
        lineChart.axisRight.isEnabled = false // Disable right Y-axis (optional)

        return lineChart
    }

    private fun setupChart1(netBalances: List<NetBalanceEntry>, usernames: HashMap<Long, String>): BarChart {
        val barChart = BarChart(requireContext())

        val entries = ArrayList<BarEntry>()

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(usernames.values.toList())
        barChart.xAxis.granularity = 1f
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        for (i in netBalances.indices) {
            entries.add(BarEntry(i.toFloat(), netBalances[i].netBalance.toFloat()))
        }

        val description = Description()
        description.text = ""
        barChart.description = description

        val dataSet = BarDataSet(entries, "Net Balance")
        dataSet.colors = listOf(
            resources.getColor(R.color.md_theme_primaryFixed, requireContext().theme),
            resources.getColor(R.color.md_theme_primaryFixedDim, requireContext().theme),
            resources.getColor(R.color.md_theme_primary, requireContext().theme),
            resources.getColor(R.color.md_theme_primary_highContrast, requireContext().theme),
        )

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.invalidate()

        return barChart
    }
}