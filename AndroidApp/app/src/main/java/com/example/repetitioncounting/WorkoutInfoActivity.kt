package com.example.repetitioncounting

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.*
import com.example.repetitioncounting.rabbitMQ.RabbitServer
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class  WorkoutInfoActivity : AppCompatActivity() {
    private lateinit var dateTextView: TextView
    private lateinit var dateButton: Button
    private lateinit var workoutTextView: TextView
    private lateinit var workoutStatus: TextView
    private lateinit var downloadButton: ImageButton
    private lateinit var fileName: TextView
    private var workout: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        dateTextView = findViewById(R.id.workoutDateTextView)
        dateButton = findViewById(R.id.chooseDateButton)
        workoutTextView = findViewById(R.id.workoutDetailsTextView)
        workoutStatus = findViewById(R.id.workoutStatusTextView)
        downloadButton = findViewById(R.id.downloadWorkoutImageButton)
        fileName = findViewById(R.id.fileNameTextView)

        var myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }

        dateButton.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        downloadButton.setOnClickListener{
            var fileNameStr = fileName.text.toString() + ".txt"
            var externalFile = File(getExternalFilesDir("Workouts"), fileNameStr)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(externalFile)
                fos.write(workout.toByteArray())
                //fos.write("S-o salvat".toByteArray())
                Toast.makeText(this, "Saved to " + getFilesDir() + "/" + fileNameStr, Toast.LENGTH_LONG).show()
            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }catch (e: IOException){
                e.printStackTrace()
            }finally {
                if (fos != null){
                    try {
                        fos.close()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateLable(myCalendar: Calendar){
        val myFormat = "dd-MMMM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateTextView.setText(sdf.format(myCalendar.time))

        workout = ""
        val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
        var connection = rabbitServer.getConnection()
        var channel = rabbitServer.getChannel()
        if(channel != null){
            val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                workout = message
                println(" [x] Received '$message'")
            }
            channel.basicPublish("", "finishSending", null, dateTextView.text.toString().toByteArray())
            Thread.sleep(2000L)

            channel.basicConsume("repCountResult", true, deliverCallback, CancelCallback { consumerTag: String? -> })
            println("Ma execut")
        }else{
            Log.d("myTag", "Channel is null");
        }
        while (workout == ""){}
        connection?.close()

        var workoutList: List<String> = workout.lines()
        var workoutText = ""
        var timeSpend = 0
        var caloriesBurned = 0.0
        if (!workoutList[0].equals("[]")){
            val typesString = workoutList[0].substring(1, workoutList[0].length-1).replace("\\s".toRegex(), "")
            val repsString = workoutList[1].substring(1, workoutList[1].length-1).replace("\\s".toRegex(), "")
            val weightString = workoutList[2].substring(1, workoutList[2].length-1).replace("\\s".toRegex(), "")
            val timeString = workoutList[3].substring(1, workoutList[3].length-1).replace("\\s".toRegex(), "")
            val types = listOf(*typesString.split(",").toTypedArray())
            val repsStr: List<String> = listOf(*repsString.split(",").toTypedArray())
            val reps = repsStr.map { it.toFloat() }.toTypedArray()
            val weightStr: List<String> = listOf(*weightString.split(",").toTypedArray())
            val weight = weightStr.map { it.toFloat() }.toTypedArray()
            val timeStr: List<String> = listOf(*timeString.split(",").toTypedArray())
            val time = timeStr.map { it.toFloat() }.toTypedArray()
            var newTypes: ArrayList<String> = ArrayList<String>()
            for(type in types){
                var newType = type.removeRange(0,2)
                newType = newType.removeRange(newType.length-1, newType.length)
                newTypes.add(newType)
            }
            for(i in 0..types.size-1){
                timeSpend += time[i].toInt()
                workoutText = workoutText + "Exercise" + (i+1) + ": " + newTypes[i] + "\n" + "Number of repetitions: " + reps[i].toInt() + "\n" + "Weight used: " + weight[i].toInt() + "\n" + "Time spend: " + time[i].toInt() + "\n"
                caloriesBurned += findBurnedCalories(newTypes[i], time[i].toInt())
            }
        }else{
            workoutText = "No workout found on specified date"
        }
        caloriesBurned = String.format("%.1f", caloriesBurned).toDouble()
        workoutTextView.setText(workoutText)
        workoutStatus.setText("Calories Burned: " + caloriesBurned + ", Time Spend: " + timeSpend + "sec")
    }

    fun findBurnedCalories(exName: String, time: Int) : Double{
        var MET = 0
        var caloriesBurned = 0.0
        if(exName == "BenchPress" || exName == "Deadlift"){
            MET = 6
            val sharedPreference = getSharedPreferences("shared", Context.MODE_PRIVATE)
            val weight = sharedPreference.getInt("weight", 0)
            val caloriesBurnedPerMin = MET * 3.5 * weight / 200
            caloriesBurned = time * caloriesBurnedPerMin / 60
        }
        return caloriesBurned
    }
}