package de.msdevs.einschlafhilfe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.slider.RangeSlider
import de.msdevs.einschlafhilfe.databinding.ActivitySettingsBinding
import org.w3c.dom.Text
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    lateinit var binding : ActivitySettingsBinding
    lateinit var switchSpotify : SwitchCompat
    lateinit var switchUpdatelist : SwitchCompat
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var rangeSlider : RangeSlider
    lateinit var rangeSliderKids : RangeSlider
    lateinit var cbDDF : CheckBox
    lateinit var cbDDFK : CheckBox
    lateinit var cbSpecialDDF : CheckBox
    lateinit var cbSpecialDDFK : CheckBox
    lateinit var cbD3 : CheckBox
    lateinit var tvStart : TextView
    lateinit var tvStartK : TextView
    lateinit var tvEnd: TextView
    lateinit var tvEndK : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()

        switchSpotify = binding.swUseSpotify
        switchUpdatelist = binding.swUpdateList

        rangeSlider = binding.rangeSlider
        rangeSliderKids = binding.rangeSliderKids
        cbDDF = binding.cbDDF
        cbDDFK = binding.cbKids
        cbD3 = binding.cbD3
        cbSpecialDDF = binding.cbSpecial
        cbSpecialDDFK = binding.cbSpecialK
        tvStart = binding.tvStart
        tvStartK = binding.tvStartKids
        tvEnd = binding.tvEnd
        tvEndK = binding.tvEndKids

        switchSpotify.isChecked = sharedPreferences.getBoolean("spotify",false)
        switchUpdatelist.isChecked = sharedPreferences.getBoolean("update_list",false)

        cbSpecialDDFK.isChecked = sharedPreferences.getBoolean("ddfks",false)
        cbSpecialDDF.isChecked = sharedPreferences.getBoolean("ddfs",false)
        cbD3.isChecked = sharedPreferences.getBoolean("d3",false)
        cbDDF.isChecked = sharedPreferences.getBoolean("ddf",false)
        cbDDFK.isChecked = sharedPreferences.getBoolean("ddfk",false)

        switchSpotify.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("spotify",isChecked)
            sharedPreferencesEditor.apply()
        }
        switchUpdatelist.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("update_list",isChecked)
            sharedPreferencesEditor.apply()
        }
        cbDDF.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("ddf",isChecked)
            sharedPreferencesEditor.apply()
        }
        cbDDFK.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("ddfk", isChecked)
            sharedPreferencesEditor.apply()
        }
        cbSpecialDDF.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("ddfs",isChecked)
            sharedPreferencesEditor.apply()
        }
        cbSpecialDDFK.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("ddfks",isChecked)
            sharedPreferencesEditor.apply()
        }
        cbD3.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("d3",isChecked)
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
    }
    fun String.floatToInt(): Int {
        return this.toFloat().toInt()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

