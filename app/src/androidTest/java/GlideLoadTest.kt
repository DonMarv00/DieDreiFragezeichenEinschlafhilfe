package de.msdevs.einschlafhilfe

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ExecutionException

@RunWith(AndroidJUnit4::class)
class GlideLoadTest {

    @Test
    fun testImageLoading() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val baseUrl = context.getString(R.string.cover_citroncode_url)
        val failedNumbers = mutableListOf<Int>()

        for (i in 1..227) {
            val url = "$baseUrl$i.png"
            try {
                Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
            } catch (e: ExecutionException) {
                Log.e("GlideLoadTest", "Failed to load image at URL: $url", e)
                failedNumbers.add(i)
            } catch (e: InterruptedException) {
                Log.e("GlideLoadTest", "Failed to load image at URL: $url", e)
                failedNumbers.add(i)
            }
        }

        if (failedNumbers.isNotEmpty()) {
            Log.i("GlideLoadTest", "Failed to load the following images:")
            failedNumbers.forEach { number ->
                Log.i("GlideLoadTest", number.toString())
            }
        } else {
            Log.i("GlideLoadTest", "All images loaded successfully.")
        }
    }
}