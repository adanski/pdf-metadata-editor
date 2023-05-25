package app.pdfx.prefs;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalDataDirTest {

    @Test
    public void test_Unix_GetAppDirectory_DirectoryExists() throws FileNotFoundException {
        final var path = LocalDataDir.getAppPath("test");
        final var file = path.toFile();

        assertTrue(file.exists());
        assertTrue(file.delete());
        assertFalse(file.exists());
    }
}
