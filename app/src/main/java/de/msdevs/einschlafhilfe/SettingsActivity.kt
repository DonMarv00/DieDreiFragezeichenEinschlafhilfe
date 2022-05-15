package de.msdevs.einschlafhilfe

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import de.msdevs.einschlafhilfe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    lateinit var binding : ActivitySettingsBinding
    lateinit var switchSpotify : SwitchCompat
    lateinit var switchUpdatelist : SwitchCompat
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()

        switchSpotify = binding.swUseSpotify
        switchUpdatelist = binding.swUpdateList

        switchSpotify.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("spotify",isChecked)
            sharedPreferencesEditor.apply()
        }
        switchUpdatelist.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("update_list",isChecked)
            sharedPreferencesEditor.apply()
        }

    }
}