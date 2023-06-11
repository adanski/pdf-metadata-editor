package app.pdfx.metadata

import app.pdfx.DateFormat.formatDateTimeFull
import app.pdfx.TemplateString
import app.pdfx.XmpParserProvider
import app.pdfx.metadata.annotation.MdStruct
import app.pdfx.metadata.annotation.MdStruct.Type
import app.pdfx.metadata.struct.*
import app.pdfx.toCalendar
import app.pdfx.toInstants
import com.google.gson.GsonBuilder
import org.apache.pdfbox.Loader
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDMetadata
import org.apache.xmpbox.XMPMetadata
import org.apache.xmpbox.type.BadFieldValueException
import org.apache.xmpbox.xml.XmpParsingException
import org.apache.xmpbox.xml.XmpSerializer
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.util.*
import java.util.function.Function
import javax.xml.transform.TransformerException

private val log = LoggerFactory.getLogger(MetadataInfo::class.java)

@MdStruct(type = Type.ROOT_STRUCT)
class MetadataInfo {

    @field:MdStruct
    var doc: Basic

    @field:MdStruct
    var basic: XmpBasic

    @field:MdStruct
    var pdf: XmpPdf

    @field:MdStruct
    var dc: XmpDublinCore

    @field:MdStruct
    var rights: XmpRights

    @field:MdStruct(name = "file", access = MdStruct.Access.READ_ONLY)
    var file: FileInfo

    @field:MdStruct(name = "doc", type = Type.CHILD_ENABLE_STRUCT)
    var docEnabled: BasicEnabled

    @field:MdStruct(name = "basic", type = Type.CHILD_ENABLE_STRUCT)
    var basicEnabled: XmpBasicEnabled

    @field:MdStruct(name = "pdf", type = Type.CHILD_ENABLE_STRUCT)
    var pdfEnabled: XmpPdfEnabled

    @field:MdStruct(name = "dc", type = Type.CHILD_ENABLE_STRUCT)
    var dcEnabled: XmpDublinCoreEnabled

    @field:MdStruct(name = "rights", type = Type.CHILD_ENABLE_STRUCT)
    var rightsEnabled: XmpRightsEnabled

    @field:MdStruct(name = "file", type = Type.CHILD_ENABLE_STRUCT, access = MdStruct.Access.READ_ONLY)
    var fileEnabled: FileInfoEnabled

    init {
        doc = Basic()
        basic = XmpBasic()
        pdf = XmpPdf()
        dc = XmpDublinCore()
        rights = XmpRights()
        file = FileInfo()
        docEnabled = BasicEnabled()
        basicEnabled = XmpBasicEnabled()
        pdfEnabled = XmpPdfEnabled()
        dcEnabled = XmpDublinCoreEnabled()
        rightsEnabled = XmpRightsEnabled()
        fileEnabled = FileInfoEnabled()
    }

    @Throws(IOException::class, XmpParsingException::class, BadFieldValueException::class)
    private fun loadFromPDF(document: PDDocument) {
        val info = document.documentInformation

        // Basic info
        doc.apply {
            title = info.title
            author = info.author
            subject = info.subject
            keywords = info.keywords
            creator = info.creator
            producer = info.producer
            creationDate = info.creationDate?.toInstant()
            modificationDate = info.modificationDate?.toInstant()
            trapped = info.trapped
        }

        // Load XMP catalog
        val catalog = document.documentCatalog
        val meta = catalog.metadata

        if (meta != null) {
            // Load the metadata
            val metadata = XmpParserProvider.get().parse(meta.createInputStream())

            // XMP Basic
            metadata.xmpBasicSchema?.let {
                basic.apply {
                    creatorTool = it.creatorTool
                    createDate = it.createDate?.toInstant()
                    modifyDate = it.modifyDate?.toInstant()
                    baseURL = it.baseURL
                    rating = it.rating
                    label = it.label
                    nickname = it.nickname
                    identifiers = it.identifiers
                    advisories = it.advisory
                    metadataDate = it.metadataDate?.toInstant()
                }
            }

            // XMP PDF
            metadata.adobePDFSchema?.let {
                pdf.apply {
                    pdfVersion = it.pdfVersion
                    keywords = it.keywords
                    producer = it.producer
                }
            }

            // XMP Dublin Core
            metadata.dublinCoreSchema?.let {
                dc.apply {
                    title = it.title
                    description = it.description
                    creators = it.creators
                    contributors = it.contributors
                    coverage = it.coverage
                    dates = it.dates.toInstants()
                    format = it.format
                    identifier = it.identifier
                    languages = it.languages
                    publishers = it.publishers
                    relationships = it.relations
                    rights = it.rights
                    source = it.source
                    subjects = it.subjects
                    types = it.types
                }
            }

            // XMP Rights
            metadata.xmpRightsManagementSchema?.let {
                rights.apply {
                    certificate = it.certificate
                    marked = it.marked
                    owner = it.owners
                    usageTerms = it.usageTerms
                    webStatement = it.webStatement
                }
            }
        }

        //System.err.println("Loaded:");
        //System.err.println(toYAML());
    }

