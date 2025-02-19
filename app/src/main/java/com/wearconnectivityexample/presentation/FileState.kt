package com.wearconnectivityexample.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object FileState {
    var imagePath: String? by mutableStateOf(null)
}