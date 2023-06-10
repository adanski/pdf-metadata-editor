package app.pdfx

import app.pdfx.metadata.MetadataInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateStringTest {

    @Test
    fun `test process`() {
        val md = MetadataInfo()
        md["doc.title"] = "basic_title_1"
        md["doc.keywords"] = "basic_keywords_2_1,basic_keywords_2_2,basic_keywords_2_3"
        assertEquals("", TemplateString("{nonexistent}").process(md))
        assertEquals("{{{{", TemplateString("{{{{").process(md))
        assertEquals("{{", TemplateString("{{}{{").process(md))
        assertEquals("basic_title_1", TemplateString("{doc.title}").process(md))
        assertEquals("basic_title_1{", TemplateString("{doc.title}{").process(md))
        assertEquals("basic_title_1}", TemplateString("{doc.title}}").process(md))
        assertEquals("basic_title_1", TemplateString("{doc.title}{}").process(md))
        assertEquals(
            "basic_title_1-basic_keywords_2_1,basic_keywords_2_2,basic_keywords_2_3",
            TemplateString("{doc.title}-{doc.keywords}").process(md)
        )
        assertEquals(
            "-ba",
            TemplateString("{doc.title}-{doc.keywords}", 3).process(md)
        )
        assertEquals(
            "basi-basic_keywords_",
            TemplateString("{doc.title}-{doc.keywords}", 20).process(md)
        )
        assertEquals(
            "1234567890",
            TemplateString("1234567890", 3).process(md)
        )
        assertEquals(
            "ba1234567890",
            TemplateString("{doc.title}1234567890", 12).process(md)
        )
    }
}
