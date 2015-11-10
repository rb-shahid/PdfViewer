package com.byteshaft.pdfviewer.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.byteshaft.pdfviewer.AppGlobals;

public class Helpers {

    public static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void savePreviousOpenedFile(String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(AppGlobals.LAST_FILE_KEY, value).apply();
    }

    public static String getPreviousSavedFile() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(AppGlobals.LAST_FILE_KEY, "");
    }

    public static void saveCurrentPage(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
        System.out.println("pages saved");
    }

    public static int getLastLoadedPage(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getInt(key, 0);
    }
}
