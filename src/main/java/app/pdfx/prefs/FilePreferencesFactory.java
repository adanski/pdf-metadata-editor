package app.pdfx.prefs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 * <p/>
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the
 * system property <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 *
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */
public class FilePreferencesFactory implements PreferencesFactory {
    private static final Logger log = LoggerFactory.getLogger(FilePreferencesFactory.class.getName());

    Preferences rootPreferences;
    public static final String SYSTEM_PROPERTY_FILE = "app.pdfx.prefs.FilePreferencesFactory.file";

    public Preferences systemRoot() {
        return userRoot();
    }

    public Preferences userRoot() {
        if (rootPreferences == null) {

            rootPreferences = new FilePreferences(null, "");
        }
        return rootPreferences;
    }

    private static File preferencesFile;

    public static File getPreferencesFile() {
        if (preferencesFile != null) {
            return preferencesFile;
        }

        String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);
        if (prefsFile == null || prefsFile.length() == 0) {
            String prefsDir = null;
            try {
                prefsDir = LocalDataDir.getAppPath("pdf-metadata-editor").toString();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            File prefsDirectory = new File(prefsDir);
            if (!prefsDirectory.exists()) {
                prefsDirectory.mkdirs();
            }
            prefsFile = prefsDir + File.separator + "fileprefs";
        }
        preferencesFile = new File(prefsFile).getAbsoluteFile();

        return preferencesFile;
    }

}
