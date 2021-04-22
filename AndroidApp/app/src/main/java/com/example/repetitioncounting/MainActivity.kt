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
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.repetitioncounting.models.SensorData
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

    override fun onSensorChanged(event: SensorEvent?) {
        var accelerometerData = findViewById<TextView>(R.id.accelerometerDataTextView)
//        accelerometerData.text = "x = ${event!!.values[0]}\n\n" +
//                                 "y = ${event.values[1]}\n\n"  +
//                                 "z = ${event.values[2]}\n\n"

        val acc_x = event!!.values[0]
        val acc_y = event.values[1]
        val acc_z = event.values[2]

        val timestamp = java.util.Calendar.getInstance()
        val type = chosenExercise
        val sensorData = SensorData(acc_x, acc_y, acc_z, timestamp.timeInMillis, type)

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

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        )

        val intent: Intent = intent
        chosenExercise = intent.getStringExtra("ex") as String
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
        }else{
            val channel = this.channel
            if(channel != null){
                repCount.setTextColor(getColor(R.color.MyRez))
                val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, UTF_8)
                    println(" [x] Received '$message'")
                    repCount.text = message
                }
                channel.basicPublish("", "finishSending", null, "Done sending".toByteArray())
//                repCount.text = "Computing..."
                Thread.sleep(2000L)
//                SystemClock.sleep(10000);

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
        }
    }

}

