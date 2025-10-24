package com.wearconnectivityexample.service

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fabonreact.wearconnectivity.WearFileTransferClient
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.wearconnectivityexample.data.FileState
import java.io.File

class WearDataListenerService : WearableListenerService() {

    private val fileTransferClient by lazy { WearFileTransferClient(this) }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/file_transfer") {
                    val asset = DataMapItem.fromDataItem(dataItem).dataMap.getAsset("file")
                    if (asset != null) {
                        saveReceivedFile(asset)
                    }
                }
            }
        }
    }

    fun saveReceivedFile(asset: Asset) {
        val targetFile = File(filesDir, "received_file.jpg")
        fileTransferClient.saveAsset(
            asset = asset,
            outputFile = targetFile,
            onSuccess = { file ->
                Handler(Looper.getMainLooper()).post {
                    FileState.imagePath = file.absolutePath
                }
                Log.w(TAG, "File transfer successful")
            },
            onFailure = { error ->
                Log.e(TAG, "File transfer failed", error)
            }
        )
    }

    private companion object {
        private const val TAG = "WearOS"
    }
}
