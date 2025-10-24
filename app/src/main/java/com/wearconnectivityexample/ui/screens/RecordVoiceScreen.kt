package com.example.wearconnectivityexample.ui.screens

import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import java.io.File
import java.io.IOException
import androidx.wear.compose.material.Icon
import com.wearconnectivityexample.R
import com.fabonreact.wearconnectivity.WearFileTransferClient

@Composable
fun RecordVoiceScreen(
    onStopRecording: () -> Unit
) {
    val context = LocalContext.current
    val fileTransferClient = remember { WearFileTransferClient(context) }

    // State to track whether recording is in progress.
    var isRecording by remember { mutableStateOf(false) }
    // Hold a reference to the MediaRecorder.
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    // Define the output file path; we use internal storage.
    val outputFile = remember { "${context.filesDir.absolutePath}/voice_message.mp3" }

    // Function to start recording.
    fun startRecording() {
        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
                start()
                Log.i("RecordVoiceScreen", "Recording started")
            } catch (e: IOException) {
                Log.e("RecordVoiceScreen", "prepare() failed", e)
            }
        }
        mediaRecorder = recorder
        isRecording = true
    }

    // Function to stop recording and send the voice message.
    fun stopRecordingAndSend() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false

            val file = File(outputFile)
            val metadata = buildMap {
                put("fileName", file.name)
                put("fileType", file.extension)
            }

            fileTransferClient.sendFile(file, metadata)
                .addOnSuccessListener { dataItem ->
                    Log.i("RecordVoiceScreen", "Voice message sent successfully: $dataItem")
                }
                .addOnFailureListener { e ->
                    Log.e("RecordVoiceScreen", "Failed to send voice message", e)
                }
            onStopRecording()
        } catch (e: Exception) {
            Log.e("RecordVoiceScreen", "Error stopping recorder", e)
        }
    }

    val onRecordCallback = if (isRecording) ::stopRecordingAndSend else ::startRecording
    // UI: display a toggle button to start/stop recording.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RecordComponent(onRecordClicked = onRecordCallback , checked = isRecording)
    }
}

@Composable
fun RecordComponent(onRecordClicked: () -> Unit, checked: Boolean) {
    val title = if (checked) "Stop Recording" else "Record Voice"
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 16.sp,
                text = title,
            )
            ToggleButton(
                checked = checked,
                onCheckedChange = { onRecordClicked() },
                enabled = true,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = "airplane",
                    modifier =
                    Modifier
                        .size(ToggleButtonDefaults.DefaultIconSize)
                        .wrapContentSize(align = Alignment.Center),
                )
            }
        }
    }
}