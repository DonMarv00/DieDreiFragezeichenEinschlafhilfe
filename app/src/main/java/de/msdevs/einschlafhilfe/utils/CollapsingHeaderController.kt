package de.msdevs.einschlafhilfe.utils

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import com.google.android.material.appbar.AppBarLayout

/**
 * Kapselt den Collapsing-/Fade-Effekt für Toolbar, Statusbar-Placeholder
 * und den Header-Info-Block.
 *
 * Copyright 2017 - 2026 by Marvin Stelter
 */
class CollapsingHeaderController(
    private val appBarLayout: AppBarLayout,
    private val toolbar: Toolbar,
    private val statusBarPlaceholder: View,
    private val headerInfo: View,
    private val scrimColor: Int,
    private val scrimColorDark: Int,
    private val collapsedTitle: String,
    private val dockStartRatio: Float = 0.75f,
    private val headerStartRatio: Float = 0.95f
) {

    private val toolbarBg = ColorDrawable(scrimColor).apply { alpha = 0 }
    private val statusBg = ColorDrawable(scrimColorDark).apply { alpha = 0 }

    fun attach() {
        toolbar.background = toolbarBg
        statusBarPlaceholder.background = statusBg

        headerInfo.post {
            val fullHeight = headerInfo.height

            appBarLayout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
                    val range = appBar.totalScrollRange
                    if (range == 0) return@OnOffsetChangedListener
                    val scrolled = -verticalOffset

                    // Toolbar + Statusbar einblenden
                    val dockStart = range * dockStartRatio
                    val df = ((scrolled - dockStart) / (range - dockStart)).coerceIn(0f, 1f)
                    val a = (df * 255).toInt()
                    toolbarBg.alpha = a
                    statusBg.alpha = a

                    // Header faden + kollabieren (eigener Startpunkt)
                    val headerStart = range * headerStartRatio
                    val hf = ((scrolled - headerStart) / (range - headerStart)).coerceIn(0f, 1f)
                    headerInfo.alpha = 1f - hf
                    headerInfo.updateLayoutParams {
                        height = (fullHeight * (1f - hf)).toInt()
                    }

                    toolbar.title = if (df >= 1f) collapsedTitle else ""
                }
            )
        }
    }
}