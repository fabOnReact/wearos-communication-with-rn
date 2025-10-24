package com.wearconnectivityexample.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataRequest
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WearAssetHandlerTest {

    private lateinit var context: Context
    private lateinit var handler: WearAssetHandler
    private lateinit var createdFiles: MutableList<File>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        createdFiles = mutableListOf()
    }

    @After
    fun tearDown() {
        createdFiles.forEach { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }

    @Test
    fun saveReceivedFile_persistsPayloadAndInvokesCallback() {
        val payload = "wear-data".toByteArray()
        val asset = Asset.createFromBytes(payload)
        val fileName = "received_test.bin"
        val fakeClient = AssetProvidingWearDataClient(payload)
        handler = WearAssetHandler(dataClientProvider = { fakeClient }, targetFileName = { fileName })

        val latch = CountDownLatch(1)
        var savedPath: String? = null

        handler.saveReceivedFile(context, asset) { path ->
            savedPath = path
            latch.countDown()
        }

        assertTrue("Callback should be invoked", latch.await(5, TimeUnit.SECONDS))
        val actualPath = savedPath
        assertTrue("Saved path should not be null", actualPath != null)
        val savedFile = File(actualPath!!)
        createdFiles.add(savedFile)
        assertTrue("File should exist", savedFile.exists())
        assertArrayEquals(payload, savedFile.readBytes())
    }
}

private class AssetProvidingWearDataClient(private val bytes: ByteArray) : WearDataClient {
    override fun putDataItem(request: PutDataRequest): com.google.android.gms.tasks.Task<DataItem> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override fun getFdForAsset(asset: Asset): com.google.android.gms.tasks.Task<DataClient.GetFdForAssetResponse> {
        return Tasks.forResult(object : DataClient.GetFdForAssetResponse {
            override fun getInputStream(): InputStream = ByteArrayInputStream(bytes)

            override fun getParcelFileDescriptor(): android.os.ParcelFileDescriptor? = null
        })
    }
}
