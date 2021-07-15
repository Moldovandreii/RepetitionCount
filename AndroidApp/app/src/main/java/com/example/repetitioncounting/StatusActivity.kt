package com.example.repetitioncounting

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

class StatusActivity : AppCompatActivity() {
    private var weight: Int? = 0
    private var height: Int? = 0
    private var age: Int? = 0
    private var goal: String? = ""
    private var gender: String? = ""
    private var activity: String? = ""
    private var diet: String? = ""
    private var caloriesRounded = 0.0
    private var proteinsRounded = 0.0
    private var fatsRounded = 0.0
    private var carbsRounded = 0.0
    lateinit var caloriesText: TextView
    lateinit var proteinsText: TextView
    lateinit var fatsText: TextView
    lateinit var carbsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        caloriesText = findViewById<TextView>(R.id.caloriesTextView)
        proteinsText = findViewById<TextView>(R.id.proteinTextView)
        fatsText = findViewById<TextView>(R.id.fatsTextView)
        carbsText = findViewById<TextView>(R.id.carbsTextView)
        val userInfoButton = findViewById<Button>(R.id.userInfoButton)
        userInfoButton.setOnClickListener{
            val bottomSheetFragment = BottomsheetFragment()
            bottomSheetFragment.show(supportFragmentManager, "customDialog")
        }

        val intent: Intent = intent
        val totalCalories = intent.getDoubleExtra("totalCalories", 0.0)
        val totalProteins = intent.getDoubleExtra("totalProteins", 0.0)
        val totalFats = intent.getDoubleExtra("totalFats", 0.0)
        val totalCarbs = intent.getDoubleExtra("totalCarbs", 0.0)
        caloriesRounded = String.format("%.2f", totalCalories).toDouble()
        proteinsRounded = String.format("%.2f", totalProteins).toDouble()
        fatsRounded = String.format("%.2f", totalFats).toDouble()
        carbsRounded = String.format("%.2f", totalFats).toDouble()
        caloriesText.text = caloriesRounded.toString()
        proteinsText.text = proteinsRounded.toString()
        fatsText.text = fatsRounded.toString()
        carbsText.text = carbsRounded.toString()

        updateDiet()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        updateDiet()
        return super.onTouchEvent(event)
    }

    fun updateDiet(){
        val sharedPreference = getSharedPreferences("shared", Context.MODE_PRIVATE)
        weight = sharedPreference.getInt("weight", 0)
        height = sharedPreference.getInt("height", 0)
        age = sharedPreference.getInt("age",0)
        goal = sharedPreference.getString("goal", "")
        gender = sharedPreference.getString("gender", "")
        activity = sharedPreference.getString("activity", "")
        diet = sharedPreference.getString("diet", "")

        var BMR = 10 * weight!! + (6.25 * height!!) - (5 * age!!)
        if(gender == "Male"){
            BMR += 5
        }else if(gender == "Female"){
            BMR -= 161
        }
        var HBE = 0.0      //The daily calorie intake recommended for maintaining the current weight = Total Daily Energy Expenditure
        if(activity == "Sedentary"){
            HBE = BMR * 1.2
        }else if(activity == "Lightly Active"){
            HBE = BMR * 1.4
        }else if(activity == "Moderately Active"){
            HBE = BMR * 1.6
        }else if(activity == "Very Active"){
            HBE = BMR * 1.75
        }else if(activity == "Extra Active"){
            HBE = BMR * 2.0
        }else if(activity == "Professional Athlete"){
            HBE = BMR * 2.3
        }
        var finalCalories = 0
        var proteinsPerKg = 0.0
        var fatPercent = 0
        var carbsPercent = 0
        if(goal == "Loose Fat"){
            finalCalories = (HBE!! - 500).toInt()
            fatPercent = 20
            carbsPercent = 40
            if(gender == "Female"){
                proteinsPerKg = 0.7
            }else if(gender == "Male"){
                proteinsPerKg = 0.8
            }
        }else if(goal == "Maintain"){
            finalCalories = HBE.toInt()
            fatPercent = 25
            carbsPercent = 50
            if(gender == "Female"){
                proteinsPerKg = 1.3
            }else if(gender == "Male"){
                proteinsPerKg = 1.4
            }
        }else if(goal == "Build Muscle"){
            finalCalories = (HBE!! + 500).toInt()
            fatPercent = 30
            carbsPercent = 60
            if(gender == "Female"){
                proteinsPerKg = 1.9
            }else if(gender == "Male"){
                proteinsPerKg = 2.0
            }
        }
        var proteinPercent = 0
        if(diet == "High Carbs"){
            fatPercent = 35
            carbsPercent = 45
            proteinPercent = 20
        }else if(diet == "Low Carbs"){
            fatPercent = 35
            carbsPercent = 20
            proteinPercent = 45
        }else if(diet == "Keto"){
            proteinPercent = 25
            fatPercent = 70
            carbsPercent = 5
        }else if(diet == "Paleo"){
            proteinPercent = 30
            fatPercent = 40
            carbsPercent = 30
        }

        var finalProteins = 0.0
        if(proteinPercent == 0){
            finalProteins = weight!! * proteinsPerKg
            finalProteins = String.format("%.2f", finalProteins).toDouble()
        }else{
            finalProteins = (proteinPercent / 100.0) * finalCalories / 4
        }
        var finalFats = (fatPercent / 100.0) * finalCalories / 9
        var finalCarbs = (carbsPercent / 100.0) * finalCalories / 4

        var caloriesBalance = finalCalories - caloriesRounded
        caloriesBalance = String.format("%.2f", caloriesBalance).toDouble()
        var proteinsBalance = finalProteins - proteinsRounded
        proteinsBalance = String.format("%.2f", proteinsBalance).toDouble()
        var fatsBalance = finalFats - fatsRounded
        fatsBalance = String.format("%.2f", fatsBalance).toDouble()
        var carbsBalance = finalCarbs - carbsRounded
        carbsBalance = String.format("%.2f", carbsBalance).toDouble()

        var caloriesBalanceTxt = ""
        if(caloriesBalance > 0){
            caloriesBalanceTxt = caloriesBalance.toString() + " remaining"
        }else{
            caloriesBalanceTxt = "Surplus of " + caloriesBalance.toString()
        }

        var proteinsBalanceText = ""
        if(proteinsBalance > 0){
            proteinsBalanceText = proteinsBalance.toString() + "g remaining"
        }else{
            proteinsBalanceText = "Surplus of " + proteinsBalance.toString()
        }

        var fatsBalanceTxt = ""
        if(fatsBalance > 0){
            fatsBalanceTxt = fatsBalance.toString() + "g remaining"
        }else{
            fatsBalanceTxt = "Surplus of " + fatsBalance.toString()
        }

        var carbsBalanceTxt = ""
        if(carbsBalance > 0){
            carbsBalanceTxt = carbsBalance.toString() + "g remaining"
        }else{
            carbsBalanceTxt = "Surplus of  " + carbsBalance.toString()
        }

        caloriesText.text = "Calories: " + caloriesRounded.toString() + " - " + caloriesBalanceTxt
        proteinsText.text = "Proteins: " + proteinsRounded.toString() + "g - " + proteinsBalanceText
        fatsText.text = "Fats: " + fatsRounded.toString() + "g - " + fatsBalanceTxt
        carbsText.text = "Carbs: " + carbsRounded.toString() + "g - " + carbsBalanceTxt
    }
}