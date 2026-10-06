package de.msdevs.einschlafhilfe.domain

import de.msdevs.einschlafhilfe.data.local.Episode
import java.time.Year

object FilterEngine {

    fun apply(all: List<Episode>, preset: FilterPreset): List<Episode> {
        var result = all.asSequence()

        if (preset.kategorien.isNotEmpty()) {
            result = result.filter { it.kategorie in preset.kategorien }
        }

        preset.numberRange?.let { range ->
            result = result.filter { it.nummer != null && it.nummer in range }
        }

        preset.yearsRecent?.let { years ->
            val cutoff = Year.now().value - years
            result = result.filter { it.jahr != null && it.jahr >= cutoff }
        }

        if (preset.jubilee) {
            result = result.filter { it.nummer != null && it.nummer >= 100 && it.nummer % 25 == 0 }
        }

        return result.toList()
    }
}