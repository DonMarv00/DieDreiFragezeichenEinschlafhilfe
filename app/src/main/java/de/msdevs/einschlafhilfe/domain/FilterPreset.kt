package de.msdevs.einschlafhilfe.domain

import de.msdevs.einschlafhilfe.data.local.Kategorie

/**
 * Ein vordefinierter Filter. Genau EINE Art von Einschränkung aktiv:
 * - kategorien leer + numberRange null + yearsRecent null  => alles (Default)
 * - kategorien gesetzt                                     => nur diese Kategorien
 * - numberRange gesetzt (nur SERIE)                        => Ären / Jubiläum
 * - yearsRecent gesetzt                                    => letzte N Jahre
 * - jubilee = true                                         => Nummer % 25 == 0 (>=100)
 */
data class FilterPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val kategorien: Set<Kategorie> = emptySet(),
    val numberRange: IntRange? = null,
    val yearsRecent: Int? = null,
    val jubilee: Boolean = false,
    val characterAliases: List<String>? = null   // NEU
)

object FilterPresets {

    val all: List<FilterPreset> = listOf(
        // Allgemein
        FilterPreset("all", "Alle Folgen", "Keine Einschränkung"),
        FilterPreset("new", "Neue Folgen", "Letzte 3 Jahre", yearsRecent = 3),
        FilterPreset("jubilee", "Jubiläumsfolgen", "100, 125, 150 …", jubilee = true),

        // Kategorien
        FilterPreset("serie", "Hauptserie", "Die nummerierte Serie",
            kategorien = setOf(Kategorie.SERIE)),
        FilterPreset("kids", "Die drei ??? Kids", "Für jüngere Hörer",
            kategorien = setOf(Kategorie.KIDS)),
        FilterPreset("dr3i", "Die DR3i", "Eigenständige Reihe",
            kategorien = setOf(Kategorie.DR3I)),
        FilterPreset("spezial", "Spezialfolgen", "Sonderveröffentlichungen",
            kategorien = setOf(Kategorie.SPEZIAL)),
        FilterPreset("kurz", "Kurzgeschichten", "Kürzere Fälle",
            kategorien = setOf(Kategorie.KURZGESCHICHTEN)),

        // Ären (nur Hauptserie, nach Nummer)
        FilterPreset("klassiker", "Die Klassiker", "Folge 1–46",
            kategorien = setOf(Kategorie.SERIE), numberRange = 1..46),
        FilterPreset("crimebusters", "Crimebusters", "Folge 47–56",
            kategorien = setOf(Kategorie.SERIE), numberRange = 47..56),
        FilterPreset("vorlagen", "Frühe deutsche Vorlagen", "Folge 57–72",
            kategorien = setOf(Kategorie.SERIE), numberRange = 57..72),
        FilterPreset("triumvirat", "Triumvirats-Ära", "Folge 73–119",
            kategorien = setOf(Kategorie.SERIE), numberRange = 73..119),
        FilterPreset("neuzeit", "Die Neuzeit", "Ab Folge 120",
            kategorien = setOf(Kategorie.SERIE), numberRange = 120..Int.MAX_VALUE),

        //Charakter
        FilterPreset("char_skinny", "Mit Skinny Norris", "Der ewige Rivale",
            characterAliases = listOf("Skinny Norris")),
        FilterPreset("char_cotta", "Mit Inspektor Cotta", "Auch als Inspektor Milton",
            characterAliases = listOf("Cotta", "Milton")),
        FilterPreset("char_reynolds", "Mit Kommissar Reynolds", "Die Polizei von Rocky Beach",
            characterAliases = listOf("Reynolds")),
        FilterPreset("char_hugenay", "Mit Hugenay", "Der Kunstdieb",
            characterAliases = listOf("Hugenay")),
        FilterPreset("char_mathilda", "Mit Tante Mathilda", "Die Familie Jonas",
            characterAliases = listOf("Mathilda")),
        FilterPreset("char_titus", "Mit Onkel Titus", "Der Schrottplatz-Chef",
            characterAliases = listOf("Titus")),
        FilterPreset("char_morton", "Mit Morton", "Der Chauffeur",
            characterAliases = listOf("Morton")),
        FilterPreset("char_rubbish", "Mit Rubbish George", "Der Trödelhändler",
            characterAliases = listOf("Rubbish George"))

    )

    val default: FilterPreset get() = all.first()

    fun byId(id: String?): FilterPreset =
        all.firstOrNull { it.id == id } ?: default
}