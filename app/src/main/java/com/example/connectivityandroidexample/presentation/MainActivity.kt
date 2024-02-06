/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.connectivityandroidexample.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Nullable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutionException


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {
    private var nodes: MutableList<Node>? = null
    var count by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)
        lifecycleScope.launch(Dispatchers.Main) {
            nodes = getNodes();
            if (nodes!!.size > 0) {
                for (node in nodes) {
                    if (nodes != null) {
                        sendMessageToClient(node)
                    }
                }
            }
        }

        setContent {
            WearApp(currentCount = count, { increaseCount() })
        }
    }

    suspend fun getNodes(): MutableList<Node>? {
        val nodeClient = Wearable.getNodeClient(this);
        try {
            val result = nodeClient.getConnectedNodes().await();
            return result;
        } catch (e: ExecutionException) {
            // The Task failed, this is the same exception you'd get in a non-blocking
            // failure handler.
            // ...
            Log.w("error: ", e);
        } catch (e: InterruptedException) {
            // An interrupt occurred while waiting for the task to complete.
            // ...
            Log.w("error: ", e);
        }
        return TODO("Provide the return value")
    }

    private fun onResult(result: MutableList<Node>?) {
        Log.w("TESTING: ", "result: " + result);
    }

    public override fun onStart() {
        super.onStart()
        // mobileDeviceListener.start()
    }

    public override fun onStop() {
        super.onStop()
        // mobileDeviceListener.stop()
        // manage other components that need to respond
        // to the activity lifecycle
    }

    fun increaseCount() {
        count++;
    }

    fun sendMessageToClient(node: Node) {
        try {
            val sendTask = Wearable.getMessageClient(applicationContext).sendMessage(
                node.getId(), "/increase_wear_counter", null
            )
            val onSuccessListener: OnSuccessListener<Any> =
                OnSuccessListener { Log.w("TESTING from wear: ", "onSuccess") }
            sendTask.addOnSuccessListener(onSuccessListener)
            val onFailureListener =
                OnFailureListener { e -> Log.w("TESTING from wear: ", "onFailure with e: $e") }
            sendTask.addOnFailureListener(onFailureListener)
        } catch (e: Exception) {
            Log.w("TESTING from wear: ", "e $e")
        }
    }

    fun onConnected(@Nullable bundle: Bundle?) {
        Wearable.getMessageClient(this).addListener(this)
    }


    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.getPath().equals("/increase_wear_counter")) {
            count = count + 1;
        }
    }
}