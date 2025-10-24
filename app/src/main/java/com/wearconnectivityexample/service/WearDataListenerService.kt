package com.wearconnectivityexample.service

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.wearconnectivityexample.data.FileState
import java.io.File

class WearDataListenerService : WearableListenerService() {
    internal var assetHandler: WearAssetHandler = WearAssetHandler()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/file_transfer") {
                    val asset = DataMapItem.fromDataItem(dataItem).dataMap.getAsset("file")
                    if (asset != null) {
                        assetHandler.saveReceivedFile(this, asset)
                    }
                }
            }
        }
    }
}

class WearAssetHandler(
    private val dataClientProvider: (Context) -> WearDataClient = { defaultWearDataClient(it) },
    private val targetFileName: () -> String = { "received_file.jpg" }
) {
    fun saveReceivedFile(
        context: Context,
        asset: Asset,
        onFileSaved: (String) -> Unit = { savedPath -> FileState.imagePath = savedPath }
    ) {
        val dataClient = dataClientProvider(context)
        val task = dataClient.getFdForAsset(asset)

        task.addOnSuccessListener { response ->
            response.inputStream.use { inputStream ->
                val file = File(context.filesDir, targetFileName())
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                onFileSaved(file.absolutePath)
            }
            Log.w("WearOS", "File transfer successful")
        }.addOnFailureListener { e ->
            Log.e("WearOS", "File transfer failed", e)
        }
    }
}
