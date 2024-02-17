package de.msdevs.einschlafhilfe
import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsActivity


class AboutLibrariesActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent = LibsBuilder()
            .withEdgeToEdge(true)
            .withAboutMinimalDesign(true)
            .withActivityTitle(getString(R.string.about_libs))
            .withSearchEnabled(true)
            .intent(this)
        super.onCreate(savedInstanceState)
    }

}