/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.wearconnectivityexample.presentation

import ImageViewer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {
    private var nodes: MutableList<Node>? = null
    var count by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)

        setContent {
            WearApp(currentCount = count, { increaseCount() }) { sendMessagesToClient() }
            // ImageViewer()
        }
    }

    public override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            nodes = getNodes();
        }
    }

    fun sendMessagesToClient() {
        if (nodes!!.size > 0) {
            for (node in nodes!!) {
                if (nodes != null) {
                    sendMessageToClient(node)
                }
            }
        }
    }

    public override fun onStop() {
        super.onStop()
        // disconnect from client and disable button
    }

    suspend fun getNodes(): MutableList<Node> {
        val nodeClient = Wearable.getNodeClient(this);
        return nodeClient.getConnectedNodes().await();
    }

    fun increaseCount() {
        count++;
    }

    fun sendMessageToClient(node: Node) {
        val jsonObject = JSONObject().apply {
            put("event", "message")
            put("text", "hello")
        }
        try {
            val sendTask = Wearable.getMessageClient(applicationContext).sendMessage(
                node.getId(), jsonObject.toString(), null
            )
        } catch (e: Exception) {
            Log.w("WearOS: ", "e $e")
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val jsonObject = JSONObject(messageEvent.path)
        val event = jsonObject.getString("event")
        if (event.equals("message")) {
            count = count + 1;
        }
    }
}