package com.wearconnectivityexample.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.wearconnectivityexample.ui.components.ImageViewer
import com.wearconnectivityexample.data.FileState
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@Composable
fun ImageScreen() {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        ImageViewer()
        Button(
            onClick = { sendImageFile(context) },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
        ) {
            Text("Send File")
        }
    }
}

fun sendImageFile(context: Context) {
    val path = FileState.imagePath ?: return
    val file = File(path)
    if (!file.exists()) return

    val asset = try {
        FileInputStream(file).use { fis ->
            val bytes = ByteArray(file.length().toInt())
            fis.read(bytes)
            Asset.createFromBytes(bytes)
        }
    } catch (e: IOException) {
        Log.e("ImageScreen", "Error creating asset", e)
        return
    }

    val dataMapRequest = PutDataMapRequest.create("/file_transfer")
    dataMapRequest.dataMap.putAsset("file", asset)
    val metadata = DataMap().apply {
        putString("fileName", file.name)
        putString("fileType", file.extension)
    }
    dataMapRequest.dataMap.putDataMap("metadata", metadata)
    dataMapRequest.dataMap.putLong("timestamp", System.currentTimeMillis())

    val request = dataMapRequest.asPutDataRequest()
    Wearable.getDataClient(context).putDataItem(request)
        .addOnSuccessListener { dataItem ->
            Log.i("ImageScreen", "Image sent successfully: $dataItem")
        }
        .addOnFailureListener { e ->
            Log.e("ImageScreen", "Failed to send image", e)
        }
}
