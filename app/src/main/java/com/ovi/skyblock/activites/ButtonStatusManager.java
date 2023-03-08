package com.ovi.skyblock.activites;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ButtonStatusManager {
    public static void saveButtonStatus(String buttonId, boolean status, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(buttonId, status);
        editor.apply();
    }

    public static boolean getButtonStatus(String buttonId, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(buttonId, false);
    }
}
