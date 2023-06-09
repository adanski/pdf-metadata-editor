package app.pdfx

import app.pdfx.metadata.MetadataFieldType
import app.pdfx.metadata.MetadataInfo
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.Instant
import java.util.*
import java.util.function.Supplier

class MetadataInfoTest {
    class PMTuple(val file: File, val md: MetadataInfo)

    @Test
    fun `test simple equality`() {
        assertTrue(MetadataInfo().isEquivalent(MetadataInfo()))
        assertTrue(DEMO_METADATA.isEquivalent(DEMO_METADATA))
        val md1 = MetadataInfo()
        val md2 = MetadataInfo()
        md1.setAppendFromString("doc.title", "a title")
        Assertions.assertFalse(md1.isEquivalent(md2))
        md2.setAppendFromString("doc.title", md1.getString("doc.title"))
        assertTrue(md1.isEquivalent(md2))
        md1.setAppendFromString("basic.rating", "333")
        Assertions.assertFalse(md1.isEquivalent(md2))
        md2.setAppendFromString("basic.rating", "333")
        assertTrue(md1.isEquivalent(md2))
        md1.setAppendFromString("rights.marked", "true")
        Assertions.assertFalse(md1.isEquivalent(md2))
        md2.setAppendFromString("rights.marked", "true")
        assertTrue(md1.isEquivalent(md2))
    }

    @Test
    fun `test empty load`() {
        val md = MetadataInfo()
        md.loadFromPDF(emptyPdf())
        assertTrue(md.isEquivalent(MetadataInfo()))
    }

    @Test
    fun `test fuzzing`() {
        for (t in randomFiles(NUM_FILES)) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            assertTrue(t.md.isEquivalent(loaded), errorMessage(t, loaded))
        }
    }

    companion object {
        var NUM_FILES = 5

        fun emptyPdf(): File {
            val temp = Files.createTempFile("test-file", ".pdf").toFile()
            PDDocument().use { doc ->
                // a valid PDF document requires at least one page
                val blankPage = PDPage()
                doc.addPage(blankPage)
                doc.save(temp)
            }
            temp.deleteOnExit()
            return temp
        }

        fun csvFile(lines: List<String?>?): File {
            val temp = Files.createTempFile("test-csv", ".csv").toFile()
            Files.write(temp.toPath(), lines, Charset.forName("UTF-8"))
            temp.deleteOnExit()
            return temp
        }

        fun randomFiles(numFiles: Int): List<PMTuple> {
            val fields = MetadataInfo.keys()
            val numFields = fields.size
            val rval: MutableList<PMTuple> = mutableListOf()
            val rand = Random()
            for (i in 0 until numFiles) {
                val md = MetadataInfo()
                //int genFields = rand.nextInt(numFields);
                var j = 0
                while (j < numFields) {
                    val field = fields[rand.nextInt(numFields)]
                    // 	ignore file fields as they are read only
                    if (field.startsWith("file.")) {
                        --j
                        ++j
                        continue
                    }
                    if (field == "doc.trapped") {
                        md.setAppend(
                            field,
                            mutableListOf("False", "True", "Unknown")[rand.nextInt(3)]
                        )
                        ++j
                        continue
                    }
                    val fd = MetadataInfo.getFieldDescription(field)
                    when (fd!!.type) {
                        MetadataFieldType.LONG -> md.setAppend(field, rand.nextInt(1000).toLong())
                        MetadataFieldType.INT -> md.setAppend(field, rand.nextInt(1000))
                        MetadataFieldType.BOOL -> md.setAppend(
                            field,
                            rand.nextInt(1000) and 1 == 1
                        )

                        MetadataFieldType.DATE -> {
                            val d = Instant.now()
                            md.setAppend(field, d)
                        }

                        else -> md.setAppend(field, BigInteger(130, rand).toString(32))
                    }
                    ++j
                }
                val pdf = emptyPdf()
                md.saveAsPDF(pdf)
                rval.add(PMTuple(pdf, md))
            }
            return rval
        }

        private fun errorMessage(t: PMTuple, loaded: MetadataInfo): Supplier<String> {
            return Supplier {
                """
     ${t.file.absolutePath}
     SAVED:
     =========
     ${t.md.toYAML(true)}
     LOADED:
     =========
     ${loaded.toYAML(true)}
     """.trimIndent()
            }
        }
    }
}
