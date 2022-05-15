package de.msdevs.einschlafhilfe.fragments


import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import de.msdevs.einschlafhilfe.R
import de.msdevs.einschlafhilfe.databinding.FragmentSettingsBinding


class SettingsFragment(override val isPolicyRespected: Boolean) : Fragment(), SlidePolicy {

    lateinit var switchSpotify : SwitchCompat
    lateinit var switchUpdatelist : SwitchCompat
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
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
        TODO("Not yet implemented")
    }

}


