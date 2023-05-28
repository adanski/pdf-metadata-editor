package app.pdfx

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TemplateStringTest {
    @Test
    fun testProcess() {
        val md = MetadataInfo()
        md["doc.title"] = "basic_title_1"
        md["doc.keywords"] = "basic_keywords_2_1,basic_keywords_2_2,basic_keywords_2_3"
        Assertions.assertEquals("", TemplateString("{nonexistent}").process(md))
        Assertions.assertEquals("{{{{", TemplateString("{{{{").process(md))
        Assertions.assertEquals("{{", TemplateString("{{}{{").process(md))
        Assertions.assertEquals("basic_title_1", TemplateString("{doc.title}").process(md))
        Assertions.assertEquals("basic_title_1{", TemplateString("{doc.title}{").process(md))
        Assertions.assertEquals("basic_title_1}", TemplateString("{doc.title}}").process(md))
        Assertions.assertEquals("basic_title_1", TemplateString("{doc.title}{}").process(md))
        Assertions.assertEquals(
            "basic_title_1-basic_keywords_2_1,basic_keywords_2_2,basic_keywords_2_3",
            TemplateString("{doc.title}-{doc.keywords}").process(md)
        )
        Assertions.assertEquals(
            "-ba",
            TemplateString("{doc.title}-{doc.keywords}", 3).process(md)
        )
        Assertions.assertEquals(
            "basi-basic_keywords_",
            TemplateString("{doc.title}-{doc.keywords}", 20).process(md)
        )
        Assertions.assertEquals(
            "1234567890",
            TemplateString("1234567890", 3).process(md)
        )
        Assertions.assertEquals(
            "ba1234567890",
            TemplateString("{doc.title}1234567890", 12).process(md)
        )
    }
}
