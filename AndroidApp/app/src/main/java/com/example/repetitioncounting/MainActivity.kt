
package com.example.repetitioncounting

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.repetitioncounting.models.SensorData
import com.example.repetitioncounting.models.SensorDataTrain
import com.example.repetitioncounting.rabbitMQ.RabbitServer
import com.google.gson.Gson
import com.rabbitmq.client.*
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), SensorEventListener, View.OnClickListener{

    lateinit var sensorManager : SensorManager
    private var channel: Channel? = null
    private var connection: Connection? = null
    private var chosenExercise: String = "none"
    private var descriptionId = 0

    override fun onSensorChanged(event: SensorEvent?) {
//        var accelerometerData = findViewById<TextView>(R.id.accelerometerDataTextView)
//        accelerometerData.text = "x = ${event!!.values[0]}\n\n" +
//                                 "y = ${event.values[1]}\n\n"  +
//                                 "z = ${event.values[2]}\n\n"

        val acc_x = event!!.values[0]
        val acc_y = event.values[1]
        val acc_z = event.values[2]

        val timestamp = java.util.Calendar.getInstance()
        val type = chosenExercise
        val weight = descriptionId
        val sensorData = SensorData(acc_x, acc_y, acc_z, timestamp.timeInMillis, type, weight)
        //val sensorData = SensorDataTrain(acc_x, acc_y, acc_z, timestamp.timeInMillis, type, descriptionId)

        val channel = this.channel
        if(channel != null){
            val jsonStr = Gson().toJson(sensorData)
            channel.basicPublish("", "andreiQueue", null, jsonStr.toByteArray())
//            Log.d("myTag", sensorData.toString());
        }else{
//            Log.d("myTag", "Channel is null");
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendDataButton = findViewById<Button>(R.id.gatherDataButton)
        sendDataButton.setOnClickListener(this)
        val stopDataButton = findViewById<Button>(R.id.stopDataButton)
        stopDataButton.setOnClickListener(this)
        val saveInfo = findViewById<ImageButton>(R.id.downloadWorkoutImageButton)
        saveInfo.setOnClickListener(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        )

        val intent: Intent = intent
        chosenExercise = intent.getStringExtra("ex") as String

        val descId = findViewById<EditText>(R.id.descriptionIdEditText)
        descId.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = descId.text.toString()
                if(text != ""){
                    descriptionId = descId.text.toString().toInt()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onClick(v: View?) {
        var repCount = findViewById<TextView>(R.id.repCountTextView)
        if(v!!.id == R.id.gatherDataButton){
            val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
            this.connection = rabbitServer.getConnection()
            this.channel = rabbitServer.getChannel()
            var status = findViewById<TextView>(R.id.statusTextView)
            status.text = "Exercising"
            status.setTextColor(getColor(R.color.myGreen))
            repCount.text = "Press stop button after you finish"
            repCount.setTextColor(getColor(R.color.MyInfo))
        }else if (v!!.id == R.id.stopDataButton){
            val channel = this.channel
            if(channel != null){
                repCount.setTextColor(getColor(R.color.MyRez))
                val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, UTF_8)
                    println(" [x] Received '$message'")
                    repCount.text = message
                }

                channel.basicPublish("", "finishSending", null, "Done sending".toByteArray())
                Thread.sleep(2000L)

                channel.basicConsume("repCountResult", true, deliverCallback, CancelCallback { consumerTag: String? -> })
                println("Ma execut")
            }else{
                Log.d("myTag", "Channel is null");
            }
            this.connection?.close()
            this.connection = null
            this.channel = null
            var status = findViewById<TextView>(R.id.statusTextView)
            status.text = "Resting"

            status.setTextColor(getColor(R.color.MyRed))
        } else{
            var workout = ""
            val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
            var connection = rabbitServer.getConnection()
            var channel = rabbitServer.getChannel()
            if(channel != null){
                val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                    val message = String(delivery.body, UTF_8)
                    workout = message
                    println(" [x] Received '$message'")
                }

                channel.basicPublish("", "finishSending", null, "Workout done".toByteArray())
                Thread.sleep(2000L)

                channel.basicConsume("repCountResult", true, deliverCallback, CancelCallback { consumerTag: String? -> })
                println("Ma execut")
            }else{
                Log.d("myTag", "Channel is null");
            }
            connection?.close()
        }
    }

}

