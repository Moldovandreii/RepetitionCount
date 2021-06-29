package com.example.repetitioncounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.*
import com.example.repetitioncounting.models.FoodData
import com.example.repetitioncounting.rabbitMQ.RabbitServer
import com.google.gson.Gson
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class DietActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var foodName: String
    lateinit var quantity: EditText
    lateinit var dietRez: TextView
    private var totalCalories = 0.0
    private var totalProteins = 0.0
    private var totalFats = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        quantity = findViewById<EditText>(R.id.quantityEditText)
        val addFoodButton = findViewById<Button>(R.id.addToDietButton)
        addFoodButton.setOnClickListener(this)
        val viewStatusButton = findViewById<Button>(R.id.ViewStatusButton)
        viewStatusButton.setOnClickListener(this)
        dietRez = findViewById<TextView>(R.id.dietRezTextView)

        searchSpinner()

        getFood()
    }

    private fun searchSpinner()  {
        val searchmethod = ArrayAdapter(this, android.R.layout.simple_spinner_item,foods)
        searchmethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val searchSpinner = findViewById<SearchableSpinner>(R.id.diet_spinner)
        searchSpinner!!.adapter = searchmethod
        searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                foodName = foods[position]
            }

        }
    }

    private var foods = arrayOf(
        "Search Food",
        "Bread",
        "Fish",
        "Rice",
        "Avocado"
    )

    private fun getFood(){
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.UK)
        var currentDate = sdf.format(Date())
        currentDate = "Diet$currentDate"
        var diet = ""
        val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
        var connection = rabbitServer.getConnection()
        var channel = rabbitServer.getChannel()
        if(channel != null){
            val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                diet = message
                println(" [x] Received '$message'")
            }
            channel.basicPublish("", "finishSending", null, currentDate.toByteArray())
            Thread.sleep(2000L)

            channel.basicConsume("repCountResult", true, deliverCallback, CancelCallback { consumerTag: String? -> })
            println("Ma execut")
        }else{
            Log.d("myTag", "Channel is null");
        }
        while (diet == ""){}
        connection?.close()

        var dietList: List<String> = diet.lines()
        var dietText = ""
        if (!dietList[0].equals("[]")){
            val namesString = dietList[0].substring(1, dietList[0].length-1).replace("\\s".toRegex(), "")
            val quantityString = dietList[1].substring(1, dietList[1].length-1).replace("\\s".toRegex(), "")
            val caloriesString = dietList[2].substring(1, dietList[2].length-1).replace("\\s".toRegex(), "")
            val proteinsString = dietList[3].substring(1, dietList[3].length-1).replace("\\s".toRegex(), "")
            val fatsString = dietList[4].substring(1, dietList[4].length-1).replace("\\s".toRegex(), "")
            val names = listOf(*namesString.split(",").toTypedArray())
            val quantityStr: List<String> = listOf(*quantityString.split(",").toTypedArray())
            val quantity = quantityStr.map { it.toFloat() }.toTypedArray()
            val caloriesStr: List<String> = listOf(*caloriesString.split(",").toTypedArray())
            val calories = caloriesStr.map { it.toFloat() }.toTypedArray()
            val proteinsStr: List<String> = listOf(*proteinsString.split(",").toTypedArray())
            val proteins = proteinsStr.map { it.toFloat() }.toTypedArray()
            val fatsStr: List<String> = listOf(*fatsString.split(",").toTypedArray())
            val fats = fatsStr.map { it.toFloat() }.toTypedArray()
            var newNames: ArrayList<String> = ArrayList<String>()
            for(name in names){
                var newName = name.removeRange(0,2)
                newName = newName.removeRange(newName.length-1, newName.length)
                newNames.add(newName)
            }
            for(i in 0..names.size-1){
                totalCalories += calories[i]
                totalProteins += proteins[i]
                totalFats += fats[i]
                dietText = dietText + "Food" + (i+1) + " - Name: " + newNames[i] + ", Quantity: " + quantity[i] + ", Calories: " + calories[i] + ", Proteins: " + proteins[i] + ", Fats: " + fats[i] + "\n"
            }
        }else{
            dietText = "No food added today."
        }
        dietRez.text = dietText
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.addToDietButton){
            val sdf = SimpleDateFormat("dd MMMM yyyy")
            var currentDate = sdf.format(Date())
            val quant = quantity.text.toString().toInt()
            val foodData = FoodData(foodName, quant, currentDate)

            val rabbitServer =  RabbitServer().defaultExchangeAndQueue()
            var connection = rabbitServer.getConnection()
            var channel = rabbitServer.getChannel()
            if(channel != null){
                val jsonStr = Gson().toJson(foodData)
                channel.basicPublish("", "andreiQueue", null, jsonStr.toByteArray())
            }
            getFood()
        }else{
            val intent = Intent(this@DietActivity, StatusActivity::class.java)
            intent.putExtra("totalCalories", totalCalories)
            intent.putExtra("totalProteins", totalProteins)
            intent.putExtra("totalFats", totalFats)
            startActivity(intent)
        }
    }
}