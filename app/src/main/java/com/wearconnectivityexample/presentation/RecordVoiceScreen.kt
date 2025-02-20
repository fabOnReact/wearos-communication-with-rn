import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ToggleButton
import com.wearconnectivityexample.R

@Composable
fun RecordVoiceScreen() {
    val context = LocalContext.current

    // State to track whether recording is in progress.
    var isRecording by remember { mutableStateOf(false) }
    // Hold a reference to the MediaRecorder.
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    // Define the output file path; we use internal storage.
    val outputFile = remember { "${context.filesDir.absolutePath}/voice_message.mp4" }

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
            sendVoiceMessage(context, outputFile)
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
fun sendVoiceMessage(context: Context, filePath: String) {
    val file = File(filePath)
    val dataClient = Wearable.getDataClient(context)
    val asset = try {
        FileInputStream(file).use { fis ->
            val bytes = ByteArray(file.length().toInt())
            fis.read(bytes)
            Asset.createFromBytes(bytes)
        }
    } catch (e: IOException) {
        Log.e("RecordVoiceScreen", "Error creating asset", e)
        return
    }
    val dataMapRequest = PutDataMapRequest.create("/voice_transfer")
    dataMapRequest.dataMap.putAsset("voice", asset)
    dataMapRequest.dataMap.putLong("timestamp", System.currentTimeMillis())
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
