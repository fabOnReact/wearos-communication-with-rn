package com.wearconnectivityexample.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.wearconnectivityexample.presentation.theme.ConnectivityAndroidExampleTheme
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.ToggleButton
import com.wearconnectivityexample.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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

@Composable
fun WearApp(currentCount: Int, increaseCount: () -> Unit, sendMessagesToClient: () -> Unit) {
    ConnectivityAndroidExampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            RecordVoiceScreen(
                onRecordClicked = { Log.d("RecordVoiceScreen", "clicked") }
            )
        }
    }
}

@Composable
fun Counter(count: Int) {
    Text(
        modifier = Modifier.offset(x = -3.dp, y = -30.dp),
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontSize = 50.sp,
        text = count.toString()
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(0, { fun() {} }, { fun() {} })
}


@OptIn(ExperimentalWearMaterialApi::class)
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