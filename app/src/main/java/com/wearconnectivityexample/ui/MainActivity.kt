package com.example.wearconnectivityexample.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }

    public override fun onStart() {
        super.onStart()
        requestAudioPermission(this)
    }
}

fun requestAudioPermission(activity: Activity) {
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
    }
}