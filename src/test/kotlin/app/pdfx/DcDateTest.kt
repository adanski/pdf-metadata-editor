package app.pdfx

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentCatalog
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDMetadata
import org.apache.xmpbox.XMPMetadata
import org.apache.xmpbox.xml.XmpSerializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.*

/**
 * @author zaro
 * Test for some bugs in pdfbox 2.0.X
 */
class TestDcDates {
    @Test
    @Throws(Exception::class)
    fun test() {
        val temp = Files.createTempFile("test-file", ".pdf").toFile()
        temp.deleteOnExit()
        val cal = Calendar.getInstance()
        PDDocument().use { doc ->
            // a valid PDF document requires at least one page
            val blankPage = PDPage()
            doc.addPage(blankPage)
            val xmpNew = XMPMetadata.createXMPMetadata()
            val dcS = xmpNew.createAndAddDublinCoreSchema()
            dcS.addDate(cal)
            val catalog = doc.documentCatalog
            val metadataStream = PDMetadata(doc)
            val serializer = XmpSerializer()
            val baos = ByteArrayOutputStream()
            serializer.serialize(xmpNew, baos, true)
            metadataStream.importXMPMetadata(baos.toByteArray())
            catalog.metadata = metadataStream
            doc.save(temp)
        }

        // Read the DC dates field
        var catalog: PDDocumentCatalog
        Loader.loadPDF(temp).use { document -> catalog = document.documentCatalog }
        val meta = catalog.metadata
        val metadata = XmpParserProvider.get().parse(meta.createInputStream())
        val dcS = metadata.dublinCoreSchema
        val actual = dcS.dates
        Assertions.assertEquals(1, actual.size)
        Assertions.assertEquals(cal.timeInMillis / 1000, actual[0].timeInMillis / 1000)
    }

    @Test
    @Throws(Exception::class)
    fun testDateFormat() {
        val xmp = """<?xpacket begin="ï»¿" id="W5M0MpCehiHzreSzNTczkc9d"?>
<x:xmpmeta xmlns:x="adobe:ns:meta/" x:xmptk="3.1-701">
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
<rdf:Description rdf:about="uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b"
xmlns:pdf="http://ns.adobe.com/pdf/1.3/">
<pdf:Producer>Acrobat Distiller 9.4.5 (Windows)</pdf:Producer>
</rdf:Description>
<rdf:Description rdf:about="uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b"
xmlns:xap="http://ns.adobe.com/xap/1.0/">
<xap:CreatorTool>3B2 Total Publishing System 8.07e/W Unicode </xap:CreatorTool>
<xap:ModifyDate>2011-11-22T20:24:41+08:00</xap:ModifyDate>
<xap:CreateDate>2011-11-20T10:09Z</xap:CreateDate>
<xap:MetadataDate>2011-11-22T20:24:41+08:00</xap:MetadataDate>
</rdf:Description>
<rdf:Description rdf:about="uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b"
xmlns:xapMM="http://ns.adobe.com/xap/1.0/mm/">
<xapMM:DocumentID>uuid:bdfff38a-a251-43cd-baed-42a7db3ec2f3</xapMM:DocumentID>
<xapMM:InstanceID>uuid:23ec6b59-5bb1-40ba-8e50-5e829b6be2e9</xapMM:InstanceID>
</rdf:Description>
<rdf:Description rdf:about="uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b"
xmlns:dc="http://purl.org/dc/elements/1.1/">
<dc:format>application/pdf</dc:format>
<dc:title>
<rdf:Alt>
<rdf:li xml:lang="x-default"/>
</rdf:Alt>
</dc:title>
</rdf:Description>
</rdf:RDF>
</x:xmpmeta>
<?xpacket end="w"?>"""
        val metadata = XmpParserProvider.get().parse(xmp.toByteArray())
    }
}
