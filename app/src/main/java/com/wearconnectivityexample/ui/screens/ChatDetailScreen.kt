// ChatDetailScreen.kt
package com.example.mywearosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.Text

@Composable
fun ChatDetailScreen(
    phoneNumber: String,
    onRecordClick: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            // First message bubble
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF97B1DA), // Light blue bubble
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Hello how are you?",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }

            // Second message bubble
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF97B1DA),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "I'm good thanks.",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Record button at the bottom
        Button(
            onClick = onRecordClick,
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