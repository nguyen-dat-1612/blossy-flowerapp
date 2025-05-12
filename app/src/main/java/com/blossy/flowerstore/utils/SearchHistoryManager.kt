package com.blossy.flowerstore.utils

import android.content.SharedPreferences
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val key = "history"

    fun saveQuery(query: String) {
        val historyJson = sharedPreferences.getString(key, "[]") ?: "[]"
        val historyList = JSONArray(historyJson)

        for (i in 0 until historyList.length()) {
            if (historyList.getString(i) == query) {
                historyList.remove(i)
                break
            }
        }

        val newHistory = JSONArray().apply {
            put(query)
            for (i in 0 until historyList.length()) {
                put(historyList.getString(i))
            }
        }

        while (newHistory.length() > 10) {
            newHistory.remove(newHistory.length() - 1)
        }

        sharedPreferences.edit().putString(key, newHistory.toString()).apply()
    }

    fun getHistory(): List<String> {
        val historyJson = sharedPreferences.getString(key, "[]") ?: "[]"
        val historyList = JSONArray(historyJson)
        return List(historyList.length()) { i -> historyList.getString(i) }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}
