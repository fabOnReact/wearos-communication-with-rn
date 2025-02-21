// WearApp.kt
package com.example.wearconnectivityexample.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.navigation.navArgument
import com.example.mywearosapp.ui.screens.ChatDetailScreen
import com.example.wearconnectivityexample.ui.screens.RecordVoiceScreen
import com.wearconnectivityexample.ui.screens.ChatListScreen

@Composable
fun WearApp() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "chatList"
    ) {
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
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            ChatDetailScreen(
                phoneNumber = phoneNumber,
                onRecordClick = { navController.navigate("recordVoice") }
            )
        }
        composable("recordVoice") {
            RecordVoiceScreen(
                onStopRecording = {
                    // Go back to the previous screen, or handle differently
                    navController.popBackStack()
                }
            )
        }
    }
}