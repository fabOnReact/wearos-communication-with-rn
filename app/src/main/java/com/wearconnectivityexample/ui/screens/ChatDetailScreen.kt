package com.example.mywearosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.Text
import com.example.wearconnectivityexample.ui.screens.RecordVoiceScreen

@Composable
fun ChatDetailScreen(
    phoneNumber: String
) {
    var isRecording by remember { mutableStateOf(false) }
    val listState = rememberScalingLazyListState()
    val messageList = listOf("Hello, How are you?", "I'm good. And you?", "I'm great. What did you do today?")

    Box(modifier = Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            scalingParams = ScalingLazyColumnDefaults.scalingParams(
                edgeScale = 0.4f,
                minTransitionArea = 0.4f
            )
        ) {
            items(messageList.size) { index ->
                val item = messageList[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF97B1DA),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(0.dp)
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }

            if (isRecording) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF97B1DA),
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(0.dp)
                        ) {
                            Text(
                                text = "Recording...",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            item {
                // Record button at the bottom
                Button(
                    onClick = { isRecording = true },
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

        if (isRecording) {
            RecordVoiceScreen(onStopRecording = { isRecording = false })
        }
    }
}