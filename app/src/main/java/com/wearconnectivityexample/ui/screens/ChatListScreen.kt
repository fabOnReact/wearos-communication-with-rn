package com.wearconnectivityexample.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState

@Composable
fun ChatListScreen(
    onChatSelected: (String) -> Unit
) {
    val chatList = listOf("08123111717", "08123111715")
    val listState = rememberScalingLazyListState()

    // This is the Wear-specific lazy column with scaling
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        items(chatList.size) { index ->
            val phoneNumber = chatList[index]
            Chip(
                onClick = { onChatSelected(phoneNumber) },
                label = { Text(text = phoneNumber) },
                modifier = Modifier.padding(8.dp),
                colors = ChipDefaults.primaryChipColors()
            )
        }
        item {
            Text(
                text = "You’ve reached the end of your conversations",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}