package app.pdfx.prefs

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LocalDataDirTest {

    @Test
    fun `should get app data directory`() {
        val path = LocalDataDir.getAppPath("test")
        val file = path.toFile()
        assertTrue(file.exists())
        assertTrue(file.delete())
        assertFalse(file.exists())
    }
}
