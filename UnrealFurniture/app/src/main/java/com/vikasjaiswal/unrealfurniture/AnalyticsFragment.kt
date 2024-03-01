package com.vikasjaiswal.unrealfurniture

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class AnalyticsFragment : Fragment() {


    private lateinit var lineChart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.analytics_fragment, container, false)

        lineChart = view.findViewById(R.id.lineChart)
        setupLineChart()

        return view
    }

    private fun setupLineChart() {
        val entries = mutableListOf<Entry>()
        entries.add(Entry(1f, 8f))
        entries.add(Entry(2f, 8f))
        entries.add(Entry(3f, 10f))
        entries.add(Entry(4f, 6f))
        entries.add(Entry(5f, 8f))
        entries.add(Entry(6f, 10f))
        entries.add(Entry(7f, 9f))
        entries.add(Entry(8f, 8f))
        entries.add(Entry(9f, 10f))
        entries.add(Entry(10f, 12f))

        val dataSet = LineDataSet(entries, "Some Chart")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.darker)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.darker)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_white)
        dataSet.fillDrawable = drawable
        dataSet.setDrawFilled(true)

        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.setTouchEnabled(false)
        lineChart.setPinchZoom(false)

        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawLabels(false)

        val description = Description()
        description.text = "Some Chart"
        lineChart.description = description

        lineChart.invalidate()
    }

}