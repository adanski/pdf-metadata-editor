package app.pdfx;

import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;

public class XmpParserProvider {

    public static DomXmpParser get() throws XmpParsingException {
        DomXmpParser parser = new DomXmpParser();
        parser.setStrictParsing(false);
        return parser;
    }
}
