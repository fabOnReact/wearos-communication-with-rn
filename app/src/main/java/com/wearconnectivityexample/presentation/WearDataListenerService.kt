package com.wearconnectivityexample.presentation

import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import java.io.File

class WearDataListenerService : WearableListenerService() {
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
				val dataClient = Wearable.getDataClient(this)
				val task = dataClient.getFdForAsset(asset)

				task.addOnSuccessListener { response ->
						response.inputStream.use { inputStream ->
								val file = File(filesDir, "received_file.jpg")
								file.outputStream().use { outputStream ->
										inputStream.copyTo(outputStream)
								}
						}
				}.addOnFailureListener { e ->
						Log.e("WearOS", "File transfer failed", e)
				}
		}
}
