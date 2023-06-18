package io.pdfx.desktop.util

import org.apache.xmpbox.xml.DomXmpParser
import org.apache.xmpbox.xml.XmpParsingException

object XmpParserProvider {
    @Throws(XmpParsingException::class)
    fun get(): DomXmpParser {
        val parser = DomXmpParser()
        parser.isStrictParsing = false
        return parser
    }
}
