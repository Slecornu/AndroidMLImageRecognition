package uk.co.samlecornu.machinelearningimagerecognition.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.text.TextUtils
import java.io.IOException
import java.net.URL


class BitmapRetriever(private var bitmapPath: String, private var callback : OnRetrieved) : AsyncTask<Any, Any, Bitmap>() {

    override fun doInBackground(vararg `object`: Any?): Bitmap? {
        if (!isCancelled && !TextUtils.isEmpty(bitmapPath)) {
            var bitmap: Bitmap? = null
            try {
                bitmap = BitmapFactory.decodeStream(URL(bitmapPath).openConnection().getInputStream())
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmap
        }
        return null
    }

    override fun onPostExecute(bitmap: Bitmap?) {
        if (!isCancelled) {
            callback.run(bitmapPath, bitmap)
        }
    }

    interface OnRetrieved {
        fun run(bitmapUri: String?, bitmap: Bitmap?)
    }
}