package com.fabonreact.wearconnectivity

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Handles file transfers between the Wear OS device and the paired Android application by wrapping
 * the Data Layer API.
 */
class WearFileTransferClient(context: Context) {

    private val applicationContext = context.applicationContext
    private val dataClient = Wearable.getDataClient(applicationContext)

    /**
     * Sends [file] to the connected device. Additional metadata can be provided to mirror the
     * structure used by the React Native companion library.
     */
    fun sendFile(
        file: File,
        metadata: Map<String, String> = emptyMap(),
        path: String = DEFAULT_FILE_TRANSFER_PATH
    ): Task<DataItem> {
        val asset = try {
            createAssetFromFile(file)
        } catch (error: IOException) {
            return Tasks.forException(error)
        }

        val request = PutDataMapRequest.create(path).apply {
            dataMap.putAsset(FILE_KEY, asset)
            if (metadata.isNotEmpty()) {
                dataMap.putDataMap(METADATA_KEY, metadata.toDataMap())
            }
            dataMap.putLong(TIMESTAMP_KEY, System.currentTimeMillis())
        }.asPutDataRequest()

        return dataClient.putDataItem(request)
    }

    /**
     * Saves the contents of [asset] into [outputFile]. Results are delivered through the
     * [onSuccess] and [onFailure] callbacks.
     */
    fun saveAsset(
        asset: Asset,
        outputFile: File,
        onSuccess: (File) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        dataClient.getFdForAsset(asset)
            .addOnSuccessListener { response ->
                try {
                    response.inputStream.use { inputStream ->
                        FileOutputStream(outputFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    onSuccess(outputFile)
                } catch (error: IOException) {
                    onFailure(error)
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    private fun createAssetFromFile(file: File): Asset {
        FileInputStream(file).use { inputStream ->
            val bytes = inputStream.readBytes()
            return Asset.createFromBytes(bytes)
        }
    }

    private fun Map<String, String>.toDataMap(): DataMap = DataMap().also { map ->
        forEach { (key, value) ->
            map.putString(key, value)
        }
    }

    companion object {
        const val DEFAULT_FILE_TRANSFER_PATH = "/file_transfer"
        private const val FILE_KEY = "file"
        private const val METADATA_KEY = "metadata"
        private const val TIMESTAMP_KEY = "timestamp"
    }
}
