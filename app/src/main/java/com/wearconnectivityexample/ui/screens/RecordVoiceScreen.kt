package com.example.wearconnectivityexample.ui.screens

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.wearconnectivityexample.R
import com.wearconnectivityexample.service.WearDataClient
import com.wearconnectivityexample.service.defaultWearDataClient
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@Composable
fun RecordVoiceScreen(
    onStopRecording: () -> Unit,
    recorderProvider: () -> MediaRecorder = ::MediaRecorder,
    messageSender: (Context, String) -> Unit = { context, filePath ->
        sendVoiceMessage(context, filePath)
    }
) {
    val context = LocalContext.current

    // State to track whether recording is in progress.
    var isRecording by remember { mutableStateOf(false) }
    // Hold a reference to the MediaRecorder.
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    // Define the output file path; we use internal storage.
    val outputFile = remember { "${context.filesDir.absolutePath}/voice_message.mp3" }

    // Function to start recording.
    fun startRecording() {
        val recorder = recorderProvider().apply {
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
            messageSender(context, outputFile)
            onStopRecording();
        } catch (e: Exception) {
            Log.e("RecordVoiceScreen", "Error stopping recorder", e)
        }
    }

    val onRecordCallback = if (isRecording) ::stopRecordingAndSend else ::startRecording
    // UI: display a Chip that acts as a toggle button.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RecordComponent(onRecordClicked = onRecordCallback , checked = isRecording)
    }
}


/**
 * Sends the voice message file using the DataClient API.
 */
fun sendVoiceMessage(
    context: Context,
    filePath: String,
    dataClient: WearDataClient = defaultWearDataClient(context),
    timestampProvider: () -> Long = System::currentTimeMillis
) {
    val file = File(filePath)
    val dataMapRequest = try {
        createVoiceMessageDataMap(file, timestampProvider)
    } catch (ioException: IOException) {
        Log.e("RecordVoiceScreen", "Failed to prepare voice message asset", ioException)
        return
    }
    val request = dataMapRequest.asPutDataRequest()
    dataClient.putDataItem(request)
        .addOnSuccessListener { dataItem ->
            Log.i("RecordVoiceScreen", "Voice message sent successfully: $dataItem")
        }
        .addOnFailureListener { e ->
            Log.e("RecordVoiceScreen", "Failed to send voice message", e)
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

fun createVoiceMessageDataMap(
    file: File,
    timestampProvider: () -> Long
): PutDataMapRequest {
    val fileName = file.name
    val fileExtension = file.extension
    val asset = try {
        FileInputStream(file).use { fis ->
            val bytes = ByteArray(file.length().toInt())
            fis.read(bytes)
            Asset.createFromBytes(bytes)
        }
    } catch (e: IOException) {
        Log.e("RecordVoiceScreen", "Error creating asset", e)
        throw e
    }
    return PutDataMapRequest.create("/file_transfer").apply {
        dataMap.putAsset("file", asset)
        val metadata = DataMap().apply {
            putString("fileName", fileName)
            putString("fileType", fileExtension)
        }
        dataMap.putDataMap("metadata", metadata)
        dataMap.putLong("timestamp", timestampProvider())
    }
}
