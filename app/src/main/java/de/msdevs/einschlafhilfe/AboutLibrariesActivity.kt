package de.msdevs.einschlafhilfe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.LicenseHueResolver
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import de.msdevs.einschlafhilfe.ui.AppThemeNoToolbar


class AboutLibrariesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeNoToolbar {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(getString(R.string.pref_licenses_title)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        painterResource(R.drawable.ic_back),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                ) { padding ->
                    val libraries by produceLibraries(R.raw.aboutlibraries)

                    val chipRed = Color(0xFFD50000)
                    val onChip  = Color(0xFFFFFFFF)

                    val libs = libraries
                    if (libs == null || libs.libraries.isEmpty()) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(color = chipRed)
                        }
                    } else {
                        LibrariesContainer(
                            libraries = libs,                       // ← libs, nicht libraries
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = padding.calculateTopPadding()),
                            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                            actionMode = LibraryActionMode.Icons,
                            variant = LibrariesVariant.Traditional,
                            density = LibrariesDensity.Cozy,
                            colors = LibraryDefaults.libraryColors(
                                licenseChipColors = LibraryDefaults.chipColors(
                                    containerColor = chipRed,
                                    contentColor = onChip
                                )
                            ),
                            variantColors = LibraryDefaults.m3VariantColors(
                                licenseHueResolver = LicenseHueResolver.None,
                                licenseBadgeContainer = chipRed,
                                licenseBadgeContent = onChip
                            )
                        )
                    }
                }
            }
        }
    }
}