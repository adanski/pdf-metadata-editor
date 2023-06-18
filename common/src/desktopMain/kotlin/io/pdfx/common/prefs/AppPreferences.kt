package io.pdfx.common.prefs

import java.util.prefs.Preferences

actual val APP_PREFERENCES: AppPreferences = RootPreferences

actual sealed class AppPreferences {

    protected abstract val preferences: Preferences

    actual fun putString(key: String, value: String) {
        preferences.put(key, value)
    }

    actual fun getString(key: String): String? {
        return preferences.get(key, null)
    }

    actual fun getString(key: String, default: String): String {
        return preferences.get(key, default)
    }

    actual fun node(key: String): AppPreferences {
        return NodePreferences(preferences.node(key))
    }

}

internal object RootPreferences : AppPreferences() {
    override val preferences: Preferences by lazy(LazyThreadSafetyMode.PUBLICATION) {
        System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory::class.java.name)
        Preferences.userRoot().node("pdfxMetadataEditor")
    }
}

internal class NodePreferences(override val preferences: Preferences) : AppPreferences()
