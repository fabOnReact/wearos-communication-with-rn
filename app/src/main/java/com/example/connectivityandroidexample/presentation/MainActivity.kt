/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.connectivityandroidexample.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Nullable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {
    private lateinit var mobileDeviceListener: MobileDeviceListener
    var count by mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)
        mobileDeviceListener = MobileDeviceListener(this) { node ->
            // update UI
            val nodeClient = Wearable.getNodeClient(this)
            val nodes = Tasks.await(nodeClient.getConnectedNodes())
            /*
            if (nodes.size > 0) {
                for (node in nodes) {
                // sendMessageToClient(node)
                }
            }*/
        }
        setContent {
            WearApp(currentCount = count, { increaseCount() })
        }
    }

    public override fun onStart() {
        super.onStart()
        mobileDeviceListener.start()
    }

    public override fun onStop() {
        super.onStop()
        mobileDeviceListener.stop()
        // manage other components that need to respond
        // to the activity lifecycle
    }

    fun increaseCount() {
        count++;
    }

    fun sendMessageToClient(node: Node) {
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
internal class MobileDeviceListener(
    private val context: Context,
    private val callback: (Node) -> Unit
) {

    fun start() {
        // connect to system location service
    }

    fun stop() {
        // disconnect from system location service
    }
}