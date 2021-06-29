package com.example.repetitioncounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class HomeView : AppCompatActivity(), View.OnClickListener {

    lateinit var exercise: Spinner
    lateinit var result: TextView
    private var chosenExercise: String = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        val chooseExButton = findViewById<Button>(R.id.chooseEcerciseButton)
        chooseExButton.setOnClickListener(this)
        val graphButton = findViewById<ImageButton>(R.id.graphImageButton)
        graphButton.setOnClickListener(this)
        val workoutInfoButton = findViewById<Button>(R.id.workoutInfoButton)
        workoutInfoButton.setOnClickListener(this)
        val exerciseInfoButton = findViewById<ImageButton>(R.id.exInfoImageButton)
        exerciseInfoButton.setOnClickListener(this)
        val addFoodButton = findViewById<Button>(R.id.addFoodButton)
        addFoodButton.setOnClickListener(this)

        exercise = findViewById<Spinner>(R.id.exercisesSpinner)
        result = findViewById<TextView>(R.id.exerciseTextView)
        result.text = "Please choose an exercise then press select"

        val exercises = arrayOf("Bench Press", "Pull Up", "Deadlift", "Biceps Curl", "Ab Crunches", "Skullcrushers", "Squats", "Military Press", "Lateral Raises", "Seated Rows", "Triceps Pushdowns", "Lat Pulldowns", "Overhead Extension", "Dumbbell Pullover")
        exercise.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, exercises)

        exercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenExercise = exercises[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                result.text = "Please choose an exercise then press select"
            }
        }
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.chooseEcerciseButton){
            val intent = Intent(this@HomeView, MainActivity::class.java)
            intent.putExtra("ex", chosenExercise)
            startActivity(intent)
        }else if(v!!.id == R.id.graphImageButton){
            val intent = Intent(this@HomeView, GraphsActivity::class.java)
            intent.putExtra("ex", chosenExercise)
            startActivity(intent)
        }else if(v!!.id == R.id.workoutInfoButton){
            val intent = Intent(this@HomeView, WorkoutInfoActivity::class.java)
            startActivity(intent)
        }else if(v!!.id == R.id.exInfoImageButton){
            val intent = Intent(this@HomeView, ExerciseInfoActivity::class.java)
            intent.putExtra("ex", chosenExercise)
            startActivity(intent)
        }else{
            val intent = Intent(this@HomeView, DietActivity::class.java)
            startActivity(intent)
        }
    }
}