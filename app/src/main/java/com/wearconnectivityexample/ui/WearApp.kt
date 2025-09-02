package com.example.wearconnectivityexample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.navigation.navArgument
import com.example.mywearosapp.ui.screens.ChatDetailScreen
import com.example.wearconnectivityexample.ui.screens.RecordVoiceScreen
import com.wearconnectivityexample.ui.components.ImageViewer
import com.wearconnectivityexample.ui.screens.ChatListScreen

@Composable
fun WearApp() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "menu",
    ) {
        composable("menu") {
            MenuScreen(
                onChatListClick = { navController.navigate("chatList") },
                onRecordClick = { navController.navigate("recordVoice") },
                onCounterClick = { navController.navigate("counter") },
                onImageViewerClick = { navController.navigate("imageViewer") }
            )
        }
        composable("chatList") {
            ChatListScreen(
                onChatSelected = { phoneNumber ->
                    navController.navigate("chatDetail/$phoneNumber")
                }
            )
        }
        composable(
            route = "chatDetail/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") {})
        ) { backStackEntry ->
            val phoneNumber =
                backStackEntry.arguments?.getString("phoneNumber") ?: ""
            ChatDetailScreen(
                phoneNumber = phoneNumber,
                onRecordClick = { navController.navigate("recordVoice") }
            )
        }
        composable("recordVoice") {
            RecordVoiceScreen(
                onStopRecording = { navController.popBackStack() }
            )
        }
        composable("counter") {
            CounterScreen(onBack = { navController.popBackStack() })
        }
        composable("imageViewer") {
            ImageViewerScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MenuScreen(
    onChatListClick: () -> Unit,
    onRecordClick: () -> Unit,
    onCounterClick: () -> Unit,
    onImageViewerClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onChatListClick) { Text("Chats") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRecordClick) { Text("Record") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onCounterClick) { Text("Counter") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onImageViewerClick) { Text("Images") }
    }
}

@Composable
fun CounterScreen(onBack: () -> Unit) {
    var count by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Count: $count")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { count++ }) { Text("Increase") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
fun ImageViewerScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ImageViewer()
        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("Back")
        }
    }
}

