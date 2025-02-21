package com.example.wearconnectivityexample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState

@Composable
fun ChatDetailScreen(
    phoneNumber: String,
    onRecordClick: () -> Unit
) {
    val messages = listOf(
        "Hello how are you?",
        "I'm good thanks."
    )
    val listState: ScalingLazyListState = rememberScalingLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        // List of messages
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            item {
                Text(text = "Chat with $phoneNumber", modifier = Modifier.padding(8.dp))
            }
            items(messages.size) { index ->
                Text(
                    text = messages[index],
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Record button at the bottom
        Button(
            onClick = { onRecordClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Record"
            )
        }
    }
}