    @Throws(IOException::class, XmpParsingException::class, BadFieldValueException::class)
    fun loadFromPDF(pdfFile: File) {
        loadPDFFileInfo(pdfFile)
        Loader.loadPDF(pdfFile, MemoryUsageSetting.setupMixed(30_720)).use { document -> loadFromPDF(document) }
    }

    @Throws(IOException::class)
    fun loadPDFFileInfo(pdfFile: File) {
        file.fullPath = pdfFile.absolutePath
        file.nameWithExt = pdfFile.name
        val attrs = Files.readAttributes(pdfFile.toPath(), BasicFileAttributes::class.java)
        file.sizeBytes = attrs.size()
        file.createTime = attrs.creationTime().toString()
        file.modifyTime = attrs.lastModifiedTime().toString()

        // filename w/o extension
        if (file.nameWithExt != null) {
            val dotPos = file.nameWithExt!!.lastIndexOf('.')
            if (dotPos >= 0) {
                file.name = file.nameWithExt!!.substring(0, dotPos)
            } else {
                file.name = file.nameWithExt
            }
        }
        // human readable file size
        var size = file.sizeBytes!!.toDouble()
        var idx: Int
        idx = 0
        while (idx < hrSizes.size) {
            if (size < 1000) {
                break
            }
            size /= 1000.0
            ++idx
        }
        file.size = String.format("%.2f%s", size, hrSizes[idx])
    }

