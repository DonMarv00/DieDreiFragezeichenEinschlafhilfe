package de.msdevs.einschlafhilfe

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroFragment
import de.msdevs.einschlafhilfe.fragments.PrivacyFragment
import de.msdevs.einschlafhilfe.fragments.SettingsFragment

class AppIntroActivity : AppIntro() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
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
        // Decide what to do when the user clicks on "Skip"
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