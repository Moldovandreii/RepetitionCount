package com.example.repetitioncounting

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.repetitioncounting.models.SensorData
import com.example.repetitioncounting.rabbitMQ.RabbitServer
import com.google.gson.Gson
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), SensorEventListener, View.OnClickListener{

    lateinit var sensorManager : SensorManager
    private var channel: Channel? = null
    private var connection: Connection? = null

    override fun onSensorChanged(event: SensorEvent?) {
        var accelerometerData = findViewById<TextView>(R.id.accelerometerDataTextView)
        accelerometerData.text = "x = ${event!!.values[0]}\n\n" +
                                 "y = ${event.values[1]}\n\n"  +
                                 "z = ${event.values[2]}\n\n"

        val acc_x = event.values[0]
        val acc_y = event.values[1]
        val acc_z = event.values[2]
        val timestamp = java.util.Calendar.getInstance()
        val sensorData = SensorData(acc_x, acc_y, acc_z, timestamp.timeInMillis)

        val channel = this.channel
        if(channel != null){
            val jsonStr = Gson().toJson(sensorData)
            channel.basicPublish("", "andreiQueue", null, jsonStr.toByteArray())
            //channel.basicPublish("", "andreiQueue", null, "This is my string".toByteArray())
            Log.d("myTag", sensorData.toString());
        }else{
            Log.d("myTag", "Channel is null");
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
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.gatherDataButton){
            val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
            this.connection = rabbitServer.getConnection()
            this.channel = rabbitServer.getChannel()

            /*var connection = RabbitServer().getFactory().newConnection()
            this.channel = connection.createChannel()
            this.connection = connection*/
        }else{
            /*var connection = this.connection
            var channel = this.channel
            connection?.close()
            channel?.close()*/
            this.connection = null
            this.channel = null
        }
    }

    
}

