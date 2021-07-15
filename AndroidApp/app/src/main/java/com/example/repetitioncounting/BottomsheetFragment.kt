package com.example.repetitioncounting

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.toptoche.searchablespinnerlibrary.SearchableSpinner


class BottomsheetFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
        var choosenGoal = sharedPreferences.getString("goal", "Loose Fat")
        var choosenWeight = sharedPreferences.getInt("weight", 80)
        var choosenHeight = sharedPreferences.getInt("height", 180)
        var choosenGender = sharedPreferences.getString("gender", "Male")
        var choosenAge = sharedPreferences.getInt("age", 20)
        var choosenActivity = sharedPreferences.getString("activity", "Sedentary")
        var choosenDiet = sharedPreferences.getString("diet", "None")
        var rootView: View = inflater.inflate(R.layout.bottomsheet_layout, container, false)
        val goalsSpinner = rootView.findViewById<Spinner>(R.id.goalsSpinner)
        val genderSpinner = rootView.findViewById<Spinner>(R.id.genderSpinner)
        val activitySpinner = rootView.findViewById<Spinner>(R.id.activitySpinner)
        val dietSpinner = rootView.findViewById<Spinner>(R.id.dietSpinner)
        var weight = rootView.findViewById<EditText>(R.id.weightEditText)
        var height = rootView.findViewById<EditText>(R.id.heightEditText)
        var age = rootView.findViewById<EditText>(R.id.ageEditText)
        weight.setText(choosenWeight.toString())
        height.setText(choosenHeight.toString())
        age.setText(choosenAge.toString())
        rootView.findViewById<Button>(R.id.saveButton).setOnClickListener(){
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putInt("weight", weight.text.toString().toInt())
                putInt("height", height.text.toString().toInt())
                putInt("age", age.text.toString().toInt())
                putString("gender", choosenGender)
                putString("goal", choosenGoal)
                putString("activity", choosenActivity)
                putString("diet", choosenDiet)
            }.apply()
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show()
        }
        rootView.findViewById<Button>(R.id.backButton).setOnClickListener(){
            dismiss()
            Toast.makeText(requireContext(), "Tap on the screen for update", Toast.LENGTH_LONG).show()
        }

        var goals = arrayOf("Loose Fat", "Maintain", "Build Muscle")
        goalsSpinner.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, goals)
        goalsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                choosenGoal = goals[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
                var goalT = sharedPreferences.getString("goal", "")
                if (goalT != null) {
                    choosenGoal = goalT
                }
            }
        }

        var genders = arrayOf("Male", "Female")
        genderSpinner.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, genders)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
                var goalT = sharedPreferences.getString("gender", "")
                if (goalT != null) {
                    choosenGoal = goalT
                }
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                choosenGender = genders[position]
            }
        }

        var activities = arrayOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extra Active", "Professional Athlete")
        activitySpinner.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, activities)
        activitySpinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                choosenActivity = activities[position]
            }
        }

        var diets = arrayOf("None", "Low Carbs", "High Carbs", "Keto", "Paleo")
        dietSpinner.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_expandable_list_item_1, diets)
        dietSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                choosenDiet = diets[position]
            }
        }

        var genderAdapter = genderSpinner.adapter as ArrayAdapter<String>
        var genderPos = genderAdapter.getPosition(choosenGender)
        genderSpinner.setSelection(genderPos)
        var goalAdapter = goalsSpinner.adapter as ArrayAdapter<String>
        var goalPos = goalAdapter.getPosition(choosenGoal)
        goalsSpinner.setSelection(goalPos)
        var activityAdapter = activitySpinner.adapter as ArrayAdapter<String>
        var activityPos = activityAdapter.getPosition(choosenActivity)
        activitySpinner.setSelection(activityPos)
        var dietAdapter = dietSpinner.adapter as ArrayAdapter<String>
        var dietPos = dietAdapter.getPosition(choosenDiet)
        dietSpinner.setSelection(dietPos)

        return rootView
    }
}