package com.example.cheilros.helpers

import android.content.Context
import android.content.SharedPreferences

class CustomSharedPref(var context: Context) {
    var preferences: SharedPreferences? = null
    fun saveData(name: String?, value: String?) {
        val editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun getData(name: String?): String? {
        val prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        return try {
            prefs.getString(name, "")
        } catch (ex: Exception) {
            ""
        }
    }

    fun delData(name: String?) {
        val prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(name)
        editor.commit()
    }

    companion object {
        const val MY_PREFS_NAME = "ros_pref"
    }
}