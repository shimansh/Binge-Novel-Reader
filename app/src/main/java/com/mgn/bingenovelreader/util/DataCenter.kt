package com.mgn.bookmark.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mgn.bingenovelreader.model.WebPage


class DataCenter(context: Context) {

    private val PREFS_FILENAME = "com.mgn.bookmark.preferences"
    private val BOOKMARKS_LIST = "bookmarksList"
    private val CACHE_LIST = "cacheList"
    private val SEARCH_HISTORY_LIST = "searchHistoryList"
    private val IS_DARK_THEME = "isDarkTheme"


    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var bookmarksJson: String
        get() = prefs.getString(BOOKMARKS_LIST, "[]")
        set(value) = prefs.edit().putString(BOOKMARKS_LIST, value).apply()

    var cacheJson: String
        get() = prefs.getString(CACHE_LIST, "{}")
        set(value) = prefs.edit().putString(CACHE_LIST, value).apply()

    var cacheMap: HashMap<String, ArrayList<WebPage>> = HashMap()

    fun loadCacheMap() {
        cacheMap = Gson().fromJson(cacheJson, object : TypeToken<HashMap<String, ArrayList<WebPage>>>() {}.type)
    }

    fun saveCacheMap() {
        cacheJson = Gson().toJson(cacheMap)
    }

    fun loadSearchHistory(): ArrayList<String> = Gson().fromJson(prefs.getString(SEARCH_HISTORY_LIST, "[]"), object : TypeToken<ArrayList<String>>() {}.type)

    fun saveSearchHistory(history: ArrayList<String>) = prefs.edit().putString(SEARCH_HISTORY_LIST, Gson().toJson(history)).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(IS_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(IS_DARK_THEME, value).apply()

}