package com.example.connectivityandroidexample.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.connectivityandroidexample.presentation.theme.ConnectivityAndroidExampleTheme
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

@Composable
fun WearApp(currentCount: Int, increaseCount: () -> Unit) {
    ConnectivityAndroidExampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Counter(count = currentCount)
            Button(
                onClick = { increaseCount() },
                modifier = Modifier.offset(x = 0.dp, y = 50.dp),
            ) {
            }
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
    WearApp(0, { fun() {} })
}