
package com.wearconnectivityexample.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.ui.Alignment
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults

@Composable
fun ChatListScreen(
    onChatSelected: (String) -> Unit
) {
    val listState = rememberScalingLazyListState()

    // Example data: one "Start chat" chip, one two-line chip, plus some duplicates for testing
    val chatList = listOf("START_CHAT", "08131372751", "08131372751", "08131372751", "08131372751", "08131372751")

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        scalingParams = ScalingLazyColumnDefaults.scalingParams(
            edgeScale = 0.4f,
            minTransitionArea = 0.40f,
        )
    ) {
        items(chatList.size) { index ->
            when (val item = chatList[index]) {
                "START_CHAT" -> {
                    // 1) "Start chat" chip with chat icon
                    Chip(
                        onClick = { /* handle start chat */ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Chat Icon"
                            )
                        },
                        label = { Text(text = "Start chat") },
                        // Example dark-gray styling:
                        colors = ChipDefaults.primaryChipColors(
                            backgroundColor = Color(0xFF3C3C3C),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                else -> {
                    // 2) Two-line chip with phone number (label) and subtext (secondaryLabel)
                    Chip(
                        onClick = { onChatSelected(item) },
                        label = { Text(text = item) },
                        secondaryLabel = { Text(text = "Yes") },
                        colors = ChipDefaults.primaryChipColors(
                            backgroundColor = Color(0xFF3C3C3C),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }

        // 3) Footer text
        item {
            // Wrap both text and the button in a Column for vertical layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Footer text
                Text(
                    text = "You’ve reached the end of your conversations",
                    modifier = Modifier.padding(8.dp),
                    color = Color.White // if your app uses a dark background
                )

                // Spacing between the text and the button
                Spacer(modifier = Modifier.height(12.dp))

                // Circular button with arrow-up icon
                Button(
                    onClick = {
                        // Example action: scroll to top, or navigate, etc.
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF3C3C3C), // Dark gray
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Scroll up"
                    )
                }
            }
        }
    }
}
