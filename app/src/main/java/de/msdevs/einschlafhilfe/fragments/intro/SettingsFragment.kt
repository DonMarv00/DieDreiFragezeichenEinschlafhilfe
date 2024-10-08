package de.msdevs.einschlafhilfe.fragments.intro


import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import com.google.android.material.materialswitch.MaterialSwitch
import de.msdevs.einschlafhilfe.databinding.FragmentSettingsBinding


class SettingsFragment(override val isPolicyRespected: Boolean) : Fragment(), SlidePolicy {

    private lateinit var switchSpotify : MaterialSwitch
    private lateinit var switchUpdatelist : MaterialSwitch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(requireActivity().packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()

        switchSpotify = binding.swUseSpotify
        switchUpdatelist = binding.swUpdateList

        switchSpotify.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("spotify",isChecked)
            sharedPreferencesEditor.apply()
        }
        switchUpdatelist.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesEditor.putBoolean("update_list",isChecked)
            sharedPreferencesEditor.apply()
        }
    }
    companion object {
        fun newInstance() : SettingsFragment {
            return SettingsFragment(true)
        }
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

}


