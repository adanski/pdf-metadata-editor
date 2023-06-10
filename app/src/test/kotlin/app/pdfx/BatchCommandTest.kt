package app.pdfx

import app.pdfx.PdfMetadataEditBatch.ActionStatus
import app.pdfx.metadata.MetadataInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

private const val NUM_FILES = 5

class BatchCommandTest {

    @Test
    fun `test clear all`() {
        val fileList = MetadataInfoTest.randomFiles(NUM_FILES)
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("all")
        for (t in fileList) {
            args.add(t.file.absolutePath)
        }
        val c = CommandLine.parse(args)
        val batch = PdfMetadataEditBatch(c.params)
        batch.runCommand(c.command!!, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            assertTrue(empty.isEquivalent(loaded))
        }
    }

    @Test
    fun `test clear none`() {
        val fileList = MetadataInfoTest.randomFiles(NUM_FILES)
        val args: MutableList<String> = ArrayList()
        args.add("clear")
        args.add("none")
        for (t in fileList) {
            args.add(t.file.absolutePath)
        }
        val c = CommandLine.parse(args)
        val batch = PdfMetadataEditBatch(c.params)
        batch.runCommand(c.command!!, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            assertTrue(t.md.isEquivalent(loaded))
        }
    }

    @Test
    fun `test from CSV`() {
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
        val batch = PdfMetadataEditBatch(c.params)
        batch.runCommand(c.command!!, FileList.fileList(c.fileList), object : ActionStatus {
            override fun addStatus(filename: String, message: String) {}
            override fun addError(filename: String, error: String) {
                println(error)
                fail<Any>(error)
            }
        })
        val empty = MetadataInfo()
        for (t in fileList) {
            val loaded = MetadataInfo()
            loaded.loadFromPDF(t.file)
            //System.out.println(pdf.getAbsolutePath());
            //assertTrue(t.md.isEquivalent(loaded));
            assertEquals(loaded.doc.author, "AUTHOR-AUTHOR")
            assertEquals(loaded.dc.title, "TITLE,TITLE")
        }
    }

}
