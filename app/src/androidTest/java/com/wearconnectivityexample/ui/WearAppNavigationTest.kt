package com.wearconnectivityexample.ui

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.wearconnectivityexample.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WearAppNavigationTest {

    @get:Rule(order = 0)
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateFromChatListToRecordScreen() {
        composeRule.onNodeWithText("08131372751").assertExists().performClick()

        composeRule.onNodeWithText("Hello, How are you?").assertExists()

        composeRule.onNodeWithContentDescription("Record").assertExists().performClick()

        composeRule.onNodeWithText("Record Voice").assertExists()
    }
}
