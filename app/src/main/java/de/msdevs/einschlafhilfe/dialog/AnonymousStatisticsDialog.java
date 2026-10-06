package de.msdevs.einschlafhilfe.dialog;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import de.msdevs.einschlafhilfe.R;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AnonymousStatisticsDialog {

    private static final String PREF_KEY = "anonymous_statistics";
    private static final String PREF_KEY_DECIDED = "anonymous_statistics_decided";

    public static void showIfNeeded(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Only show once
        if (prefs.getBoolean(PREF_KEY_DECIDED, false)) {
            return;
        }

        show(context);
    }

    public static void show(@NonNull Context context) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_stats, null, false);

        new MaterialAlertDialogBuilder(context,R.style.MyAlertDialogTheme)
                .setTitle(context.getString(R.string.stats_dialog_title))
                .setView(dialogView)
                .setPositiveButton(context.getString(R.string.stats_dialog_positive), (dialog, which) ->
                        saveDecision(context, true))
                .setNegativeButton(context.getString(R.string.stats_dialog_negative), (dialog, which) ->
                        saveDecision(context, false))
                .setCancelable(false)
                .show();


    }
    private static void saveDecision(@NonNull Context context, boolean accepted) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_KEY, accepted)
                .putBoolean(PREF_KEY_DECIDED, true)
                .apply();
    }

    public static boolean isEnabled(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_KEY, false);
    }
}
