package com.wearconnectivityexample.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text

@Composable
fun MenuScreen(navController: NavHostController) {
    val items = listOf(
        "Chatroom" to "chatroom",
        "Send File" to "sendFile",
        "Counter" to "counter"
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { (label, route) ->
            Chip(
                onClick = { navController.navigate(route) },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
