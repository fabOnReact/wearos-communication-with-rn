package com.fabonreact.wearconnectivity

import org.json.JSONException
import org.json.JSONObject

/**
 * Represents a message exchanged between the Wear OS app and the paired Android application.
 *
 * The payload mirrors the structure used by the React Native companion library where the
 * [event] property identifies the type of the message and [data] contains the rest of the
 * parameters.
 */
data class WearMessage(
    val event: String,
    val data: Map<String, Any?> = emptyMap()
) {
    /** Returns the message encoded as a [JSONObject] ready to be sent through the Data Layer. */
    fun toJson(): JSONObject = JSONObject().apply {
        put(EVENT_KEY, event)
        data.forEach { (key, value) ->
            put(key, value)
        }
    }

    /** Returns the message encoded as a raw JSON string. */
    fun toJsonString(): String = toJson().toString()

    companion object {
        const val EVENT_KEY: String = "event"

        /**
         * Parses the JSON payload received from the companion app. Returns `null` when the
         * payload does not contain the mandatory [EVENT_KEY] property or when the payload is not
         * valid JSON.
         */
        fun fromJsonString(raw: String): WearMessage? = try {
            val json = JSONObject(raw)
            val event = json.optString(EVENT_KEY, null) ?: return null
            val remainingKeys = json.keys()
            val values = mutableMapOf<String, Any?>()
            while (remainingKeys.hasNext()) {
                val key = remainingKeys.next()
                if (key == EVENT_KEY) continue
                values[key] = json.get(key)
            }
            WearMessage(event = event, data = values)
        } catch (error: JSONException) {
            null
        }
    }
}
