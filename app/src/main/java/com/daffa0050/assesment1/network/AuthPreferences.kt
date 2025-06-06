package com.daffa0050.assesment1.network

import android.content.Context

class AuthPreference(context: Context) {
    private val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPref.getString("token", null)
    }

    fun saveToken(token: String) {
        sharedPref.edit().putString("token", token).apply()
    }
}
