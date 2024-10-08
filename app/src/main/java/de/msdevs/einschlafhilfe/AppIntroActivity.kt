package de.msdevs.einschlafhilfe

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import de.msdevs.einschlafhilfe.fragments.intro.PrivacyFragment
import de.msdevs.einschlafhilfe.fragments.intro.SettingsFragment

class AppIntroActivity : AppIntro() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        isSkipButtonEnabled = false
        setIndicatorColor(
            selectedIndicatorColor = getColor(R.color.white),
            unselectedIndicatorColor = getColor(R.color.whiteSecondary)
        )

        addSlide(AppIntroFragment.createInstance(
            title = resources.getString(R.string.welcome),
            backgroundColorRes = R.color.black,
            imageDrawable = R.drawable.ic_covers,
            description = resources.getString(R.string.welcome_text)
        ))
        addSlide(SettingsFragment.newInstance())
        addSlide(PrivacyFragment())


    }
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt("first",1)
        sharedPreferencesEditor.apply()
        finish()
    }
}