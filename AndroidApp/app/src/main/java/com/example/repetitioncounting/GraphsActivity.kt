package com.example.repetitioncounting

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.repetitioncounting.rabbitMQ.RabbitServer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import java.nio.charset.StandardCharsets


class GraphsActivity : AppCompatActivity() {

    lateinit var year: Spinner
    lateinit var month: Spinner
    private var chosenYear: String = ""
    private var chosenMonth: String = ""

    private lateinit var dates: List<String>
    private lateinit var weight: Array<Float>
    private lateinit var reps: Array<Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs)

        val barChart = findViewById<BarChart>(R.id.bargraph)

        val intent: Intent = intent
        var chosenExercise = intent.getStringExtra("ex") as String

        val graphEx = findViewById<TextView>(R.id.graphExTextView)
        graphEx.text = chosenExercise

        year = findViewById<Spinner>(R.id.yearSpinner)
        month = findViewById<Spinner>(R.id.monthSpinner)

        val years = arrayOf("2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")
        year.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, years)
        year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenYear = years[position]

                var specificDates: ArrayList<String> = ArrayList<String>()
                var specificWeights: ArrayList<Float> = ArrayList<Float>()
                var specificReps: ArrayList<Float> = ArrayList<Float>()
                for(i in 0..dates.size-1){
                    if(dates[i].contains(chosenYear) && dates[i].contains(chosenMonth) && chosenMonth != "All"){
                        specificDates.add(dates[i])
                        specificReps.add(reps[i])
                        specificWeights.add(weight[i])
                    }
                    if(dates[i].contains(chosenYear) && chosenMonth == "All"){
                        specificDates.add(dates[i])
                        specificReps.add(reps[i])
                        specificWeights.add(weight[i])
                    }
                }

                var newDates: ArrayList<String> = ArrayList<String>()
                for(date in specificDates){
                    var newDate = date.removeRange(0,2)
                    newDate = newDate.removeRange(newDate.length-5, newDate.length)
                    newDates.add(newDate)
                }

                val yValues = ArrayList<BarEntry>()
                val yValuesReps = ArrayList<BarEntry>()
                for (i in 0..specificWeights.size-1){
                    yValues.add(BarEntry(specificWeights[i], i))
                    yValuesReps.add(BarEntry(specificReps[i], i))
                }
                val barDataSet = BarDataSet(yValues, "Weight")
                val barDataSetReps = BarDataSet(yValuesReps, "Reps")
                barDataSet.color = resources.getColor(R.color.GraphData)
                barDataSetReps.color = resources.getColor(R.color.MyRed)
//                val finalBarDataSet = ArrayList<BarDataSet>()
//                finalBarDataSet.add(barDataSet)
//                finalBarDataSet.add(barDataSetReps)
//                val barData = BarData(newDates, finalBarDataSet as List<IBarDataSet>?)
                val barData = BarData(newDates, barDataSet)
                barData.addDataSet(barDataSetReps)

                barChart.data = barData
                barChart.setBackgroundColor(resources.getColor(R.color.GraphColor))
                barChart.animateXY(2000,2000)
                barChart.setDescription("Personal Records")
                barChart.setDescriptionColor(resources.getColor(R.color.GraphDesc))
                barChart.setDescriptionPosition(370f,225f)
                barChart.setDescriptionTextSize(13f)
            }
        }

        val months = arrayOf("All", "January", "February", "March", "April", "May", "July", "Jun", "August", "September", "October", "November", "December")
        month.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, months)
        month.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenMonth = months[position]

                var specificDates: ArrayList<String> = ArrayList<String>()
                var specificWeights: ArrayList<Float> = ArrayList<Float>()
                var specificReps: ArrayList<Float> = ArrayList<Float>()
                for(i in 0..dates.size-1){
                    if(dates[i].contains(chosenYear) && dates[i].contains(chosenMonth) && chosenMonth != "All"){
                        specificDates.add(dates[i])
                        specificReps.add(reps[i])
                        specificWeights.add(weight[i])
                    }
                    if(dates[i].contains(chosenYear) && chosenMonth == "All"){
                        specificDates.add(dates[i])
                        specificReps.add(reps[i])
                        specificWeights.add(weight[i])
                    }
                }

                var newDates: ArrayList<String> = ArrayList<String>()
                for(date in specificDates){
                    var newDate = date.removeRange(0,2)
                    newDate = newDate.removeRange(newDate.length-5, newDate.length)
                    newDates.add(newDate)
                }

                val yValues = ArrayList<BarEntry>()
                val yValuesReps = ArrayList<BarEntry>()
                for (i in 0..specificWeights.size-1){
                    yValues.add(BarEntry(specificWeights[i], i))
                    yValuesReps.add(BarEntry(specificReps[i], i))
                }
                val barDataSet = BarDataSet(yValues, "Weight")
                val barDataSetReps = BarDataSet(yValuesReps, "Reps")
                barDataSet.color = resources.getColor(R.color.GraphData)
                barDataSetReps.color = resources.getColor(R.color.MyRed)
