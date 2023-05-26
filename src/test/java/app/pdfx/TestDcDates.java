package app.pdfx;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author zaro
 * Test for some bugs in pdfbox 2.0.X
 */
public class TestDcDates {


    @Test
    public void test() throws Exception {
        File temp = Files.createTempFile("test-file", ".pdf").toFile();
        temp.deleteOnExit();
        Calendar cal = Calendar.getInstance();

        // Create empty document
        try (PDDocument doc = new PDDocument()) {
            // a valid PDF document requires at least one page
            PDPage blankPage = new PDPage();
            doc.addPage(blankPage);
            XMPMetadata xmpNew = XMPMetadata.createXMPMetadata();
            DublinCoreSchema dcS = xmpNew.createAndAddDublinCoreSchema();

            dcS.addDate(cal);

            PDDocumentCatalog catalog = doc.getDocumentCatalog();
            PDMetadata metadataStream = new PDMetadata(doc);

            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmpNew, baos, true);
            metadataStream.importXMPMetadata(baos.toByteArray());
            catalog.setMetadata(metadataStream);

            doc.save(temp);
        }

        // Read the DC dates field
        PDDocumentCatalog catalog;
        try (PDDocument document = Loader.loadPDF(temp)) {
            catalog = document.getDocumentCatalog();
        }
        PDMetadata meta = catalog.getMetadata();
        XMPMetadata metadata = XmpParserProvider.get().parse(meta.createInputStream());
        DublinCoreSchema dcS = metadata.getDublinCoreSchema();

        List<Calendar> actual = dcS.getDates();

        assertEquals(1, actual.size());
        assertEquals(cal.getTimeInMillis() / 1000, actual.get(0).getTimeInMillis() / 1000);

    }

    @Test
    public void testDateFormat() throws Exception {
        String xmp = "<?xpacket begin=\"ï»¿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n" +
                "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"3.1-701\">\n" +
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                "<rdf:Description rdf:about=\"uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b\"\n" +
                "xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\">\n" +
                "<pdf:Producer>Acrobat Distiller 9.4.5 (Windows)</pdf:Producer>\n" +
                "</rdf:Description>\n" +
                "<rdf:Description rdf:about=\"uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b\"\n" +
                "xmlns:xap=\"http://ns.adobe.com/xap/1.0/\">\n" +
                "<xap:CreatorTool>3B2 Total Publishing System 8.07e/W Unicode </xap:CreatorTool>\n" +
                "<xap:ModifyDate>2011-11-22T20:24:41+08:00</xap:ModifyDate>\n" +
                "<xap:CreateDate>2011-11-20T10:09Z</xap:CreateDate>\n" +
                "<xap:MetadataDate>2011-11-22T20:24:41+08:00</xap:MetadataDate>\n" +
                "</rdf:Description>\n" +
                "<rdf:Description rdf:about=\"uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b\"\n" +
                "xmlns:xapMM=\"http://ns.adobe.com/xap/1.0/mm/\">\n" +
                "<xapMM:DocumentID>uuid:bdfff38a-a251-43cd-baed-42a7db3ec2f3</xapMM:DocumentID>\n" +
                "<xapMM:InstanceID>uuid:23ec6b59-5bb1-40ba-8e50-5e829b6be2e9</xapMM:InstanceID>\n" +
                "</rdf:Description>\n" +
                "<rdf:Description rdf:about=\"uuid:0cd65b51-c9b8-4f78-bbb6-28c4b83ff97b\"\n" +
                "xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "<dc:format>application/pdf</dc:format>\n" +
                "<dc:title>\n" +
                "<rdf:Alt>\n" +
                "<rdf:li xml:lang=\"x-default\"/>\n" +
                "</rdf:Alt>\n" +
                "</dc:title>\n" +
                "</rdf:Description>\n" +
                "</rdf:RDF>\n" +
                "</x:xmpmeta>\n" +
                "<?xpacket end=\"w\"?>";

        XMPMetadata metadata = XmpParserProvider.get().parse(xmp.getBytes());

    }

}
