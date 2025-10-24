package com.fabonreact.wearconnectivity

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CopyOnWriteArraySet

/**
 * High level wrapper around [MessageClient] that mirrors the API exposed by the React Native
 * library. The class is responsible for discovering connected nodes, sending JSON messages and
 * dispatching received events to registered listeners.
 */
class WearMessagingClient(context: Context) : MessageClient.OnMessageReceivedListener {

    private val applicationContext = context.applicationContext
    private val messageClient = Wearable.getMessageClient(applicationContext)
    private val nodeClient = Wearable.getNodeClient(applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val listeners = CopyOnWriteArraySet<WearMessageListener>()
    @Volatile
    private var cachedNodes: List<Node> = emptyList()

    /** Registers the listener within the Google Play Services layer. */
    suspend fun connect() {
        messageClient.addListener(this)
        refreshNodes()
    }

    /** Unregisters the listener and clears the cached nodes. */
    fun disconnect() {
        messageClient.removeListener(this)
        cachedNodes = emptyList()
    }

    /**
     * Sends [message] to all connected nodes. Returns `true` when at least one node received the
     * payload.
     */
    suspend fun sendMessage(message: WearMessage): Boolean {
        val nodes = ensureNodes()
        if (nodes.isEmpty()) return false

        var delivered = false
        var lastError: Exception? = null
        for (node in nodes) {
            try {
                messageClient.sendMessage(node.id, message.toJsonString(), byteArrayOf()).await()
                delivered = true
            } catch (error: Exception) {
                lastError = error
            }
        }

        if (!delivered && lastError != null) {
            throw lastError
        }
        return delivered
    }

    /** Convenience overload to send a message with a simple map as payload. */
    suspend fun sendMessage(event: String, data: Map<String, Any?> = emptyMap()): Boolean {
        return sendMessage(WearMessage(event = event, data = data))
    }

    /**
     * Refreshes the internal cache of connected nodes.
     */
    suspend fun refreshNodes(): List<Node> {
        cachedNodes = nodeClient.connectedNodes.await()
        return cachedNodes
    }

    /**
     * Registers a listener that will be notified when a new message is received.
     */
    fun addListener(listener: WearMessageListener) {
        listeners.add(listener)
    }

    /**
     * Removes a previously registered listener.
     */
    fun removeListener(listener: WearMessageListener) {
        listeners.remove(listener)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val message = WearMessage.fromJsonString(messageEvent.path) ?: return
        if (listeners.isEmpty()) return

        scope.launch(Dispatchers.Main) {
            listeners.forEach { listener ->
                listener.onMessage(message)
            }
        }
    }

    private suspend fun ensureNodes(): List<Node> {
        return if (cachedNodes.isEmpty()) {
            refreshNodes()
        } else {
            cachedNodes
        }
    }

    fun interface WearMessageListener {
        fun onMessage(message: WearMessage)
    }
}
