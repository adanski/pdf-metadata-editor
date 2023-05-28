package app.pdfx

import app.pdfx.CommandLine.ParseError
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class CommandLineTest {
    var mdFieldList = Arrays.asList(
        *arrayOf(
            "doc.title", "doc.author", "doc.subject", "doc.keywords",
            "doc.creator", "doc.producer", "doc.creationDate", "doc.modificationDate", "doc.trapped",
            "basic.creatorTool", "basic.createDate", "basic.modifyDate", "basic.baseURL",
            "basic.rating", "basic.label", "basic.nickname", "basic.identifiers", "basic.advisories",
            "basic.metadataDate", "pdf.pdfVersion", "pdf.keywords", "pdf.producer", "dc.title",
            "dc.description", "dc.creators", "dc.contributors", "dc.coverage", "dc.dates",
            "dc.format", "dc.identifier", "dc.languages", "dc.publishers", "dc.relationships",
            "dc.rights", "dc.source", "dc.subjects", "dc.types"
        )
    )

    @Test
    @Throws(ParseError::class)
    fun testValid() {
        val c = CommandLine.parse(
            arrayOf(
                "-nogui", "edit", "--", "file1", "file2"
            )
        )
        Assertions.assertNotNull(c)
        Assertions.assertTrue(c.noGui)
        Assertions.assertTrue(c.command.`is`("edit"))
        Assertions.assertEquals(c.fileList, mutableListOf("file1", "file2"))
    }

    @Test
    @Throws(ParseError::class)
    fun testClear() {
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.addAll(mdFieldList)
        val c = CommandLine.parse(args)
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNotNull(c.command)
        Assertions.assertTrue(c.command.`is`("clear"))
        for (field in mdFieldList) {
            Assertions.assertTrue(c.params.metadata.isEnabled(field))
        }
        Assertions.assertTrue(c.fileList.isEmpty())
    }

    @Test
    @Throws(ParseError::class)
    fun testClearNone() {
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("none")
        val c = CommandLine.parse(args)
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNotNull(c.command)
        Assertions.assertTrue(c.command.`is`("clear"))
        for (field in mdFieldList) {
            Assertions.assertFalse(c.params.metadata.isEnabled(field))
        }
        Assertions.assertTrue(c.fileList.isEmpty())
    }

    @Test
    @Throws(ParseError::class)
    fun testClearAll() {
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("all")
        val c = CommandLine.parse(args)
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNotNull(c.command)
        Assertions.assertTrue(c.command.`is`("clear"))
        for (field in mdFieldList) {
            Assertions.assertTrue(c.params.metadata.isEnabled(field))
        }
        Assertions.assertTrue(c.fileList.isEmpty())
    }

    @Test
    @Throws(ParseError::class)
    fun testClearSome() {
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("all")
        args.add("!doc.title")
        val c = CommandLine.parse(args)
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNotNull(c.command)
        Assertions.assertTrue(c.command.`is`("clear"))
        for (field in mdFieldList) {
            if (field == "doc.title") {
                Assertions.assertFalse(c.params.metadata.isEnabled(field))
            } else {
                Assertions.assertTrue(c.params.metadata.isEnabled(field))
            }
        }
        Assertions.assertTrue(c.fileList.isEmpty())
    }

    @Test
    @Throws(ParseError::class)
    fun testEditAll() {
        val cal = Calendar.getInstance()
        cal.clear()
        cal[2012, 3] = 3
        val dateString = "2012-04-03"
        val genList: MutableList<String> = ArrayList()
        val md = MetadataInfo()
        for (field in mdFieldList) {
            if (field.endsWith("Date")) {
                genList.add("$field=$dateString")
            } else if (field.endsWith(".dates")) {
                genList.add("$field=$dateString")
                genList.add("$field=$dateString")
            } else if (field.endsWith(".rating")) {
                genList.add("$field=17")
            } else if (MetadataInfo.getFieldDescription(field).isList) {
                genList.add("$field=$field")
                genList.add("$field=$field")
            } else {
                genList.add("$field=$field")
            }
        }
        val args: MutableList<String> = ArrayList()
        args.add("edit")
        args.addAll(genList)
        val c = CommandLine.parse(args)
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNotNull(c.command)
        Assertions.assertTrue(c.command.`is`("edit"))
        for (field in mdFieldList) {
            Assertions.assertTrue(c.params.metadata.isEnabled(field))
            if (field.endsWith("Date")) {
                Assertions.assertEquals(cal, c.params.metadata[field] as Calendar)
            } else if (field.endsWith(".dates")) {
                Assertions.assertEquals(Arrays.asList(cal, cal), c.params.metadata[field])
            } else if (field.endsWith(".rating")) {
                Assertions.assertEquals(17, c.params.metadata[field])
            } else if (MetadataInfo.getFieldDescription(field).isList) {
                Assertions.assertEquals(Arrays.asList(field, field), c.params.metadata[field])
            } else {
                Assertions.assertEquals(field, c.params.metadata[field])
            }
        }
        Assertions.assertTrue(c.fileList.isEmpty())
    }

    @Test
    @Throws(ParseError::class)
    fun testValid2() {
        val c = CommandLine.parse(
            arrayOf(
                "doc.title=title"
            )
        )
        Assertions.assertNotNull(c)
        Assertions.assertFalse(c.noGui)
        Assertions.assertNull(c.command)
        Assertions.assertEquals(c.params.metadata.doc.title, "title")
    }

    @Test
    fun testInvalid1() {
        Assertions.assertThrows(ParseError::class.java) {
            val c = CommandLine.parse(
                arrayOf(
                    "--something", "editv", "doc.creationDate"
                )
            )
        }
    }

    @Test
    fun testInvalid2() {
        Assertions.assertThrows(ParseError::class.java) {
            val c = CommandLine.parse(
                arrayOf(
                    "--renameTemplate"
                )
            )
        }
    }
}
