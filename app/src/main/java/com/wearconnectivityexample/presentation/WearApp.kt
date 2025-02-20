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
import androidx.wear.compose.material.Text
import com.wearconnectivityexample.presentation.theme.ConnectivityAndroidExampleTheme
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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