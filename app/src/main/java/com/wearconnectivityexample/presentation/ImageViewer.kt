package com.wearconnectivityexample.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun ImageViewer() {
		val context = LocalContext.current
		val imageFile = File(context.filesDir, "received_file.jpg")

		Box(modifier = Modifier.fillMaxSize()) {
				if (imageFile.exists()) {
						Image(
								painter = rememberAsyncImagePainter(imageFile),
								contentDescription = "Received Image",
								modifier = Modifier.fillMaxSize(),
								contentScale = ContentScale.Crop
						)
				} else {
						Text(text = "Image not available")
				}
		}
}