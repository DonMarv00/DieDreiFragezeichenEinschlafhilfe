package de.msdevs.einschlafhilfe


import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.msdevs.einschlafhilfe.utils.Utility.getTheme


open class BaseActivity(private val noToolbar: Boolean) : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
    }

    private fun updateTheme() {

        if (getTheme(applicationContext) <= THEME_RED) {
            setTheme(R.style.AppThemeNoToolbar)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        } else if (getTheme(applicationContext) == THEME_BLUE) {
            setTheme(R.style.AppThemeNoToolbarBlue)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDarkBlue)
        } else if (getTheme(applicationContext) == THEME_WHITE) {
            setTheme(R.style.AppThemeNoToolbarWhite)
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