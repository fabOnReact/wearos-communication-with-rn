/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.connectivityandroidexample.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.connectivityandroidexample.presentation.theme.ConnectivityAndroidExampleTheme
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {
    var countExternal by  mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            WearApp(0, { increaseCount() })
        }
        Wearable.getMessageClient(this).addListener(this)
    }

    fun increaseCount() {
        countExternal = countExternal + 1;
        Log.d("TESTING: ", "countExternal increased to: " + countExternal);
        setContent {
            WearApp(countExternal, { increaseCount() })
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.getPath().equals("/increase_wear_counter")) {
            println(">>> increase_wear_counter")
            countExternal = countExternal + 1;
            setContent {
                WearApp(countExternal, { increaseCount() })
            }
        }
    }
}

@Composable
fun WearApp(currentCount: Int, increaseCount: () -> Unit) {
    ConnectivityAndroidExampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Counter(countExternal = currentCount)
            Button(
                onClick = { increaseCount() },
                modifier = Modifier.offset(x = 0.dp, y = 50.dp),
            ) {
            }
        }
    }
}

@Composable
fun Counter(countExternal: Int) {
    Text(
        modifier = Modifier.offset(x = -3.dp, y = -30.dp),
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontSize = 50.sp,
        text = countExternal.toString()
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(0, { fun() {} })
}