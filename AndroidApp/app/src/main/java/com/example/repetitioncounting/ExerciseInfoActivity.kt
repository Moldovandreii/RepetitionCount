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
            "Pull Up" -> {exerciseDescription.text = "The pull up exercise is a compound exercise which targets mostly the upper part of the back, but also the biceps. Depending on its execution, it can target different parts of the upper back. If the exercise is performed with a wide grip, the inner part of the back is mostly targeted, whereas if the grip is narrower, the outer part will be more engaged. It is recommend to perform this exercises at the beginning of the workout, not only because of his high difficulty level, but also because it is a very good warm up exercise. The recommended execution range would be around 3 sets of 8-12 repetitions."
                              uri = Uri.parse("android.resource://$packageName/${R.raw.deadlift}")}
        }


        exerciseVideo.setMediaController(mediaController)
        exerciseVideo.setVideoURI(uri)
        exerciseVideo.requestFocus()
        exerciseVideo.start()
    }
}