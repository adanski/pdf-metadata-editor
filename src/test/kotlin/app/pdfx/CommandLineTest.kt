package app.pdfx

import app.pdfx.CommandLine.ParseError
import app.pdfx.metadata.MetadataInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

private val MD_FIELD_LIST = listOf(
    "doc.title", "doc.author", "doc.subject", "doc.keywords",
    "doc.creator", "doc.producer", "doc.creationDate", "doc.modificationDate", "doc.trapped",
    "basic.creatorTool", "basic.createDate", "basic.modifyDate", "basic.baseURL",
    "basic.rating", "basic.label", "basic.nickname", "basic.identifiers", "basic.advisories",
    "basic.metadataDate", "pdf.pdfVersion", "pdf.keywords", "pdf.producer", "dc.title",
    "dc.description", "dc.creators", "dc.contributors", "dc.coverage", "dc.dates",
    "dc.format", "dc.identifier", "dc.languages", "dc.publishers", "dc.relationships",
    "dc.rights", "dc.source", "dc.subjects", "dc.types"
)

class CommandLineTest {

    @Test
    fun `test valid`() {
        val c = CommandLine.parse(
            arrayOf(
                "-nogui", "edit", "--", "file1", "file2"
            )
        )
        assertNotNull(c)
        assertTrue(c.noGui)
        assertTrue(c.command!!.`is`("edit"))
        assertEquals(c.fileList, mutableListOf("file1", "file2"))
    }

    @Test
    fun `test clear`() {
        val args: List<String> = listOf("clear") + MD_FIELD_LIST
        val c = CommandLine.parse(args)
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNotNull(c.command)
        assertTrue(c.command!!.`is`("clear"))
        for (field in MD_FIELD_LIST) {
            assertTrue(c.params.metadata.isEnabled(field))
        }
        assertTrue(c.fileList.isEmpty())
    }

    @Test
    fun `test clear none`() {
        val args: List<String> = listOf("clear", "none")
        val c = CommandLine.parse(args)
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNotNull(c.command)
        assertTrue(c.command!!.`is`("clear"))
        for (field in MD_FIELD_LIST) {
            assertFalse(c.params.metadata.isEnabled(field))
        }
        assertTrue(c.fileList.isEmpty())
    }

    @Test
    fun `test clear all`() {
        val args: List<String> = listOf("clear", "all")
        val c = CommandLine.parse(args)
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNotNull(c.command)
        assertTrue(c.command!!.`is`("clear"))
        for (field in MD_FIELD_LIST) {
            assertTrue(c.params.metadata.isEnabled(field))
        }
        assertTrue(c.fileList.isEmpty())
    }

    @Test
    fun `test clear some`() {
        val args: List<String> = listOf("clear", "all", "!doc.title")
        val c = CommandLine.parse(args)
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNotNull(c.command)
        assertTrue(c.command!!.`is`("clear"))
        for (field in MD_FIELD_LIST) {
            if (field == "doc.title") {
                assertFalse(c.params.metadata.isEnabled(field))
            } else {
                assertTrue(c.params.metadata.isEnabled(field))
            }
        }
        assertTrue(c.fileList.isEmpty())
    }

    @Test
    fun `test edit all`() {
        val dateString = "2012-04-03"
        val cal = Instant.parse("${dateString}T00:00:00Z")
        val genList: MutableList<String> = ArrayList()
        val md = MetadataInfo()
        for (field in MD_FIELD_LIST) {
            if (field.endsWith("Date")) {
                genList.add("$field=$dateString")
            } else if (field.endsWith(".dates")) {
                genList.add("$field=$dateString")
                genList.add("$field=$dateString")
            } else if (field.endsWith(".rating")) {
                genList.add("$field=17")
            } else if (MetadataInfo.getFieldDescription(field)!!.list) {
                genList.add("$field=$field")
                genList.add("$field=$field")
            } else {
                genList.add("$field=$field")
            }
        }
        val args: List<String> = listOf("edit") + genList
        val c = CommandLine.parse(args)
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNotNull(c.command)
        assertTrue(c.command!!.`is`("edit"))
        for (field in MD_FIELD_LIST) {
            assertTrue(c.params.metadata.isEnabled(field))
            if (field.endsWith("Date")) {
                assertEquals(cal, c.params.metadata[field] as Instant)
            } else if (field.endsWith(".dates")) {
                assertEquals(listOf(cal, cal), c.params.metadata[field])
            } else if (field.endsWith(".rating")) {
                assertEquals(17, c.params.metadata[field])
            } else if (MetadataInfo.getFieldDescription(field)!!.list) {
                assertEquals(listOf(field, field), c.params.metadata[field])
            } else {
                assertEquals(field, c.params.metadata[field])
            }
        }
        assertTrue(c.fileList.isEmpty())
    }

    @Test
    fun `test valid 2`() {
        val c = CommandLine.parse(
            arrayOf(
                "doc.title=title"
            )
        )
        assertNotNull(c)
        assertFalse(c.noGui)
        assertNull(c.command)
        assertEquals(c.params.metadata.doc.title, "title")
    }

    @Test
    fun `test invalid 1`() {
        assertThrows(ParseError::class.java) {
            val c = CommandLine.parse(
                arrayOf(
                    "--something", "editv", "doc.creationDate"
                )
            )
        }
    }

    @Test
    fun testInvalid2() {
        assertThrows(ParseError::class.java) {
            val c = CommandLine.parse(
                arrayOf(
                    "--renameTemplate"
                )
            )
        }
    }
}
