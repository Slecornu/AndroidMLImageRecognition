package uk.co.samlecornu.machinelearningimagerecognition.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uk.co.samlecornu.machinelearningimagerecognition.net.BitmapRetriever
import uk.co.samlecornu.machinelearningimagerecognition.net.BitmapRetriever.OnRetrieved
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import uk.co.samlecornu.machinelearningimagerecognition.R

class MainActivity : AppCompatActivity() {

  private val imagePath = "https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fspecials-images.forbesimg.com%2Fdam%2Fimageserve%2F1072007868%2F960x0.jpg%3Ffit%3Dscale"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val image : ImageView = findViewById(R.id.image)
    val description : TextView = findViewById(R.id.description)
    description.text = getString(R.string.fetching_image)
    BitmapRetriever(imagePath,
      object : OnRetrieved {
        override fun run(bitmapUri: String?, bitmap: Bitmap?) {
          if (bitmap != null) {
            description.text = getString(R.string.processing_image)
            image.setImageBitmap(bitmap)

            val localModel = LocalModel.Builder().setAssetFilePath("mnasnet_1.3_224_1_metadata_1.tflite").build();
            val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel).setConfidenceThreshold(0.5f).build()
            ImageLabeling.getClient(customImageLabelerOptions).process(InputImage.fromBitmap(bitmap, 0))
              .addOnSuccessListener { labels ->
                var infomation = ""
                for (label in labels) {
                  infomation += String.format(getString(R.string.image_information), label.text, label.confidence, label.index)
                }
                description.text = infomation
              }
              .addOnFailureListener { e ->
                description.text = String .format(getString(R.string.failed_with_error), e.localizedMessage)
              }
          }
          else {
            description.text = getString(R.string.fetching_image_failed)
          }
        }
      }
    ).execute()
  }
}