    @Throws(Exception::class)
    private fun saveToPDF(document: PDDocument, pdfFile: File) {
        if (!(docEnabled.atLeastOne() || basicEnabled.atLeastOne() || pdfEnabled.atLeastOne() || dcEnabled.atLeastOne() || rightsEnabled.atLeastOne())) {
            return
        }
        //System.err.println("Saving:");
        //System.err.println(toYAML());
        // Basic info
        if (docEnabled.atLeastOne()) {
            val info = document.documentInformation
            if (docEnabled.title) {
                info.title = doc.title
            }
            if (docEnabled.author) {
                info.author = doc.author
            }
            if (docEnabled.subject) {
                info.subject = doc.subject
            }
            if (docEnabled.keywords) {
                info.keywords = doc.keywords
            }
            if (docEnabled.creator) {
                info.creator = doc.creator
            }
            if (docEnabled.producer) {
                info.producer = doc.producer
            }
            if (docEnabled.creationDate) {
                info.creationDate = doc.creationDate.toCalendar()
            }
            if (docEnabled.modificationDate) {
                info.modificationDate = doc.modificationDate.toCalendar()
            }
            if (docEnabled.trapped) {
                info.trapped = doc.trapped
            }
            document.documentInformation = info
        }

        // XMP
        val catalog = document.documentCatalog
        val meta = catalog.metadata
        var xmpOld: XMPMetadata? = null
        if (meta != null) {
            xmpOld = XmpParserProvider.get().parse(meta.createInputStream())
        }
        val xmpNew = XMPMetadata.createXMPMetadata()
        // XMP Basic
        val biOld = xmpOld?.xmpBasicSchema
        var atLeastOneXmpBasicSet = false
        if (basicEnabled.atLeastOne() || biOld != null) {
            val bi = xmpNew.createAndAddXMPBasicSchema()
            if (basicEnabled.advisories) {
                if (basic.advisories != null) {
                    for (a in basic.advisories!!) {
                        bi.addAdvisory(a)
                        atLeastOneXmpBasicSet = true
                    }
                }
            } else if (biOld != null) {
                val old = biOld.advisory
                if (old != null) {
                    for (a in old) {
                        bi.addAdvisory(a)
                        atLeastOneXmpBasicSet = true
                    }
                }
            }
            if (basicEnabled.baseURL) {
                if (basic.baseURL != null) {
                    bi.baseURL = basic.baseURL
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val baseUrl = biOld.baseURL
                if (baseUrl != null) {
                    bi.baseURL = baseUrl
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.createDate) {
                if (basic.createDate != null) {
                    bi.createDate = basic.createDate.toCalendar()
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.createDate
                if (old != null) {
                    bi.createDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.modifyDate) {
                if (basic.modifyDate != null) {
                    bi.modifyDate = basic.modifyDate.toCalendar()
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.modifyDate
                if (old != null) {
                    bi.modifyDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.creatorTool) {
                if (basic.creatorTool != null) {
                    bi.creatorTool = basic.creatorTool
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.creatorTool
                if (old != null) {
                    bi.creatorTool = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.identifiers) {
                if (basic.identifiers != null) {
                    for (i in basic.identifiers!!) {
                        bi.addIdentifier(i)
                        atLeastOneXmpBasicSet = true
                    }
                }
            } else if (biOld != null) {
                val old = biOld.identifiers
                if (old != null) {
                    for (a in old) {
                        bi.addIdentifier(a)
                        atLeastOneXmpBasicSet = true
                    }
                }
            }
            if (basicEnabled.label) {
                if (basic.label != null) {
                    bi.label = basic.label
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.label
                if (old != null) {
                    bi.label = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.metadataDate) {
                if (basic.metadataDate != null) {
                    bi.metadataDate = basic.metadataDate.toCalendar()
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.metadataDate
                if (old != null) {
                    bi.metadataDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.nickname) {
                if (basic.nickname != null) {
                    bi.nickname = basic.nickname
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.nickname
                if (old != null) {
                    bi.nickname = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled.rating) {
                if (basic.rating != null) {
                    bi.rating = basic.rating
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.rating
                if (old != null) {
                    bi.rating = old
                    atLeastOneXmpBasicSet = true
                }
            }
        }
        // XMP PDF
        val piOld = xmpOld?.adobePDFSchema
        var atLeastOneXmpPdfSet = false
        if (pdfEnabled.atLeastOne() || piOld != null) {
            val pi = xmpNew.createAndAddAdobePDFSchema()
            if (pdfEnabled.keywords) {
                if (pdf.keywords != null) {
                    pi.keywords = pdf.keywords
                    atLeastOneXmpPdfSet = true
                }
            } else if (piOld != null) {
                val old = piOld.keywords
                if (old != null) {
                    pi.keywords = old
                    atLeastOneXmpPdfSet = true
                }
            }
            if (pdfEnabled.producer) {
                if (pdf.producer != null) {
                    pi.producer = pdf.producer
                    atLeastOneXmpPdfSet = true
                }
            } else if (piOld != null) {
                val old = piOld.producer
                if (old != null) {
                    pi.producer = old
                    atLeastOneXmpPdfSet = true
                }
            }
            if (pdfEnabled.pdfVersion) {
                if (pdf.pdfVersion != null) {
                    pi.pdfVersion = pdf.pdfVersion
                    atLeastOneXmpPdfSet = true
                }
            } else if (piOld != null) {
                val old = piOld.pdfVersion
                if (old != null) {
                    pi.pdfVersion = old
                    atLeastOneXmpPdfSet = true
                }
            }
        }

        // XMP Dublin Core
        val dcOld = xmpOld?.dublinCoreSchema
        var atLeastOneXmpDcSet = false
        if (dcEnabled.atLeastOne() || dcOld != null) {
            val dcS = xmpNew.createAndAddDublinCoreSchema()
            if (dcEnabled.title) {
                if (dc.title != null) {
                    dcS.title = dc.title
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.title
                if (old != null) {
                    dcS.title = old
                    atLeastOneXmpDcSet = true
                }
            }
            //
            if (dcEnabled.contributors) {
                if (dc.contributors != null) {
                    for (i in dc.contributors!!) {
                        dcS.addContributor(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.contributors
                if (old != null) {
                    for (a in old) {
                        dcS.addContributor(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.publishers) {
                if (dc.publishers != null) {
                    for (i in dc.publishers!!) {
                        dcS.addPublisher(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.publishers
                if (old != null) {
                    for (a in old) {
                        dcS.addPublisher(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.relationships) {
                if (dc.relationships != null) {
                    for (i in dc.relationships!!) {
                        dcS.addRelation(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.relations
                if (old != null) {
                    for (a in old) {
                        dcS.addRelation(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.subjects) {
                if (dc.subjects != null) {
                    for (i in dc.subjects!!) {
                        dcS.addSubject(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.subjects
                if (old != null) {
                    for (a in old) {
                        dcS.addSubject(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.types) {
                if (dc.types != null) {
                    for (i in dc.types!!) {
                        dcS.addType(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.types
                if (old != null) {
                    for (a in old) {
                        dcS.addType(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.languages) {
                if (dc.languages != null) {
                    for (i in dc.languages!!) {
                        dcS.addLanguage(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.languages
                if (old != null) {
                    for (a in old) {
                        dcS.addLanguage(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.creators) {
                if (dc.creators != null) {
                    for (i in dc.creators!!) {
                        dcS.addCreator(i)
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.creators
                if (old != null) {
                    for (a in old) {
                        dcS.addCreator(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
            //
            if (dcEnabled.coverage) {
                if (dc.coverage != null) {
                    dcS.coverage = dc.coverage
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.coverage
                if (old != null) {
                    dcS.coverage = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled.format) {
                if (dc.format != null) {
                    dcS.format = dc.format
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.format
                if (old != null) {
                    dcS.format = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled.identifier) {
                if (dc.identifier != null) {
                    dcS.identifier = dc.identifier
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.identifier
                if (old != null) {
                    dcS.identifier = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled.rights) {
                if (dc.rights != null) {
                    dcS.addRights(null, dc.rights)
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val rll = dcOld.rightsLanguages
                if (rll != null) {
                    for (rl in rll) {
                        val rights = dcOld.getRights(rl)
                        if (rights != null) {
                            dcS.addRights(rl, rights)
                            atLeastOneXmpDcSet = true
                        }
                    }
                }
            }
            if (dcEnabled.source) {
                if (dc.source != null) {
                    dcS.source = dc.source
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.source
                if (old != null) {
                    dcS.source = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled.description) {
                if (dc.description != null) {
                    dcS.description = dc.description
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.description
                if (old != null) {
                    dcS.description = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled.dates) {
                if (dc.dates != null) {
                    for (date in dc.dates!!) {
                        dcS.addDate(date.toCalendar())
                        atLeastOneXmpDcSet = true
                    }
                }
            } else if (dcOld != null) {
                val old = dcOld.dates
                if (old != null) {
                    for (a in old) {
                        dcS.addDate(a)
                        atLeastOneXmpDcSet = true
                    }
                }
            }
        }

        // XMP Rights
        val riOld = xmpOld?.xmpRightsManagementSchema
        var atLeastOneXmpRightsSet = false
        if (rightsEnabled.atLeastOne() || riOld != null) {
            val ri = xmpNew.createAndAddXMPRightsManagementSchema()
            if (rightsEnabled.certificate) {
                if (rights.certificate != null) {
                    ri.certificate = rights.certificate
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.certificate
                if (old != null) {
                    ri.certificate = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled.marked) {
                if (rights.marked != null) {
                    ri.marked = rights.marked
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.marked
                if (old != null) {
                    ri.marked = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled.owner) {
                if (rights.owner != null) {
                    for (i in rights.owner!!) {
                        ri.addOwner(i)
                        atLeastOneXmpRightsSet = true
                    }
                }
            } else if (riOld != null) {
                val old = riOld.owners
                if (old != null) {
                    for (a in old) {
                        ri.addOwner(a)
                        atLeastOneXmpRightsSet = true
                    }
                }
            }
            if (rightsEnabled.usageTerms) {
                if (rights.usageTerms != null) {
                    ri.usageTerms = rights.usageTerms
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.usageTerms
                if (old != null) {
                    ri.usageTerms = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled.webStatement) {
                if (rights.webStatement != null) {
                    ri.webStatement = rights.webStatement
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.webStatement
                if (old != null) {
                    ri.webStatement = old
                    atLeastOneXmpRightsSet = true
                }
            }
        }

        // Do the save
        if (basicEnabled.atLeastOne() || pdfEnabled.atLeastOne() || dcEnabled.atLeastOne() || rightsEnabled.atLeastOne() ||
            atLeastOneXmpBasicSet || atLeastOneXmpPdfSet || atLeastOneXmpDcSet || atLeastOneXmpRightsSet
        ) {
            val metadataStream = PDMetadata(document)
            try {
                val serializer = XmpSerializer()
                val baos = ByteArrayOutputStream()
                serializer.serialize(xmpNew, baos, true)
                metadataStream.importXMPMetadata(baos.toByteArray())
            } catch (e: TransformerException) {
                throw Exception("Failed to save document:" + e.message)
            }
            catalog.metadata = metadataStream
        }
        document.save(pdfFile)
    }

    fun saveAsPDF(pdfFile: File, newFile: File? = null) {
        Loader.loadPDF(pdfFile, MemoryUsageSetting.setupMixed(30_720))
            .use { document -> saveToPDF(document, newFile ?: pdfFile) }
    }

    fun copyDocToXMP() {
        pdf.keywords = doc.keywords
        pdf.producer = doc.producer
        pdfEnabled.keywords = docEnabled.keywords
        pdfEnabled.producer = docEnabled.producer
        basic.createDate = doc.creationDate
        basic.modifyDate = doc.modificationDate
        basicEnabled.createDate = docEnabled.creationDate
        basicEnabled.modifyDate = docEnabled.modificationDate
        basic.creatorTool = doc.creator
        basicEnabled.creatorTool = docEnabled.creator
        dc.title = doc.title
        dc.description = doc.subject
        dc.creators = listOf(doc.author!!)
        dcEnabled.title = docEnabled.title
        dcEnabled.description = docEnabled.subject
        dcEnabled.creators = docEnabled.author
    }

    fun copyXMPToDoc() {
        doc.keywords = pdf.keywords
        doc.producer = pdf.producer
        docEnabled.keywords = pdfEnabled.keywords
        docEnabled.producer = pdfEnabled.producer
        doc.creationDate = basic.createDate
        doc.modificationDate = basic.modifyDate
        docEnabled.creationDate = basicEnabled.createDate
        docEnabled.modificationDate = basicEnabled.modifyDate
        doc.creator = basic.creatorTool
        docEnabled.creator = basicEnabled.creatorTool
        doc.title = dc.title
        doc.subject = dc.description
        var author: String? = ""
        if (dc.creators != null) {
            var delim = ""
            for (creator in dc.creators!!) {
                author += delim + creator
                delim = ", "
            }
        } else {
            author = null
        }
        doc.author = author
        docEnabled.title = dcEnabled.title
        docEnabled.subject = dcEnabled.description
        docEnabled.author = dcEnabled.creators
    }

    fun setEnabled(value: Boolean) {
        docEnabled.setAll(value)
        basicEnabled.setAll(value)
        pdfEnabled.setAll(value)
        dcEnabled.setAll(value)
        rightsEnabled.setAll(value)
    }

    fun setEnabled(id: String, value: Boolean) {
        _setObjectEnabled(id, value)
    }

    fun isEnabled(id: String): Boolean {
        return _getObjectEnabled(id)
    }

    fun <T> asFlatMap(convertor: Function<Any?, T>): MutableMap<String, T> {
        val map = LinkedHashMap<String, T>()
        for (fieldName in keys()) {
            val o = get(fieldName)
            map[fieldName] = convertor.apply(o)
        }
        return map
    }

    fun asFlatMap(): MutableMap<String, Any?> {
        return asFlatMap { t: Any? -> t }
    }

    fun asFlatStringMap(): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        for (fieldName in keys()) {
            map[fieldName] = getString(fieldName)
        }
        return map
    }

    fun fromFlatMap(map: Map<String?, Any?>, convertor: Function<Any?, Any?>) {
        for (fieldName in keys()) {
            if (map.containsKey(fieldName)) {
                set(fieldName, convertor.apply(map[fieldName]))
            }
        }
    }

    fun clone(): MetadataInfo {
        val md = MetadataInfo()
        md.copyFrom(this)
        return md
    }

    fun copyFrom(other: MetadataInfo) {
        for (fieldName in keys()) {
            set(fieldName, other[fieldName])
        }
    }

    fun copyUnset(other: MetadataInfo) {
        for (fieldName in keys()) {
            val o = get(fieldName)
            if (o == null) {
                set(fieldName, other[fieldName])
            }
        }
    }

    fun copyUnsetExpanded(other: MetadataInfo) {
        for (fieldName in keys()) {
            val o = get(fieldName)
            if (o == null) {
                var otherVal = other[fieldName]
                if (otherVal is String) {
                    val ts = TemplateString(otherVal)
                    otherVal = ts.process(this)
                }
                set(fieldName, otherVal)
            }
        }
    }

    fun expand(expandInfo: MetadataInfo) {
        for (fieldName in keys()) {
            val o = get(fieldName)
            if (o != null) {
                var expandedVal = get(fieldName)
                if (expandedVal is String) {
                    val ts = TemplateString(expandedVal)
                    expandedVal = ts.process(expandInfo)
                }
                set(fieldName, expandedVal)
            }
        }
    }

    fun enableOnlyNonNull() {
        val values: Map<String, Any?> = asFlatMap()
        docEnabled.setAll(false)
        basicEnabled.setAll(false)
        pdfEnabled.setAll(false)
        dcEnabled.setAll(false)
        rightsEnabled.setAll(false)
        for ((key, value) in values) {
            if (value != null) {
                setEnabled(key, true)
            }
        }
    }

    @JvmOverloads
    fun toJson(pretty: Boolean = false): String {
        val map: Map<String, Any?> = asFlatMap { t: Any? ->
            if (t != null) {
                if (t is Instant) {
                    return@asFlatMap formatDateTimeFull(t)
                }
            }
            t
        }
        var gson = GsonBuilder().disableHtmlEscaping()
        if (pretty) {
            gson = gson.setPrettyPrinting()
        }
        return gson.create().toJson(map)
    }

    @JvmOverloads
    fun toYAML(pretty: Boolean = false): String {
        val options = DumperOptions()
        if (!pretty) {
            options.width = 0xFFFF
        }
        val yaml = Yaml(options)
        return yaml.dump(asFlatMap())
    }

    fun fromYAML(yamlString: String?) {
        val yaml = Yaml()
        val map = yaml.load<Map<String?, Any?>>(yamlString)
        fromFlatMap(map) { t: Any? ->
            if (t is Date) {
                return@fromFlatMap t.toInstant()
            }
            t
        }
    }

    fun isEquivalent(other: MetadataInfo): Boolean {
        for ((key, value) in FIELDS_BY_NAME) {
            // Skip file.* fields, as they are read only and come from file metadata
            if (key.startsWith("file.")) {
                continue
            }
            val t = get(key)
            val o = other[key]
            val fd = value[value.size - 1]
            if (t == null) {
                return if (o == null) {
                    continue
                } else {
                    false
                }
            }
            if (fd.list && fd.type == MetadataFieldType.DATE) {
                val tl = t as List<Instant>
                val ol = o as List<Instant>
                if (tl.size != ol.size) {
                    return false
                }
                for (i in tl.indices) {
                    val tc = tl[i]
                    val oc = ol[i]
                    if (tc == null) {
                        return if (oc == null) {
                            continue
                        } else {
                            false
                        }
                    }
                    if (tc.epochSecond != oc.epochSecond) {
                        return false
                    }
                }
            } else if (t is Instant && o is Instant) {
                if (t.epochSecond != o.epochSecond) {
                    return false
                }
            } else if (t != o) {
                return false
            }
        }
        return true
    }

    fun asPersistenceString(): String {
        val map = asFlatMap()
        // Don't store null values as they are the default
        for (key in FIELDS_BY_NAME.keys) {
            if (map[key] == null) {
                map.remove(key)
            }
        }
        val enabledMap: MutableMap<String, Boolean> = LinkedHashMap()
        // Don't store true values as they are the default
        for (keyEnabled in ENABLED_FIELDS_BY_NAME.keys) {
            if (!isEnabled(keyEnabled)) {
                enabledMap[keyEnabled] = false
            }
        }
        if (enabledMap.isNotEmpty()) {
            map["_enabled"] = enabledMap
        }
        val options = DumperOptions()
        options.width = 0xFFFF
        val yaml = Yaml(options)
        return yaml.dump(map)
    }

    private fun getStructObject(
        id: String,
        mdFields: Map<String, List<MetadataField>>?,
        parent: Boolean,
        toString: Boolean,
        useDefault: Boolean,
        defaultValue: Any?
    ): Any? {
        val fields = mdFields!![id]
        if (fields.isNullOrEmpty()) {
            if (useDefault) {
                return defaultValue
            }
            throw RuntimeException("getStructObject: No field for '$id'")
        }
        var current: Any? = this
        var fieldD: MetadataField? = null
        for (i in 0 until fields.size - if (parent) 1 else 0) {
            try {
                fieldD = fields[i]
                current = fieldD.get(current!!)
            } catch (e: IllegalArgumentException) {
                if (useDefault) {
                    return defaultValue
                }
                throw IllegalArgumentException("getStructObject('$id')", e)
            } catch (e: IllegalAccessException) {
                if (useDefault) {
                    return defaultValue
                }
                throw RuntimeException("getStructObject('$id') ${e.message}")
            }
        }
        return if (toString) {
            fieldD!!.makeStringFromValue(current)
        } else current
    }

    operator fun get(id: String): Any? {
        return getStructObject(id, FIELDS_BY_NAME, false, false, false, null)
    }

    fun getString(id: String): String {
        return getStructObject(id, FIELDS_BY_NAME, false, true, false, null) as String
    }

    operator fun get(id: String, defaultValue: Any): Any {
        return getStructObject(id, FIELDS_BY_NAME, false, false, true, defaultValue)!!
    }

    fun getString(id: String, defaultValue: String): String {
        return getStructObject(id, FIELDS_BY_NAME, false, true, true, defaultValue) as String
    }

    private fun _getObjectEnabled(id: String): Boolean {
        return getStructObject(id, ENABLED_FIELDS_BY_NAME, false, false, true, false) as Boolean
    }

    private fun _setStructObject(
        id: String,
        value: Any?,
        append: Boolean,
        fromString: Boolean,
        mdFields: Map<String, List<MetadataField>>?
    ) {
        var value = value
        val fields = mdFields!![id]
        if (fields.isNullOrEmpty()) {
            throw RuntimeException("_setStructObject('$id') No such field")
        }
        val current = getStructObject(id, mdFields, true, false, false, null)
            ?: throw RuntimeException("_setStructObject('$id') No such field")
        try {
            val fieldD = fields[fields.size - 1]
            if (fromString && value != null) {
                value = fieldD.makeValueFromString(value.toString())
            }
            if (fieldD.list && append) {
                var l: MutableList<Any>? = fieldD.get(current)
                if (l == null) {
                    l = mutableListOf()
                }
                if (MutableList::class.java.isAssignableFrom(value!!.javaClass)) {
                    l.addAll((value as List<Any>))
                } else {
                    l.add(value)
                }
                fieldD.set(current, l)
            } else {
                fieldD.set(current, value)
            }
        } catch (e: IllegalArgumentException) {
            log.error("setStructObject failed for '$id'", e)
            throw e
        } catch (e: IllegalAccessException) {
            log.error("setStructObject failed for '$id'", e)
            throw e
        }
    }

    operator fun set(id: String, value: Any?) {
        _setStructObject(id, value, false, false, FIELDS_BY_NAME)
    }

    fun setAppend(id: String, value: Any?) {
        _setStructObject(id, value, true, false, FIELDS_BY_NAME)
    }

    fun setFromString(id: String, value: String?) {
        _setStructObject(id, value, false, true, FIELDS_BY_NAME)
    }

    fun setAppendFromString(id: String, value: String?) {
        _setStructObject(id, value, true, true, FIELDS_BY_NAME)
    }

    private fun _setObjectEnabled(id: String, value: Boolean) {
        _setStructObject(id, value, false, false, ENABLED_FIELDS_BY_NAME)
    }

    companion object {

        val hrSizes = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        fun keys(): List<String> {
            return FIELDS_BY_NAME.keys.toList()
        }

        fun keyIsWritable(key: String): Boolean {
            val fd = getFieldDescription(key)
            return fd?.writable ?: false
        }

        fun fromPersistenceString(yamlString: String?): MetadataInfo {
            val yaml = Yaml()
            val map = yaml.load<Any>(yamlString) as Map<String, Any>
            val md = MetadataInfo()
            md.fromYAML(yamlString)
            val enMap = map["_enabled"]
            if (enMap != null && Map::class.java.isAssignableFrom(enMap.javaClass)) {
                val enabledMap = enMap as Map<String, Any>
                for (fieldName in ENABLED_FIELDS_BY_NAME.keys) {
                    if (enabledMap.containsKey(fieldName)) {
                        md.setEnabled(fieldName, (enabledMap[fieldName] as Boolean?)!!)
                    }
                }
            }
            return md
        }

        fun getFieldDescription(id: String): MetadataField? {
            val fields = FIELDS_BY_NAME[id]!!
            return if (fields.isNotEmpty()) {
                fields[fields.size - 1]
            } else null
        }

    }
}
