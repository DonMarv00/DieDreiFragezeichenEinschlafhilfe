package de.msdevs.einschlafhilfe


import android.R
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.RangeSlider
import de.msdevs.einschlafhilfe.databinding.ActivitySettingsBinding
import de.msdevs.einschlafhilfe.utils.Utility


class SettingsActivity : BaseActivity() {

    lateinit var binding : ActivitySettingsBinding
    lateinit var switchSpotify : MaterialSwitch
    lateinit var switchUpdatelist : MaterialSwitch
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var rangeSlider : RangeSlider
    lateinit var rangeSliderKids : RangeSlider
    lateinit var tvStart : TextView
    lateinit var tvStartK : TextView
    lateinit var tvEnd: TextView
    lateinit var tvEndK : TextView
    lateinit var selectedTheme : String
    lateinit var ivCheckJustus : ImageView
    lateinit var ivCheckBob : ImageView
    lateinit var ivCheckPeter : ImageView
    lateinit var rlJustus : RelativeLayout
    lateinit var rlBob : RelativeLayout
    lateinit var rlPeter: RelativeLayout
    lateinit var btnFilter : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        if(sharedPreferences.getInt("min",0) == 0){
            rangeSlider.values = listOf(50f,200f)
            tvStart.text = "50"
            tvEnd.text = "200"
        }else{
            val x = sharedPreferences.getInt("min",0).toFloat()
            val y = sharedPreferences.getInt("max",0).toFloat()
            rangeSlider.values = listOf(x,y)

            tvStart.text = x.toString().floatToInt().toString()
            tvEnd.text = y.toString().floatToInt().toString()
        }

        if(sharedPreferences.getInt("minK",0) == 0){
            rangeSliderKids.values = listOf(1f,50f)
            tvStartK.text = "1"
            tvEndK.text = "50"
        }else{
            val x = sharedPreferences.getInt("minK",0).toFloat()
            val y = sharedPreferences.getInt("maxK",0).toFloat()
            rangeSliderKids.values = listOf(x,y)

            tvStartK.text = x.toString().floatToInt().toString()
            tvEndK.text = y.toString().floatToInt().toString()
        }

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
        onBackPressed()
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
                    // Color when the switch is checked
                    Color.parseColor("#000000"),
                    // Color when the switch is unchecked
                    Color.parseColor("#938F99")
                )
            )
            switchUpdatelist.thumbTintList = thumbTintSelector
            switchSpotify.thumbTintList = thumbTintSelector

        }else if(Utility.getTheme(this) == 2){

            val thumbTintSelector = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    // Color when the switch is checked
                    Color.parseColor("#d50000"),
                    // Color when the switch is unchecked
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
                    // Color when the switch is checked
                    Color.parseColor("#0048FF"),
                    // Color when the switch is unchecked
                    Color.parseColor("#938F99")
                )
            )
            switchUpdatelist.thumbTintList = thumbTintSelector
            switchSpotify.thumbTintList = thumbTintSelector
        }

    }
}

