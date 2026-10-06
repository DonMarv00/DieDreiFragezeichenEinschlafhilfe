package de.msdevs.einschlafhilfe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.msdevs.einschlafhilfe.adapter.KapitelAdapter
import de.msdevs.einschlafhilfe.adapter.LinkAdapter
import de.msdevs.einschlafhilfe.adapter.SprecherAdapter
import de.msdevs.einschlafhilfe.data.local.Episode
import de.msdevs.einschlafhilfe.domain.EpisodeDetails
import de.msdevs.einschlafhilfe.data.repository.EpisodeRepository
import de.msdevs.einschlafhilfe.data.local.Kategorie
import de.msdevs.einschlafhilfe.databinding.ActivityMainBinding
import de.msdevs.einschlafhilfe.dialog.AnonymousStatisticsDialog
import de.msdevs.einschlafhilfe.utils.AnalyticsTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var repo: EpisodeRepository

    private var playMenuItem: android.view.MenuItem? = null
    private var currentEpisode: Episode? = null
    private var collapsedTitle: String = ""
    private var playAvailable: Boolean = true
    private var isCollapsed: Boolean = false

    private var headerFullHeight: Int = 0
    /*
       Copyright 2017 - 2026 by Marvin Stelter
     */

    private val filterLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        lifecycleScope.launch {
            if (!repo.isVorratValid()) {
                repo.refillVorrat()
                preloadCovers()
            }
            showCurrent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = EpisodeRepository(this)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        setupCoverInsets()
        setupCollapsingFade()
        setupFabInsets()

        binding.recyclerLinks.layoutManager = LinearLayoutManager(this)

        binding.recyclerKapitel.layoutManager = LinearLayoutManager(this)
        binding.recyclerSprecher.layoutManager = LinearLayoutManager(this)

        binding.fabFilter.setOnClickListener {
            filterLauncher.launch(Intent(this, FilterActivity::class.java))
        }
        binding.fabReload.setOnClickListener {
            lifecycleScope.launch {
                val next = repo.next()
                preloadCovers()
                if (next != null) bind(next)
            }
        }

        binding.buttonPlayHeader.setOnClickListener { onPlayClicked() }

        startupFlow()

        AnonymousStatisticsDialog.showIfNeeded(this)
        if (AnonymousStatisticsDialog.isEnabled(this)) {
            sendAnalyticsCall()
        }
    }

    // ---------- Daten-Flow ----------

    private fun startupFlow() {
        lifecycleScope.launch {
            val hasCurrent = repo.current() != null

            if (hasCurrent) {
                showCurrent()
            } else {
                setLoading(true)
            }

            if (repo.needsSync()) {
                try {
                    repo.sync()
                } catch (e: Exception) {
                    setLoading(false)
                    showNoInternet()
                    return@launch
                }
            }

            if (!repo.isVorratValid()) {
                repo.refillVorrat()
                preloadCovers()
            }

            showCurrent()
            setLoading(false)
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressInitial.visibility =
            if (loading) android.view.View.VISIBLE else android.view.View.GONE
        val contentVis = if (loading) android.view.View.INVISIBLE else android.view.View.VISIBLE
        binding.appBarLayout.visibility = contentVis
        binding.nestedScrollView.visibility = contentVis
    }

    private suspend fun showCurrent() {
        val ep = repo.current() ?: return
        bind(ep)
    }

    private suspend fun preloadCovers() {
        val urls = repo.vorratCoverUrls()
        withContext(Dispatchers.Main) {
            urls.forEach { Glide.with(this@MainActivity).load(it).preload() }
        }
    }

    private fun showNoInternet() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.no_internet_title))
            .setMessage(getString(R.string.no_internet_message))
            .setPositiveButton(getString(R.string.dialog_ok), null)
            .show()
    }

    private fun bind(ep: Episode) {
        currentEpisode = ep

        // DR3i gibt es nicht mehr im Streaming -> kein Play
        playAvailable = ep.kategorie != Kategorie.DR3I
        binding.buttonPlayHeader.visibility =
            if (playAvailable) android.view.View.VISIBLE else android.view.View.GONE
        playMenuItem?.isVisible = playAvailable && isCollapsed

        val nummerText = if (ep.nummer != null) {
            getString(R.string.folge_prefix, ep.nummer)
        } else {
            getString(R.string.kurzgeschichte)
        }
        binding.textNummer.text = nummerText
        binding.textTitel.text = ep.titel
        binding.textAutor.text = ep.autor ?: ""


        val hasBeschreibung = !ep.beschreibung.isNullOrBlank()
        binding.labelBeschreibung.visibility = if (hasBeschreibung) View.VISIBLE else View.GONE
        binding.textBeschreibung.visibility = if (hasBeschreibung) View.VISIBLE else View.GONE
        binding.textBeschreibung.text = ep.beschreibung ?: ""

        collapsedTitle = if (ep.nummer != null) {
            getString(R.string.collapsed_title_format, ep.nummer, ep.titel)
        } else {
            ep.titel
        }

        binding.textJahr.text = ep.jahr?.toString() ?: getString(R.string.value_none)
        binding.textDauer.text = getString(R.string.dauer_minuten, ep.dauerMs / 60000)
        binding.textInfoAutor.text = ep.autor ?: getString(R.string.value_none)
        binding.textSkriptautor.text = ep.skriptautor ?: getString(R.string.value_none)

        Glide.with(this).load(ep.coverUrl).into(binding.coverImage)

        val details = EpisodeDetails.parse(ep)
        binding.recyclerKapitel.adapter = KapitelAdapter(details.kapitel ?: emptyList())
        binding.recyclerSprecher.adapter = SprecherAdapter(details.sprechrollen ?: emptyList())


        binding.headerInfo.updateLayoutParams { height = ViewGroup.LayoutParams.WRAP_CONTENT }
        binding.headerInfo.post {
            headerFullHeight = binding.headerInfo.height
        }
        buildLinks(ep)
    }

    private fun buildLinks(ep: Episode) {
        val links = buildList {
            ep.dreifragezeichenUrl?.let { add(getString(R.string.provider_dreifragezeichen) to it) }
            ep.spotifyUrl?.let { add(getString(R.string.provider_spotify) to it) }
            ep.appleMusicUrl?.let { add(getString(R.string.provider_apple_music) to it) }
            ep.amazonMusicUrl?.let { add(getString(R.string.provider_amazon_music) to it) }
            ep.youtubeMusicUrl?.let { add(getString(R.string.provider_youtube_music) to it) }
            ep.deezerUrl?.let { add(getString(R.string.provider_deezer) to it) }
        }
        binding.recyclerLinks.adapter = LinkAdapter(links) { url ->
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        }
    }

    // ---------- Play ----------

    private fun onPlayClicked() {
        val ep = currentEpisode ?: return
        if (ep.kategorie == Kategorie.DR3I) return

        val provider = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString("streaming_provider", "ask") ?: "ask"

        val url = when (provider) {
            "spotify" -> ep.spotifyUrl
            "applemusic" -> ep.appleMusicUrl
            "amazonmusic" -> ep.amazonMusicUrl
            "youtubemusic" -> ep.youtubeMusicUrl
            "deezer" -> ep.deezerUrl
            else -> null
        }

        if (provider == "ask" || url == null) {
            showProviderChooser(ep)
        } else {
            openUrl(url)
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    private fun showProviderChooser(ep: Episode) {
        val options = buildList {
            ep.spotifyUrl?.let { add(Triple(getString(R.string.provider_spotify), "spotify", it)) }
            ep.appleMusicUrl?.let { add(Triple(getString(R.string.provider_apple_music), "applemusic", it)) }
            ep.amazonMusicUrl?.let { add(Triple(getString(R.string.provider_amazon_music), "amazonmusic", it)) }
            ep.youtubeMusicUrl?.let { add(Triple(getString(R.string.provider_youtube_music), "youtubemusic", it)) }
            ep.deezerUrl?.let { add(Triple(getString(R.string.provider_deezer), "deezer", it)) }
        }
        if (options.isEmpty()) return

        val labels = options.map { it.first }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.play_with_title))
            .setItems(labels) { _, which ->
                val (_, key, url) = options[which]
                PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString("streaming_provider", key)
                    .apply()
                openUrl(url)
            }
            .show()
    }

    // ---------- Menü ----------

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        playMenuItem = menu.findItem(R.id.action_play)
        playMenuItem?.isVisible = playAvailable && isCollapsed
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_play) {
            onPlayClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------- Insets / Collapsing ----------

    private val displayBreite get() = resources.displayMetrics.widthPixels

    private fun setupCoverInsets() {
        binding.appBarLayout.updateLayoutParams { height = displayBreite }
        binding.coverImage.updateLayoutParams {
            width = displayBreite
            height = displayBreite
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            binding.statusBarPlaceholder.updateLayoutParams { height = statusBarHeight }
            binding.appBarLayout.updatePadding(top = statusBarHeight)
            binding.appBarLayout.updateLayoutParams { height = displayBreite }
            (binding.coverImage.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = -statusBarHeight
                binding.coverImage.layoutParams = this
            }
            insets
        }
    }

    private fun setupCollapsingFade() {
        val scrimColor = ContextCompat.getColor(this, R.color.colorPrimary)
        val scrimColorDark = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val toolbarBg = scrimColor.toDrawable().apply { alpha = 0 }
        toolbar.background = toolbarBg
        val statusBg = scrimColorDark.toDrawable().apply { alpha = 0 }
        binding.statusBarPlaceholder.background = statusBg

        binding.appBarLayout.addOnOffsetChangedListener(
            com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
                val range = appBar.totalScrollRange
                if (range == 0) return@OnOffsetChangedListener
                val scrolled = -verticalOffset

                val dockStart = range * 0.75f
                val df = ((scrolled - dockStart) / (range - dockStart)).coerceIn(0f, 1f)
                val a = (df * 255).toInt()
                toolbarBg.alpha = a
                statusBg.alpha = a

                val headerStart = range * 0.95f
                val hf = ((scrolled - headerStart) / (range - headerStart)).coerceIn(0f, 1f)
                binding.headerInfo.alpha = 1f - hf
                if (headerFullHeight > 0) {
                    binding.headerInfo.updateLayoutParams {
                        height = (headerFullHeight * (1f - hf)).toInt()
                    }
                }

                isCollapsed = df >= 1f
                toolbar.title = if (isCollapsed) collapsedTitle else ""
                playMenuItem?.isVisible = playAvailable && isCollapsed
            }
        )
    }

    private fun setupFabInsets() {
        val fabMargin = (10 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(binding.fabReload) { v, insets ->
            val nav = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = nav + fabMargin }
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.fabFilter) { v, insets ->
            val nav = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = nav + fabMargin + (66 * resources.displayMetrics.density).toInt()
            }
            insets
        }
    }
    private fun sendAnalyticsCall() {
        try {
            AnalyticsTracker.track()
        } catch (e: Exception) {
            Log.e("MainActivity", "Critical error in sendAnalyticsCall: " + e.message)
        }
    }
}