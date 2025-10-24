package com.wearconnectivityexample.service

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataItemAsset
import com.google.android.gms.wearable.PutDataRequest
import com.wearconnectivityexample.ui.screens.createVoiceMessageDataMap
import com.wearconnectivityexample.ui.screens.sendVoiceMessage
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VoiceMessageCommunicationTest {

    private lateinit var context: Context
    private lateinit var tempFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        tempFile = File.createTempFile("voice", ".mp3", context.cacheDir)
        tempFile.writeText("sample-data")
    }

    @After
    fun tearDown() {
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun createVoiceMessageDataMap_containsMetadataAndAsset() {
        val timestamp = 1_234L

        val request = createVoiceMessageDataMap(tempFile) { timestamp }

        val dataMap = request.dataMap
        val metadata = dataMap.getDataMap("metadata")
        assertNotNull("Metadata should be present", metadata)
        assertEquals(tempFile.name, metadata?.getString("fileName"))
        assertEquals(tempFile.extension, metadata?.getString("fileType"))
        assertNotNull("Asset should be attached", dataMap.getAsset("file"))
        assertEquals(timestamp, dataMap.getLong("timestamp"))
    }

    @Test
    fun sendVoiceMessage_dispatchesRequestToClient() {
        val fakeClient = RecordingWearDataClient()

        sendVoiceMessage(context, tempFile.absolutePath, fakeClient) { 9_876L }

        val request = fakeClient.lastRequest
        assertNotNull("PutDataRequest should be created", request)
        assertEquals("/file_transfer", request?.uri?.path)
    }
}

private class RecordingWearDataClient : WearDataClient {
    var lastRequest: PutDataRequest? = null

    override fun putDataItem(request: PutDataRequest): com.google.android.gms.tasks.Task<DataItem> {
        lastRequest = request
        return Tasks.forResult(FakeDataItem(request.uri))
    }

    override fun getFdForAsset(asset: Asset): com.google.android.gms.tasks.Task<DataClient.GetFdForAssetResponse> {
        throw UnsupportedOperationException("Not needed for this test")
    }
}

private class FakeDataItem(private val uri: Uri) : DataItem {
    private var data: ByteArray? = null

    override fun getUri(): Uri = uri

    override fun setData(data: ByteArray?): DataItem {
        this.data = data
        return this
    }

    override fun getData(): ByteArray? = data

    override fun getAssets(): MutableMap<String, DataItemAsset> = mutableMapOf()
}
