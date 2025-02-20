package com.wearconnectivityexample.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import com.wearconnectivityexample.R

@Composable
fun RecordVoiceScreen(onRecordClicked: () -> Unit) {
    // Full-screen container with centered content.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var checked by remember { mutableStateOf(true) }
        ToggleButton(
            checked = checked,
            onCheckedChange = { checked = it },
            enabled = true,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mic),
                contentDescription = "airplane",
                modifier =
                Modifier
                    .size(ToggleButtonDefaults.DefaultIconSize)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
    }
}