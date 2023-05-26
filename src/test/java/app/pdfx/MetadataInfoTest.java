package app.pdfx;

import app.pdfx.MetadataInfo.FieldDescription;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataInfoTest {
    static int NUM_FILES = 5;

    public static class PMTuple {
        final File file;
        final MetadataInfo md;

        public PMTuple(File file, MetadataInfo md) {
            this.file = file;
            this.md = md;
        }
    }

    public static File emptyPdf() throws Exception {
        File temp = Files.createTempFile("test-file", ".pdf").toFile();
        PDDocument doc = new PDDocument();
        try {
            // a valid PDF document requires at least one page
            PDPage blankPage = new PDPage();
            doc.addPage(blankPage);
            doc.save(temp);
        } finally {
            doc.close();
        }
        temp.deleteOnExit();
        return temp;
    }

    public static File csvFile(List<String> lines) throws Exception {
        File temp = Files.createTempFile("test-csv", ".csv").toFile();
        Files.write(temp.toPath(), lines, Charset.forName("UTF-8"));
        temp.deleteOnExit();
        return temp;
    }


    public static List<PMTuple> randomFiles(int numFiles) throws Exception {
        List<String> fields = MetadataInfo.keys();
        int numFields = fields.size();
        List<PMTuple> rval = new ArrayList<MetadataInfoTest.PMTuple>();

        Random rand = new Random();
        for (int i = 0; i < numFiles; ++i) {
            MetadataInfo md = new MetadataInfo();
            //int genFields = rand.nextInt(numFields);
            int genFields = numFields;
            for (int j = 0; j < genFields; ++j) {
                String field = fields.get(rand.nextInt(numFields));
                // 	ignore file fields as they are read only
                if (field.startsWith("file.")) {
                    --j;
                    continue;
                }
                if (field.equals("doc.trapped")) {
                    md.setAppend(field, Arrays.asList("False", "True", "Unknown").get(rand.nextInt(3)));
                    continue;
                }
                FieldDescription fd = MetadataInfo.getFieldDescription(field);
                switch (fd.type) {
                    case LONG -> md.setAppend(field, (long) rand.nextInt(1000));
                    case INT -> md.setAppend(field, rand.nextInt(1000));
                    case BOOL -> md.setAppend(field, ((rand.nextInt(1000) & 1) == 1) ? true : false);
                    case DATE -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setLenient(false);
                        md.setAppend(field, cal);
                    }
                    default -> md.setAppend(field, new BigInteger(130, rand).toString(32));
                }
            }
            File pdf = emptyPdf();
            md.saveAsPDF(pdf);
            rval.add(new PMTuple(pdf, md));
        }
        return rval;
    }

    @Test
    public void testSimpleEquality() {
        assertTrue(new MetadataInfo().isEquivalent(new MetadataInfo()));
        assertTrue(MetadataInfo.getSampleMetadata().isEquivalent(MetadataInfo.getSampleMetadata()));

        MetadataInfo md1 = new MetadataInfo();
        MetadataInfo md2 = new MetadataInfo();


        md1.setAppendFromString("doc.title", "a title");
        assertFalse(md1.isEquivalent(md2));

        md2.setAppendFromString("doc.title", md1.getString("doc.title"));
        assertTrue(md1.isEquivalent(md2));

        md1.setAppendFromString("basic.rating", "333");
        assertFalse(md1.isEquivalent(md2));

        md2.setAppendFromString("basic.rating", "333");
        assertTrue(md1.isEquivalent(md2));

        md1.setAppendFromString("rights.marked", "true");
        assertFalse(md1.isEquivalent(md2));

        md2.setAppendFromString("rights.marked", "true");
        assertTrue(md1.isEquivalent(md2));
    }

    @Test
    public void testEmptyLoad() throws Exception {
        MetadataInfo md = new MetadataInfo();
        md.loadFromPDF(emptyPdf());
        assertTrue(md.isEquivalent(new MetadataInfo()));
    }

    @Test
    public void testFuzzing() throws Exception {
        for (PMTuple t : randomFiles(NUM_FILES)) {
            MetadataInfo loaded = new MetadataInfo();
            loaded.loadFromPDF(t.file);

            assertTrue(t.md.isEquivalent(loaded), errorMessage(t, loaded));
        }
    }

    private static Supplier<String> errorMessage(PMTuple t, MetadataInfo loaded) {
        return () -> t.file.getAbsolutePath()
                + "\nSAVED:"
                + "\n=========\n"
                + t.md.toYAML(true)
                + "\nLOADED:"
                + "\n=========\n"
                + loaded.toYAML(true);
    }

}
