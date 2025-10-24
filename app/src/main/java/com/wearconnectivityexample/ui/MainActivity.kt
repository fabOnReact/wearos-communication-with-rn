package com.example.wearconnectivityexample.ui

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fabonreact.wearconnectivity.WearMessagingClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val wearMessagingClient by lazy { WearMessagingClient(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }

    public override fun onStart() {
        super.onStart()
        requestAudioPermission(this)
        lifecycleScope.launch {
            wearMessagingClient.connect()
        }
    }

    public override fun onStop() {
        super.onStop()
        wearMessagingClient.disconnect()
    }
}

fun requestAudioPermission(activity: Activity) {
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
    }
}