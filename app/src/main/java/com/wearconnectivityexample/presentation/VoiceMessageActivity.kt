package com.wearconnectivityexample.presentation

import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.wearconnectivityexample.R
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class VoiceMessageActivity : ComponentActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var outputFile: String
    private lateinit var dataClient: DataClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_message)
        dataClient = Wearable.getDataClient(this)

        // Set up the file path in internal storage.
        outputFile = "${filesDir.absolutePath}/voice_message.mp4"

        val btnRecord = findViewById<Button>(R.id.btnRecord)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnRecord.setOnClickListener {
            startRecording()
        }

        btnStop.setOnClickListener {
            stopRecordingAndSend()
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            // Use a suitable output format and encoder.
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
                start()
                Log.i("VoiceMessage", "Recording started")
            } catch (e: IOException) {
                Log.e("VoiceMessage", "prepare() failed", e)
            }
        }
    }

    private fun stopRecordingAndSend() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            Log.i("VoiceMessage", "Recording stopped")
            sendVoiceMessage(outputFile)
        } catch (e: Exception) {
            Log.e("VoiceMessage", "Error stopping recorder", e)
        }
    }

    private fun sendVoiceMessage(filePath: String) {
        val file = File(filePath)
        val asset = createAssetFromFile(file)
        if (asset == null) {
            Log.w("VoiceMessage", "Failed to create asset from file")
            return
        }
        // Create a DataMapRequest for voice transfer with a designated path.
        val dataMapRequest = PutDataMapRequest.create("/voice_transfer")
        dataMapRequest.dataMap.putAsset("voice", asset)
        dataMapRequest.dataMap.putLong("timestamp", System.currentTimeMillis())
        val request = dataMapRequest.asPutDataRequest()
        dataClient.putDataItem(request)
            .addOnSuccessListener(OnSuccessListener { dataItem ->
                Log.i("VoiceMessage", "Voice message sent successfully: $dataItem")
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.e("VoiceMessage", "Failed to send voice message", e)
            })
    }

    private fun createAssetFromFile(file: File): Asset? {
        return try {
            val inputStream = FileInputStream(file)
            val bytes = ByteArray(file.length().toInt())
            inputStream.read(bytes)
            inputStream.close()
            Asset.createFromBytes(bytes)
        } catch (e: IOException) {
            Log.e("VoiceMessage", "Error reading file", e)
            null
        }
    }
}