package com.vikasjaiswal.unrealfurniture

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
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
import com.google.firebase.firestore.FirebaseFirestore

class AnalyticsFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()

    private lateinit var pieChart: PieChart

    private lateinit var barChart: BarChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.analytics_fragment, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()

        barChart = view.findViewById(R.id.barChart)
        setupBarChart()

        return view
    }

    private fun setupPieChart() {
        val entries2 = ArrayList<PieEntry>()

        db.collection("products")
            .get()
            .addOnSuccessListener {
                var sofas = 0
                var beds = 0
                var tables = 0
                var chairs = 0
                var bookshelves = 0
                var wardrobes = 0
                var others = 0

                for (document in it) {
                    if (document.get("productCategory") == "Sofas") {
                        sofas += 1
                    }
                    else if (document.get("productCategory") == "Beds") {
                        beds += 1
                    }
                    else if (document.get("productCategory") == "Tables") {
                        tables += 1
                    }
                    else if (document.get("productCategory") == "Chairs") {
                        chairs += 1
                    }
                    else if (document.get("productCategory") == "Bookshelves") {
                        bookshelves += 1
                    }
                    else if (document.get("productCategory") == "Wardrobes") {
                        wardrobes += 1
                    }
                    else {
                        others += 1
                    }
                }

                entries2.add(PieEntry(sofas.toFloat(), "Sofas"))
                entries2.add(PieEntry(beds.toFloat(), "Beds"))
                entries2.add(PieEntry(tables.toFloat(), "Tables"))
                entries2.add(PieEntry(chairs.toFloat(), "Chairs"))
                entries2.add(PieEntry(bookshelves.toFloat(), "Bookshelves"))
                entries2.add(PieEntry(wardrobes.toFloat(), "Wardrobes"))
                entries2.add(PieEntry(others.toFloat(), "Others"))

                val dataSet2 = PieDataSet(entries2, "Product Category")

                dataSet2.valueTextSize = 16f

                dataSet2.colors = mutableListOf( Color.CYAN, R.color.light, Color.LTGRAY, Color.DKGRAY, R.color.darker, Color.GREEN, R.color.dark)
                dataSet2.valueTextColor = Color.BLACK

                val pieData = PieData(dataSet2)

                pieChart.animateXY(1000, 1000)

                pieChart.data = pieData
                pieChart.invalidate()
            }

    }

    private fun setupBarChart() {
        val entries3 = ArrayList<BarEntry>()

        db.collection("orders")
            .get()
            .addOnSuccessListener {
                var ordersInProgress = 0
                var ordersCompleted = 0
                var ordersCancelled = 0

                for (document in it) {
                    if (document.get("orderStatus") in listOf("Order Placed", "Order Out for Delivery", "Order Shipped")){
                        ordersInProgress += 1
                    }
                    else if (document.get("orderStatus") == "Order Delivered") {
                        ordersCompleted += 1
                    }
                    else {
                        ordersCancelled += 1
                    }
                }

                entries3.add(BarEntry(2f, ordersInProgress.toFloat()))
                entries3.add(BarEntry(3f, ordersCompleted.toFloat()))
                entries3.add(BarEntry(4f, ordersCancelled.toFloat()))

                val dataSet3 = BarDataSet(entries3, "Order Status")

                dataSet3.valueTextSize = 16f

                dataSet3.colors = mutableListOf(Color.RED, Color.GREEN, Color.BLUE)

                val legendEntries = arrayListOf(LegendEntry("In Progress", Legend.LegendForm.CIRCLE, 10f, 10f, null, Color.RED),
                    LegendEntry("Completed", Legend.LegendForm.CIRCLE, 10f, 10f, null, Color.GREEN),
                    LegendEntry("Cancelled", Legend.LegendForm.CIRCLE, 10f, 10f, null, Color.BLUE))
                val legend = barChart.legend
                legend.isEnabled = true
                legend.textSize = 14f
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.formSize = 12f
                legend.form = Legend.LegendForm.CIRCLE
                legend.setCustom(legendEntries)

                val barData2 = BarData(dataSet3)

                barChart.animateY(1000)

                barChart.data = barData2


            }
    }

}