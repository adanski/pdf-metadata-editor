package app.pdfx.prefs;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
