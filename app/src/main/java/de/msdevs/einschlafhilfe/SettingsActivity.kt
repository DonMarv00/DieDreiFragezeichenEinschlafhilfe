package de.msdevs.einschlafhilfe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.msdevs.einschlafhilfe.databinding.ActivitySettingsBinding
import de.msdevs.einschlafhilfe.dialog.AnonymousStatisticsDialog
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    /*
       Copyright 2017 - 2026 by Marvin Stelter
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupStatusBarForAndroid()

        binding.toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.updatePadding(top = top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsContainer) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)

            val providerPref = findPreference<Preference>("streaming_provider")
            providerPref?.summary = currentProviderLabel()
            providerPref?.setOnPreferenceClickListener {
                showProviderDialog(providerPref)
                true
            }

            findPreference<Preference>("licenses")?.setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutLibrariesActivity::class.java))
                true
            }

            findPreference<Preference>("contact")?.setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@citroncode.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Kontaktanfrage: DDF Folgenauswahl")
                    putExtra(Intent.EXTRA_TEXT, "Deine Nachricht hier...")
                }
                startActivity(intent)
                true
            }

            findPreference<Preference>("privacy")?.setOnPreferenceClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://citroncode.com/index.php?app=ddf-einschlafhilfe&privacy=1".toUri()
                    )
                )
                true
            }

            findPreference<Preference>("anonymous_statistics")?.setOnPreferenceClickListener {
                AnonymousStatisticsDialog.show(requireContext())
                true
            }

            findPreference<Preference>("datasource")?.setOnPreferenceClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://creativecommons.org/licenses/by/4.0/legalcode.de".toUri()
                    )
                )
                true
            }
        }

        private fun currentProviderLabel(): String {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val value = prefs.getString("streaming_provider", "ask") ?: "ask"
            val entries = resources.getStringArray(R.array.streaming_provider_entries)
            val values = resources.getStringArray(R.array.streaming_provider_values)
            val idx = values.indexOf(value).coerceAtLeast(0)
            return entries[idx]
        }

        private fun showProviderDialog(pref: Preference) {
            val entries = resources.getStringArray(R.array.streaming_provider_entries)
            val values = resources.getStringArray(R.array.streaming_provider_values)
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val current = prefs.getString("streaming_provider", "ask") ?: "ask"
            val checked = values.indexOf(current).coerceAtLeast(0)

            MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
                .setTitle(R.string.pref_streaming_provider_title)
                .setSingleChoiceItems(entries, checked) { dialog, which ->
                    prefs.edit().putString("streaming_provider", values[which]).apply()
                    pref.summary = entries[which]
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    private fun setupStatusBarForAndroid() {
        val parentView = findViewById<RelativeLayout>(R.id.rl_settings)
        val placeHolder: View = parentView.findViewById(R.id.android15statusBarPlaceHolder_settings)
        placeHolder.visibility = View.VISIBLE

        // Immer Dark Mode → keine hellen StatusBar-Icons
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        ViewCompat.setOnApplyWindowInsetsListener(parentView) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )
            placeHolder.updateLayoutParams {
                height = bars.top
            }

            v.updatePadding(
                left = bars.left,
                right = bars.right,
                bottom = bars.bottom
            )

            WindowInsetsCompat.CONSUMED
        }
    }
}