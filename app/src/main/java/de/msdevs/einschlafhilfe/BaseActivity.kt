package de.msdevs.einschlafhilfe


import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.msdevs.einschlafhilfe.utils.Utility.getTheme


open class BaseActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
    }

    fun updateTheme() {
        if (getTheme(applicationContext) <= THEME_RED) {
            setTheme(R.style.AppTheme)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
        } else if (getTheme(applicationContext) == THEME_BLUE) {
            setTheme(R.style.AppThemeBlue)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimaryBlue)
        } else if (getTheme(applicationContext) == THEME_WHITE) {
            setTheme(R.style.AppThemeWhite)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.white)

        }
    }

    companion object {
        private const val THEME_WHITE = 4
        private const val THEME_RED = 2
        private const val THEME_BLUE = 3
    }
}