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
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutionException


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    // private lateinit var mobileDeviceListener: MobileDeviceListener
    var count by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)
        /*
        mobileDeviceListener = MobileDeviceListener(this) { node ->
            // update UI
        }
         */
        lifecycleScope.launch(Dispatchers.Main) {
            val result = getNodes();
            if (result != null) {
                onResult(result)
            }
        }


        setContent {
            WearApp(currentCount = count, { increaseCount() })
        }
    }

    suspend fun getNodes(): MutableList<Node>? {
        val nodeClient = Wearable.getNodeClient(this);
        try {
            // Block on a task and get the result synchronously. This is generally done
            // when executing a task inside a separately managed background thread. Doing this
            // on the main (UI) thread can cause your application to become unresponsive.
            val result = nodeClient.getConnectedNodes().await();
            Log.w("Testing: ", "result: " + result);
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
        /*if (result.size > 0) {
            for (node in nodes) {
                sendMessageToClient(node)
            }
        }*/

        /*
        try {
            val sendTask = Wearable.getMessageClient(applicationContext).sendMessage(
                node.getId(), "/increase_wear_counter", null
            )
            val onSuccessListener: OnSuccessListener<Any> =
                OnSuccessListener { Log.w("TESTING ", "onSuccess") }
            sendTask.addOnSuccessListener(onSuccessListener)
            val onFailureListener =
                OnFailureListener { e -> Log.w("TESTING ", "onFailure with e: $e") }
            sendTask.addOnFailureListener(onFailureListener)
        } catch (e: Exception) {
            Log.w("TESTING", "e $e")
        }
        */
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
        Log.w("TESTING: ", "sendMessageToClient");
    }

    fun onConnected(@Nullable bundle: Bundle?) {
        Wearable.getMessageClient(this).addListener(this)
        // InitNodesTask().execute(this.applicationContext)
    }


    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.getPath().equals("/increase_wear_counter")) {
            count = count + 1;
        }
    }
}

// https://developer.android.com/topic/libraries/architecture/lifecycle#kotlin
// https://github.com/bevkoski/react-native-android-wear-demo/blob/efcbc7973e094472a75d7e27ec305431d2eb2fc3/android/wear/src/main/java/com/reactnativeandroidweardemo/MainActivity.java#L78
/*
internal class MobileDeviceListener(
    private val context: Context,
    private val callback: (Node) -> Unit
) {

    fun start() {
        Log.w("TESTING: ", "mobileDeviceListener.start()");
        // finish the implementation
        // https://developer.android.com/topic/libraries/architecture/lifecycle#kotlin
    }

    fun stop() {
        Log.w("TESTING: ", "mobileDeviceListener.stop()");
    }
}
 */