package com.ikaratruyen.utils;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * Preference utilities support for access/save shared reference
 * @author bado
 *
 */
public class IkaraPreferences {
    /**
     * Save specific string to SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param prefName Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void saveStringPref(Context context, String key,
            String value) {
        
        if(context == null) return;

        SharedPreferences pref = context.getSharedPreferences(
                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Save specific int to SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void saveIntPref(Context context, String key,
            int value) {
        if(context == null) return;

        SharedPreferences pref = context.getSharedPreferences(
                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    public static void saveFloatPref(Context context, String key,
            float value) {
        if(context == null) return;

        SharedPreferences pref = context.getSharedPreferences(
                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * Save specific long to SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void saveLongPref(Context context, String key,
            long value) {
        if(context == null) return;

        SharedPreferences pref = context.getSharedPreferences(
                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    
//    public static void clear(Context context, String prefName){
//    	SharedPreferences pref = context.getSharedPreferences(
//                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
//    	SharedPreferences.Editor editor = pref.edit();
//    	editor.clear();
//    	editor.commit();
//    	
//    }

    /**
     * Save specific bool to SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param prefName Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void saveBoolPref(Context context, String key,
            boolean value) {
        if(context == null) return;

        SharedPreferences pref = context.getSharedPreferences(
                PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    

    /**
     * get specific bool from SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param defValue The default value if reference not exist
     */
    public static boolean getBoolPref(Context context, String key, boolean defValue) {
        if(context == null) return defValue;

        SharedPreferences settings = context.getSharedPreferences(PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        return settings.getBoolean(key, defValue);
    }

    /**
     * get specific int from SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param defValue The default value if reference not exist
     */
    public static int getIntPref(Context context, String key, int defValue) {
        if(context == null) return defValue;

        SharedPreferences settings = context.getSharedPreferences(PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        return settings.getInt(key, defValue);
    }
    
    public static float getFloatPref(Context context, String key, float defValue) {
        if(context == null) return defValue;

        SharedPreferences settings = context.getSharedPreferences(PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        return settings.getFloat(key, defValue);
    }

    /**
     * get specific string from SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param defValue The default value if reference not exist
     */
    public static String getStringPref(Context context, String key, String defValue) {
        if(context == null) return defValue;
        String result = null;
        SharedPreferences settings = context.getSharedPreferences(PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        result = settings.getString(key, defValue);
        return result;
    }

    /**
     * get specific long from SharedPreferences
     * @param context The context of the preferences whose values are wanted.
     * @param name Desired preferences file. If a preferences file by this name does not exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit())
     * @param key The name of the preference to modify.
     * @param defValue The default value if reference not exist
     */
    public static long getLongPref(Context context, String key, long defValue) {    
        if(context == null) return defValue;

        SharedPreferences settings = context.getSharedPreferences(PrefConstant.PREF_NAME, /* MODE_PRIVATE */0);
        return settings.getLong(key, defValue);

    }
}
