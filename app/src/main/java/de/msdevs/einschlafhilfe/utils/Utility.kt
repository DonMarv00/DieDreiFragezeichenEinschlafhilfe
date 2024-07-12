package de.msdevs.einschlafhilfe.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.graphics.Rect
import android.preference.PreferenceManager
import android.widget.ImageView
import com.tapadoo.alerter.Alerter
import de.msdevs.einschlafhilfe.R

object Utility {
    fun setTheme(context: Context, theme: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply()
    }

    fun getTheme(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(context.getString(R.string.prefs_theme_key), -1)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun displayAlerter(text : String, color : Int,white : Boolean, activity : Activity){
        activity.getDrawable(R.drawable.ic_info)?.let {
         if(white){
             Alerter.create(activity)
                 .setTitle(activity.getString(R.string.information))
                 .setBackgroundColorInt(color)
                 .setIcon(it)
                 .setText(text)
                 .show()
         }else{
             Alerter.create(activity)
                 .setTitle(activity.getString(R.string.information))
                 .setBackgroundColorInt(color)
                 .setIcon(it)
                 .setText(text)
                 .show()
         }
        }
    }
    fun cropAndSetImage(context: Context, imageView: ImageView, originalBitmap: Bitmap) {
        val widthPx = 463
        val heightPx = 600
        val posX = 398
        val posY = 0

        val rect = Rect(posX, posY, posX + widthPx, posY + heightPx)
        val croppedBitmap = createBitmap(originalBitmap, rect.left, rect.top, rect.width(), rect.height())
        imageView.setImageBitmap(croppedBitmap)
    }
}