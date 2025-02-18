import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.wearconnectivityexample.presentation.FileState

@Composable
fun ImageViewer() {
  val context = LocalContext.current
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
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Image not available")
      }
    }

    // Overlay the delete button in the top-right corner
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd
    ) {
      if (imagePath != null && File(imagePath).exists()) {
        Button(
          onClick = {
            if (imagePath != null) {
              val file = File(imagePath)
              if (file.exists()) {
                file.delete()
                FileState.imagePath = null
              }
            }
          }, modifier = Modifier.padding(top = 16.dp, end = 90.dp)
        ) {
          Text("Delete")
        }
      }
    }
  }
}
