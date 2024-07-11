package de.msdevs.einschlafhilfe


import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import de.msdevs.einschlafhilfe.adapter.FilterListeAdapter
import de.msdevs.einschlafhilfe.adapter.ViewPagerAdapter
import de.msdevs.einschlafhilfe.database.DatabaseHelper
import de.msdevs.einschlafhilfe.databinding.ActivityFilterBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader

class FilterActivity : BaseActivity(false), FilterListeAdapter.OnDeleteItemListener{


    private lateinit var binding : ActivityFilterBinding
    private lateinit var folgen_database: SQLiteDatabase
    private lateinit var databaseHelper: DatabaseHelper
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
        folgen_database = openOrCreateDatabase("app_list",MODE_PRIVATE,null)
        databaseHelper = DatabaseHelper(this)
        databaseHelper.createTables(folgen_database)
        networkUtils = NetworkUtils()
        viewPager = binding.viewpager
        tabLayout = binding.tablayout

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDeleteItem(name: String) {
        databaseHelper.removeFromList(folgen_database,name)

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
}