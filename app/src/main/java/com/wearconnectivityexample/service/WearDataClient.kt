package com.wearconnectivityexample.service

import android.content.Context
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.tasks.Task

/**
 * Minimal abstraction over [DataClient] to make the wearable communication layer testable.
 */
interface WearDataClient {
    fun putDataItem(request: PutDataRequest): Task<DataItem>
    fun getFdForAsset(asset: Asset): Task<DataClient.GetFdForAssetResponse>
}

class GoogleWearDataClient(private val delegate: DataClient) : WearDataClient {
    override fun putDataItem(request: PutDataRequest): Task<DataItem> = delegate.putDataItem(request)

    override fun getFdForAsset(asset: Asset): Task<DataClient.GetFdForAssetResponse> =
        delegate.getFdForAsset(asset)
}

fun defaultWearDataClient(context: Context): WearDataClient =
    GoogleWearDataClient(Wearable.getDataClient(context))
