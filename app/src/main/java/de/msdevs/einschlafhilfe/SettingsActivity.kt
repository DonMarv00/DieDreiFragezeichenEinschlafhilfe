package de.msdevs.einschlafhilfe


import android.R
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.RangeSlider
import de.msdevs.einschlafhilfe.databinding.ActivitySettingsBinding
import de.msdevs.einschlafhilfe.utils.Utility


class SettingsActivity : BaseActivity(true) {

    private lateinit var binding : ActivitySettingsBinding
    private lateinit var switchSpotify : MaterialSwitch
    private lateinit var switchUpdatelist : MaterialSwitch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var rangeSlider : RangeSlider
    private lateinit var rangeSliderKids : RangeSlider
    private lateinit var tvStart : TextView
    private lateinit var tvStartK : TextView
    private lateinit var tvEnd: TextView
    private lateinit var tvEndK : TextView
    private lateinit var selectedTheme : String
    private lateinit var ivCheckJustus : ImageView
    private lateinit var ivCheckBob : ImageView
    private lateinit var ivCheckPeter : ImageView
    private lateinit var rlJustus : RelativeLayout
    private lateinit var rlBob : RelativeLayout
    private lateinit var rlPeter: RelativeLayout
    private lateinit var btnFilter : Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        toolbarDesign()

        iniViews()

        switchSpotify.isChecked = sharedPreferences.getBoolean("spotify",false)
        switchUpdatelist.isChecked = sharedPreferences.getBoolean("update_list",false)


        switchSpotify.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("spotify",isChecked)
            sharedPreferencesEditor.apply()
        }
        switchUpdatelist.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("update_list",isChecked)
            sharedPreferencesEditor.apply()
        }


        val x = sharedPreferences.getInt("min",0).toFloat()
        val y = sharedPreferences.getInt("max",0).toFloat()
        val xD = sharedPreferences.getInt("minD",0).toFloat()
        val yD = sharedPreferences.getInt("maxD",0).toFloat()
        Log.e("SettingsActivity", "x="+ x + " y=" + y)

        rangeSlider.valueFrom = xD
        rangeSlider.valueTo = yD

        rangeSlider.values = listOf(x,y)

        tvStart.text = x.toString().floatToInt().toString()
        tvEnd.text = y.toString().floatToInt().toString()


        val xK = sharedPreferences.getInt("minK",0).toFloat()
        val yK = sharedPreferences.getInt("maxK",0).toFloat()
        val xKD = sharedPreferences.getInt("minKD",0).toFloat()
        val yKD = sharedPreferences.getInt("maxKD",0).toFloat()

        rangeSliderKids.valueFrom = xKD
        rangeSliderKids.valueTo = yKD

        rangeSliderKids.values = listOf(xK,yK)

        tvStartK.text = xK.toString().floatToInt().toString()
        tvEndK.text = yK.toString().floatToInt().toString()


        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: RangeSlider) {
                val values = rangeSlider.values
                tvStart.text =  values[0].toString().floatToInt().toString()
                tvEnd.text = values[1].toString().floatToInt().toString()

            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = rangeSlider.values
                tvStart.text =  values[0].toString().floatToInt().toString()
                tvEnd.text = values[1].toString().floatToInt().toString()

                sharedPreferencesEditor.putInt("min",values[0].toInt())
                sharedPreferencesEditor.putInt("max",values[1].toInt())
                sharedPreferencesEditor.apply()
            }
        })
        rangeSliderKids.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: RangeSlider) {
                val values = rangeSliderKids.values
                tvStartK.text =  values[0].toString().floatToInt().toString()
                tvEndK.text = values[1].toString().floatToInt().toString()

            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = rangeSliderKids.values
                tvStartK.text =  values[0].toString().floatToInt().toString()
                tvEndK.text = values[1].toString().floatToInt().toString()

                sharedPreferencesEditor.putInt("minK",values[0].toInt())
                sharedPreferencesEditor.putInt("maxK",values[1].toInt())
                sharedPreferencesEditor.apply()
            }
        })
        btnFilter.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, FilterActivity::class.java))
        }


        rlJustus.setOnClickListener{
            selectedTheme = "justus"
            loadThemeSettings()

            Utility.setTheme(applicationContext, 4);
            recreateActivity();
        }
        rlBob.setOnClickListener{
            selectedTheme = "bob"
            loadThemeSettings()

            Utility.setTheme(applicationContext, 2);
            recreateActivity();
        }
        rlPeter.setOnClickListener{
            selectedTheme = "peter"
            loadThemeSettings()

            Utility.setTheme(applicationContext, 3);
            recreateActivity();
        }
    }
    fun iniViews(){
        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()
        selectedTheme = sharedPreferences.getString("selected_theme","").toString()

        switchSpotify = binding.swUseSpotify
        switchUpdatelist = binding.swUpdateList

        rangeSlider = binding.rangeSlider
        rangeSliderKids = binding.rangeSliderKids
        tvStart = binding.tvStart
        tvStartK = binding.tvStartKids
        tvEnd = binding.tvEnd
        tvEndK = binding.tvEndKids
        ivCheckJustus = binding.ivCheckJustus
        ivCheckPeter = binding.ivCheckPeter
        ivCheckBob = binding.ivCheckBob

        btnFilter = binding.btnFilter

        rlJustus = binding.rlJustus
        rlBob = binding.rlBob
        rlPeter = binding.rlPeter

        loadThemeSettings()
        changeViewThemes()
    }
    fun loadThemeSettings(){
        sharedPreferencesEditor.putString("selected_theme",selectedTheme)
        sharedPreferencesEditor.putInt("theme_changed",1)
        sharedPreferencesEditor.apply()

        if(selectedTheme.length == 0 || selectedTheme == "bob"){
            ivCheckBob.visibility = View.VISIBLE
            ivCheckPeter.visibility = View.GONE
            ivCheckJustus.visibility = View.GONE
        }else if(selectedTheme == "justus"){
            ivCheckJustus.visibility = View.VISIBLE
            ivCheckPeter.visibility = View.GONE
            ivCheckBob.visibility = View.GONE
        }else if(selectedTheme == "peter"){
            ivCheckPeter.visibility = View.VISIBLE
            ivCheckBob.visibility = View.GONE
            ivCheckJustus.visibility = View.GONE
        }
    }
    fun String.floatToInt(): Int {
        return this.toFloat().toInt()
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    fun recreateActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)

    }
    fun changeViewThemes(){
        if(Utility.getTheme(this) == 4){
            val thumbTintSelector = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    Color.parseColor("#000000"),
                    Color.parseColor("#938F99")
                )
            )
            switchUpdatelist.thumbTintList = thumbTintSelector
            switchSpotify.thumbTintList = thumbTintSelector

        }else if(Utility.getTheme(this) == 2  ||  selectedTheme.length == 0){
            val thumbTintSelector = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    Color.parseColor("#d50000"),
                    Color.parseColor("#938F99")
                )
            )
            switchUpdatelist.thumbTintList = thumbTintSelector
            switchSpotify.thumbTintList = thumbTintSelector
        }else if(Utility.getTheme(this) == 3){
            val thumbTintSelector = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    Color.parseColor("#0048FF"),
                    Color.parseColor("#938F99")
                )
            )
            switchUpdatelist.thumbTintList = thumbTintSelector
            switchSpotify.thumbTintList = thumbTintSelector
        }

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

