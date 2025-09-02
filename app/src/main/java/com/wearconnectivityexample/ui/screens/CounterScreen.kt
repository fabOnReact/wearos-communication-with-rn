package com.wearconnectivityexample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject

@Composable
fun CounterScreen() {
    val context = LocalContext.current
    var count by remember { mutableStateOf(0) }
    val messageClient = remember { Wearable.getMessageClient(context) }
    val nodeClient = remember { Wearable.getNodeClient(context) }

    fun sendCounter(value: Int) {
        val json = JSONObject().apply {
            put("event", "counter")
            put("value", value)
        }
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, json.toString(), null)
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = MessageClient.OnMessageReceivedListener { event ->
            try {
                val json = JSONObject(event.path)
                if (json.optString("event") == "counter") {
                    count = json.optInt("value", count)
                }
            } catch (_: Exception) {
            }
        }
        messageClient.addListener(listener)
        onDispose { messageClient.removeListener(listener) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count.toString())
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            count += 1
            sendCounter(count)
        }) {
            Text("Increment")
        }
    }
}
