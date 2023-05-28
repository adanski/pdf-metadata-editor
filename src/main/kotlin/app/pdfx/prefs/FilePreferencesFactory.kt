package app.pdfx.prefs

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

/**
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 *
 *
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the
 * system property <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 *
 * @author David Croft ([www.davidc.net](http://www.davidc.net))
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */
class FilePreferencesFactory : PreferencesFactory {
    var rootPreferences: Preferences? = null
    override fun systemRoot(): Preferences {
        return userRoot()
    }

    override fun userRoot(): Preferences {
        if (rootPreferences == null) {
            rootPreferences = FilePreferences(null, "")
        }
        return rootPreferences
    }

    companion object {
        private val log = LoggerFactory.getLogger(FilePreferencesFactory::class.java.name)
        const val SYSTEM_PROPERTY_FILE = "app.pdfx.prefs.FilePreferencesFactory.file"
        var preferencesFile: File? = null
            get() {
                if (field != null) {
                    return field
                }
                var prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE)
                if (prefsFile == null || prefsFile.length == 0) {
                    var prefsDir: String? = null
                    prefsDir = try {
                        LocalDataDir.getAppPath("pdf-metadata-editor").toString()
                    } catch (e: FileNotFoundException) {
                        throw RuntimeException(e)
                    }
                    val prefsDirectory = File(prefsDir)
                    if (!prefsDirectory.exists()) {
                        prefsDirectory.mkdirs()
                    }
                    prefsFile = prefsDir + File.separator + "fileprefs"
                }
                field = File(prefsFile).absoluteFile
                return field
            }
            private set
    }
}
