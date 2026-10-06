package de.msdevs.einschlafhilfe

import android.app.Activity
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import de.msdevs.einschlafhilfe.adapter.FilterAdapter
import de.msdevs.einschlafhilfe.domain.FilterPresets
import de.msdevs.einschlafhilfe.prefs.PrefsManager
import de.msdevs.einschlafhilfe.databinding.ActivityFilterBinding
import de.msdevs.einschlafhilfe.models.FilterItem

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private lateinit var prefs: PrefsManager
    /*
       Copyright 2017 - 2026 by Marvin Stelter
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupStatusBarForAndroid()
        prefs = PrefsManager(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.updatePadding(top = top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerFilter) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }

        binding.recyclerFilter.layoutManager = LinearLayoutManager(this)
        binding.recyclerFilter.adapter = FilterAdapter(buildFilterItems()) { entry ->
            prefs.activeFilterId = entry.id
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun buildFilterItems(): List<FilterItem> {
        val items = mutableListOf<FilterItem>()

        // Allgemein: all, new, jubilee
        items += FilterItem.Header("Allgemein")
        items += presetEntries("all", "new", "jubilee")

        // Kategorien
        items += FilterItem.Header("Kategorien")
        items += presetEntries("serie", "kids", "dr3i", "spezial", "kurz")

        // Ären
        items += FilterItem.Header("Ären")
        items += presetEntries("klassiker", "crimebusters", "vorlagen", "triumvirat", "neuzeit")

        items += FilterItem.Header("Charaktere")
        items += presetEntries(
            "char_skinny", "char_cotta", "char_reynolds", "char_hugenay",
            "char_mathilda", "char_titus", "char_morton", "char_rubbish"
        )

        return items
    }

    private fun presetEntries(vararg ids: String): List<FilterItem.Entry> {
        val activeId = prefs.activeFilterId
        return ids.mapNotNull { id ->
            FilterPresets.all.firstOrNull { it.id == id }?.let { p ->
                FilterItem.Entry(p.id, p.name, p.subtitle, selected = p.id == activeId)
            }
        }
    }
    private fun setupStatusBarForAndroid() {
        val parentView = findViewById<RelativeLayout>(R.id.rl_filter)
        val placeHolder: View = parentView.findViewById(R.id.android15statusBarPlaceHolder_filter)
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
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}