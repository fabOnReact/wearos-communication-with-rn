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
import com.wearconnectivityexample.presentation.FileState

@Composable
fun ImageViewer() {
		val context = LocalContext.current
		// Observe the image path from the shared state
		val imagePath = FileState.imagePath

		Box(modifier = Modifier.fillMaxSize()) {
				if (imagePath != null && File(imagePath).exists()) {
						Image(
								painter = rememberAsyncImagePainter(File(imagePath)),
								contentDescription = "Received Image",
								modifier = Modifier.fillMaxSize(),
								contentScale = ContentScale.Crop
						)
				} else {
						Text(text = "Image not available")
				}
		}
}