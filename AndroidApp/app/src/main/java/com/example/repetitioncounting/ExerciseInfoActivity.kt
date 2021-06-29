package com.example.repetitioncounting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView

class ExerciseInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_info)

        val intent: Intent = intent
        var chosenExercise = intent.getStringExtra("ex") as String

        val exerciseName = findViewById<TextView>(R.id.exerciseInfoTextView)
        exerciseName.text = chosenExercise

        val exerciseDescription = findViewById<TextView>(R.id.exerciseInfoDescTextView)
        val exerciseVideo = findViewById<VideoView>(R.id.exerciseInfoVideoView)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(exerciseVideo)
        var uri = Uri.parse("android.resource://$packageName/${R.raw.benchpress}")

        when(chosenExercise){
            "Bench Press" -> {exerciseDescription.text = "Bench Press Desc"
                              uri = Uri.parse("android.resource://$packageName/${R.raw.benchpress}")}
            "Pull Up" -> {exerciseDescription.text = "Pull Up Desc"
                              uri = Uri.parse("android.resource://$packageName/${R.raw.deadlift}")}
        }


        exerciseVideo.setMediaController(mediaController)
        exerciseVideo.setVideoURI(uri)
        exerciseVideo.requestFocus()
        exerciseVideo.start()
    }
}