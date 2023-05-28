package app.pdfx

import app.pdfx.PDFMetadataEditBatch.ActionStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BatchCommandTest {
    @Test
    @Throws(Exception::class)
    fun testClearAll() {
        val fileList = MetadataInfoTest.randomFiles(NUM_FILES)
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("all")
        for (t in fileList) {
            args.add(t.file.absolutePath)
        }
        val c = CommandLine.parse(args)
        val batch = PDFMetadataEditBatch(c.params)
        batch.runCommand(c.command, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                Assertions.fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            Assertions.assertTrue(empty.isEquivalent(loaded))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testClearNone() {
        val fileList = MetadataInfoTest.randomFiles(NUM_FILES)
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("none")
        for (t in fileList) {
            args.add(t.file.absolutePath)
        }
        val c = CommandLine.parse(args)
        val batch = PDFMetadataEditBatch(c.params)
        batch.runCommand(c.command, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                Assertions.fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            Assertions.assertTrue(t.md.isEquivalent(loaded))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFromCSV() {
        val fileList = MetadataInfoTest.randomFiles(NUM_FILES)
        val csvLines: MutableList<String> = ArrayList()
        csvLines.add("file.fullPath,doc.author,dc.title")
        for (t in fileList) {
            csvLines.add(t.file.absolutePath + ",AUTHOR-AUTHOR,\"TITLE,TITLE\"")
        }
        val csvFile = MetadataInfoTest.csvFile(csvLines)
        val args: MutableList<String> = ArrayList()
        args.add("fromcsv")
        args.add(csvFile.absolutePath)
        val c = CommandLine.parse(args)
        val batch = PDFMetadataEditBatch(c.params)
        batch.runCommand(c.command, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                Assertions.fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            //assertTrue(t.md.isEquivalent(loaded));
            Assertions.assertEquals(loaded.doc.author, "AUTHOR-AUTHOR")
            Assertions.assertEquals(loaded.dc.title, "TITLE,TITLE")
        }
    }

    companion object {
        const val NUM_FILES = 5
    }
}
