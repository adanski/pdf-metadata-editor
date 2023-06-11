package io.pdfx.app.prefs

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

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
