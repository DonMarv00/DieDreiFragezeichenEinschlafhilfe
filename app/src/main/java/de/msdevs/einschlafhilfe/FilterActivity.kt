package de.msdevs.einschlafhilfe



import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.msdevs.einschlafhilfe.adapter.ViewPagerAdapter
import de.msdevs.einschlafhilfe.databinding.ActivityFilterBinding
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.utils.Utility


class FilterActivity : BaseActivity(false){

    private lateinit var binding : ActivityFilterBinding
    private lateinit var networkUtils: NetworkUtils
    private lateinit var toolbar : Toolbar
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        iniViews()
        toolbarDesign()

        viewPager.adapter = ViewPagerAdapter(this@FilterActivity)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> { tab.text = getString(R.string.ddf) }
                1 -> { tab.text = getString(R.string.kids) }
                else -> { tab.text = getString(R.string.die_dr3i) }
            }
        }.attach()

    }
    private fun iniViews(){
        networkUtils = NetworkUtils()
        viewPager = binding.viewpager
        tabLayout = binding.tablayout
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toolbarDesign() {
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val nav = toolbar.navigationIcon
        if (Utility.getTheme(applicationContext) <= 2) {
            toolbar.setTitleTextColor(Color.WHITE)
            nav?.setTint(Color.WHITE)
        } else if (Utility.getTheme(applicationContext) == 3) {
            toolbar.setTitleTextColor(Color.WHITE)
            nav?.setTint(Color.WHITE)
        } else if (Utility.getTheme(applicationContext) == 4) {
            toolbar.setTitleTextColor(Color.BLACK)
            nav?.setTint(Color.BLACK)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_about ->{
                showInformationDialog()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showInformationDialog(){
        val alert : MaterialAlertDialogBuilder = if (Utility.getTheme(applicationContext) <= 2) {
            MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
        } else if (Utility.getTheme(applicationContext) == 3) {
            MaterialAlertDialogBuilder(this, R.style.DialogThemeBlue)
        } else if (Utility.getTheme(applicationContext) == 4) {
            MaterialAlertDialogBuilder(this, R.style.DialogThemeWhite)
        }else{
            MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
        }

        alert.setTitle(getString(R.string.information))
        alert.setMessage(getString(R.string.filter_informations))
        alert.setNegativeButton(getString(R.string.close)) { dlg: DialogInterface, _: Int -> dlg.dismiss() }
        alert.show()
    }
}