//                val finalBarDataSet = ArrayList<BarDataSet>()
//                finalBarDataSet.add(barDataSet)
//                finalBarDataSet.add(barDataSetReps)
//                val barData = BarData(newDates, finalBarDataSet as List<IBarDataSet>?)
                val barData = BarData(newDates, barDataSet)
                barData.addDataSet(barDataSetReps)

                barChart.data = barData
                barChart.setBackgroundColor(resources.getColor(R.color.GraphColor))
                barChart.animateXY(2000,2000)
                barChart.setDescription("Personal Records")
                barChart.setDescriptionColor(resources.getColor(R.color.GraphDesc))
                barChart.setDescriptionPosition(370f,225f)
                barChart.setDescriptionTextSize(13f)
            }
        }

        setBarChartValues(chosenExercise)
    }

    fun setBarChartValues(chosenExercise:String){
        var workout = ""
        val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
        var connection = rabbitServer.getConnection()
        var channel = rabbitServer.getChannel()
        if(channel != null){
            val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                workout = message
                println(" [x] Received '$message'")
            }
            channel.basicPublish("", "finishSending", null, chosenExercise.toByteArray())
            Thread.sleep(2000L)

            channel.basicConsume("repCountResult", true, deliverCallback, CancelCallback { consumerTag: String? -> })
            println("Ma execut")
        }else{
            Log.d("myTag", "Channel is null");
        }
        while (workout == ""){}
        connection?.close()

        var workoutList: List<String> = workout.lines()
        val datesString = workoutList[0].substring(1, workoutList[0].length-1).replace("\\s".toRegex(), "")
        val repsString = workoutList[1].substring(1, workoutList[1].length-1).replace("\\s".toRegex(), "")
        val weightString = workoutList[2].substring(1, workoutList[2].length-1).replace("\\s".toRegex(), "")
        dates = listOf(*datesString.split(",").toTypedArray())
        val repsStr: List<String> = listOf(*repsString.split(",").toTypedArray())
        reps = repsStr.map { it.toFloat() }.toTypedArray()
        val weightStr: List<String> = listOf(*weightString.split(",").toTypedArray())
        weight = weightStr.map { it.toFloat() }.toTypedArray()


//        val combinedChart = findViewById<CombinedChart>(R.id.bargraph)
//
//        val yValues = ArrayList<BarEntry>()
//        for(i in 0..weight.size-1){
//            yValues.add(BarEntry(weight[i],i))
//        }
//        val barDataSet = BarDataSet(yValues, "Weight")
//        val barData = BarData(dates, barDataSet)
//
//        val yValuesLine = ArrayList<Entry>()
//        for (i in 0..reps.size-1){
//            yValuesLine.add(BarEntry(reps[i], i))
//        }
//        val lineDataSet = LineDataSet(yValuesLine, "Reps")
//        val lineData = LineData(dates, lineDataSet)
//
//
//        val combinedData = CombinedData()
//        combinedData.setData(barData)
//        combinedData.setData(lineData)
//        combinedChart.data = combinedData
    }
}