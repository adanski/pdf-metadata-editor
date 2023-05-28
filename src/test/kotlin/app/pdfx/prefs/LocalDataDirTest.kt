package app.pdfx.prefs

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

class LocalDataDirTest {
    @Test
    @Throws(FileNotFoundException::class)
    fun test_Unix_GetAppDirectory_DirectoryExists() {
        val path = LocalDataDir.getAppPath("test")
        val file = path.toFile()
        Assertions.assertTrue(file.exists())
        Assertions.assertTrue(file.delete())
        Assertions.assertFalse(file.exists())
    }
}
