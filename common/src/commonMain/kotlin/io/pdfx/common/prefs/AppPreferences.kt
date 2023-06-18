package io.pdfx.common.prefs

expect val APP_PREFERENCES: AppPreferences

expect sealed class AppPreferences {

    fun putString(key: String, value: String)

    fun getString(key: String): String?

    fun getString(key: String, default: String): String

    fun node(key: String): AppPreferences
}
