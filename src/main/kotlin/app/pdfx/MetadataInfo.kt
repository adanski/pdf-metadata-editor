package app.pdfx

import app.pdfx.CommandLine.ParseError
import app.pdfx.DateFormat.formatDateTime
import app.pdfx.DateFormat.formatDateTimeFull
import app.pdfx.DateFormat.parseDate
import app.pdfx.DateFormat.parseDateOrNull
import app.pdfx.FieldID.value
import app.pdfx.ListFormat.humanReadable
import app.pdfx.MdStruct.StructType
import com.google.gson.GsonBuilder
import org.apache.pdfbox.Loader
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDMetadata
import org.apache.xmpbox.XMPMetadata
import org.apache.xmpbox.type.BadFieldValueException
import org.apache.xmpbox.xml.XmpParsingException
import org.apache.xmpbox.xml.XmpSerializer
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import javax.xml.transform.TransformerException

class MetadataInfo {
    class FileInfo {
        var name: String? = null
        var nameWithExt: String? = null
        var sizeBytes: Long? = null
        var size: String? = null
        var createTime: String? = null
        var modifyTime: String? = null
        @JvmField
        var fullPath: String? = null
    }

    class FileInfoEnabled {
        var name = false
        var nameWithExt = false
        var sizeBytes = false
        var size = false
        var createTime = false
        var modifyTime = false
        var fullPath = false
        fun atLeastOne(): Boolean {
            return false
        }

        fun setAll(value: Boolean) {
            name = false
            nameWithExt = false
            sizeBytes = false
            size = false
            createTime = false
            modifyTime = false
            fullPath = false
        }
    }

    class Basic {
        var title: String? = null
        var author: String? = null
        var subject: String? = null
        var keywords: String? = null
        var creator: String? = null
        var producer: String? = null
        var creationDate: Calendar? = null
        var modificationDate: Calendar? = null
        var trapped: String? = null
    }

    class BasicEnabled {
        var title = true
        var author = true
        var subject = true
        var keywords = true
        var creator = true
        var producer = true
        var creationDate = true
        var modificationDate = true
        var trapped = true
        fun atLeastOne(): Boolean {
            return (title || author || subject || keywords || creator || producer || creationDate || modificationDate
                    || trapped)
        }

        fun setAll(value: Boolean) {
            title = value
            author = value
            subject = value
            keywords = value
            creator = value
            producer = value
            creationDate = value
            modificationDate = value
            trapped = value
        }
    }

    class XmpBasic {
        var creatorTool: String? = null
        var createDate: Calendar? = null
        var modifyDate: Calendar? = null
        var baseURL: String? = null
        var rating: Int? = null
        var label: String? = null
        var nickname: String? = null
        var identifiers: List<String>? = null
        var advisories: List<String>? = null
        var metadataDate: Calendar? = null
    }

    class XmpBasicEnabled {
        var creatorTool = true
        var createDate = true
        var modifyDate = true
        var baseURL = true
        var rating = true
        var label = true
        var nickname = true
        var identifiers = true
        var advisories = true
        var metadataDate = true
        fun atLeastOne(): Boolean {
            return (creatorTool || createDate || modifyDate || baseURL || rating || label || nickname
                    || identifiers || advisories || metadataDate)
        }

        fun setAll(value: Boolean) {
            creatorTool = value
            createDate = value
            modifyDate = value
            baseURL = value
            rating = value
            label = value
            nickname = value
            identifiers = value
            advisories = value
            metadataDate = value
        }
    }

    class XmpPdf {
        var pdfVersion: String? = null
        var keywords: String? = null
        var producer: String? = null
    }

    class XmpPdfEnabled {
        var pdfVersion = true
        var keywords = true
        var producer = true
        fun atLeastOne(): Boolean {
            return pdfVersion || keywords || producer
        }

        fun setAll(value: Boolean) {
            pdfVersion = value
            keywords = value
            producer = value
        }
    }

    class XmpDublinCore {
        var title: String? = null
        var description: String? = null
        var creators: MutableList<String>? = null
        var contributors: List<String>? = null
        var coverage: String? = null

        @FieldID(value = "dates", type = FieldID.FieldType.DATE)
        var dates: List<Calendar>? = null
        var format: String? = null
        var identifier: String? = null
        var languages: List<String>? = null
        var publishers: List<String>? = null
        var relationships: List<String>? = null
        var rights: String? = null
        var source: String? = null
        var subjects: List<String>? = null
        var types: List<String>? = null
    }

    class XmpDublinCoreEnabled {
        var title = true
        var description = true
        var creators = true
        var contributors = true
        var coverage = true
        var dates = true
        var format = true
        var identifier = true
        var languages = true
        var publishers = true
        var relationships = true
        var rights = true
        var source = true
        var subjects = true
        var types = true
        fun atLeastOne(): Boolean {
            return (title || description || creators || contributors || coverage || dates || format || identifier
                    || languages || publishers || relationships || rights || source || subjects || types)
        }

        fun setAll(value: Boolean) {
            title = value
            description = value
            creators = value
            contributors = value
            coverage = value
            dates = value
            format = value
            identifier = value
            languages = value
            publishers = value
            relationships = value
            rights = value
            source = value
            subjects = value
            types = value
        }
    }

    class XmpRights {
        var certificate: String? = null
        var marked: Boolean? = null
        var owner: List<String>? = null
        var usageTerms: String? = null
        var webStatement: String? = null
    }

    class XmpRightsEnabled {
        var certificate = true
        var marked = true
        var owner = true
        var usageTerms = true
        var webStatement = true
        fun atLeastOne(): Boolean {
            return certificate || marked || owner || usageTerms || webStatement
        }

        fun setAll(value: Boolean) {
            certificate = value
            marked = value
            owner = value
            usageTerms = value
            webStatement = value
        }
    }

    @MdStruct
    var doc: Basic? = null

    @MdStruct
    var basic: XmpBasic? = null

    @MdStruct
    var pdf: XmpPdf? = null

    @MdStruct
    var dc: XmpDublinCore? = null

    @MdStruct
    var rights: XmpRights? = null

    @JvmField
    @MdStruct(name = "file", type = StructType.MD_STRUCT, access = MdStruct.Access.READ_ONLY)
    var file: FileInfo? = null

    @MdStruct(name = "doc", type = StructType.MD_ENABLE_STRUCT)
    var docEnabled: BasicEnabled? = null

    @MdStruct(name = "basic", type = StructType.MD_ENABLE_STRUCT)
    var basicEnabled: XmpBasicEnabled? = null

    @MdStruct(name = "pdf", type = StructType.MD_ENABLE_STRUCT)
    var pdfEnabled: XmpPdfEnabled? = null

    @MdStruct(name = "dc", type = StructType.MD_ENABLE_STRUCT)
    var dcEnabled: XmpDublinCoreEnabled? = null

    @MdStruct(name = "rights", type = StructType.MD_ENABLE_STRUCT)
    var rightsEnabled: XmpRightsEnabled? = null

    @MdStruct(name = "file", type = StructType.MD_ENABLE_STRUCT, access = MdStruct.Access.READ_ONLY)
    var fileEnabled: FileInfoEnabled? = null
    fun clear() {
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
    protected fun loadFromPDF(document: PDDocument) {
        val info = document.documentInformation

        // Basic info
        doc!!.title = info.title
        doc!!.author = info.author
        doc!!.subject = info.subject
        doc!!.keywords = info.keywords
        doc!!.creator = info.creator
        doc!!.producer = info.producer
        doc!!.creationDate = info.creationDate
        doc!!.modificationDate = info.modificationDate
        doc!!.trapped = info.trapped

        // Load XMP catalog
        val catalog = document.documentCatalog
        val meta = catalog.metadata
        if (meta != null) {
            // Load the metadata
            val metadata = XmpParserProvider.get().parse(meta.createInputStream())

            // XMP Basic
            val bi = metadata.xmpBasicSchema
            if (bi != null) {
                basic!!.creatorTool = bi.creatorTool
                basic!!.createDate = bi.createDate
                basic!!.modifyDate = bi.modifyDate
                basic!!.baseURL = bi.baseURL
                basic!!.rating = bi.rating
                basic!!.label = bi.label
                basic!!.nickname = bi.nickname
                basic!!.identifiers = bi.identifiers
                basic!!.advisories = bi.advisory
                basic!!.metadataDate = bi.metadataDate
            }

            // XMP PDF
            val pi = metadata.adobePDFSchema
            if (pi != null) {
                pdf!!.pdfVersion = pi.pdfVersion
                pdf!!.keywords = pi.keywords
                pdf!!.producer = pi.producer
            }

            // XMP Dublin Core
            val dcS = metadata.dublinCoreSchema
            if (dcS != null) {
                dc!!.title = dcS.title
                dc!!.description = dcS.description
                dc!!.creators = dcS.creators
                dc!!.contributors = dcS.contributors
                dc!!.coverage = dcS.coverage
                dc!!.dates = dcS.dates
                dc!!.format = dcS.format
                dc!!.identifier = dcS.identifier
                dc!!.languages = dcS.languages
                dc!!.publishers = dcS.publishers
                dc!!.relationships = dcS.relations
                dc!!.rights = dcS.rights
                dc!!.source = dcS.source
                dc!!.subjects = dcS.subjects
                dc!!.types = dcS.types
            }

            // XMP Rights
            val ri = metadata.xmpRightsManagementSchema
            if (ri != null) {
                rights!!.certificate = ri.certificate
                rights!!.marked = ri.marked
                rights!!.owner = ri.owners
                rights!!.usageTerms = ri.usageTerms
                rights!!.webStatement = ri.webStatement
            }
        }

        //System.err.println("Loaded:");
        //System.err.println(toYAML());
    }

    @Throws(IOException::class, XmpParsingException::class, BadFieldValueException::class)
    fun loadFromPDF(pdfFile: File) {
        loadPDFFileInfo(pdfFile)
        Loader.loadPDF(pdfFile, MemoryUsageSetting.setupMixed(30720)).use { document -> loadFromPDF(document) }
    }

    @Throws(IOException::class)
    fun loadPDFFileInfo(pdfFile: File) {
        file!!.fullPath = pdfFile.absolutePath
        file!!.nameWithExt = pdfFile.name
        val attrs = Files.readAttributes(pdfFile.toPath(), BasicFileAttributes::class.java)
        file!!.sizeBytes = attrs.size()
        file!!.createTime = attrs.creationTime().toString()
        file!!.modifyTime = attrs.lastModifiedTime().toString()

        // filename w/o extension
        if (file!!.nameWithExt != null) {
            val dotPos = file!!.nameWithExt!!.lastIndexOf('.')
            if (dotPos >= 0) {
                file!!.name = file!!.nameWithExt!!.substring(0, dotPos)
            } else {
                file!!.name = file!!.nameWithExt
            }
        }
        // human readable file size
        var size = file!!.sizeBytes!!.toDouble()
        var idx: Int
        idx = 0
        while (idx < hrSizes.size) {
            if (size < 1000) {
                break
            }
            size /= 1000.0
            ++idx
        }
        file!!.size = String.format("%.2f%s", size, hrSizes[idx])
    }

    @Throws(Exception::class)
    protected fun saveToPDF(document: PDDocument, pdfFile: File) {
        if (!(docEnabled!!.atLeastOne() || basicEnabled!!.atLeastOne() || pdfEnabled!!.atLeastOne() || dcEnabled!!.atLeastOne() || rightsEnabled!!.atLeastOne())) {
            return
        }
        //System.err.println("Saving:");
        //System.err.println(toYAML());
        // Basic info
        if (docEnabled!!.atLeastOne()) {
            val info = document.documentInformation
            if (docEnabled!!.title) {
                info.title = doc!!.title
            }
            if (docEnabled!!.author) {
                info.author = doc!!.author
            }
            if (docEnabled!!.subject) {
                info.subject = doc!!.subject
            }
            if (docEnabled!!.keywords) {
                info.keywords = doc!!.keywords
            }
            if (docEnabled!!.creator) {
                info.creator = doc!!.creator
            }
            if (docEnabled!!.producer) {
                info.producer = doc!!.producer
            }
            if (docEnabled!!.creationDate) {
                info.creationDate = doc!!.creationDate
            }
            if (docEnabled!!.modificationDate) {
                info.modificationDate = doc!!.modificationDate
            }
            if (docEnabled!!.trapped) {
                info.trapped = doc!!.trapped
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
        if (basicEnabled!!.atLeastOne() || biOld != null) {
            val bi = xmpNew.createAndAddXMPBasicSchema()
            if (basicEnabled!!.advisories) {
                if (basic!!.advisories != null) {
                    for (a in basic!!.advisories!!) {
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
            if (basicEnabled!!.baseURL) {
                if (basic!!.baseURL != null) {
                    bi.baseURL = basic!!.baseURL
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val baseUrl = biOld.baseURL
                if (baseUrl != null) {
                    bi.baseURL = baseUrl
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.createDate) {
                if (basic!!.createDate != null) {
                    bi.createDate = basic!!.createDate
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.createDate
                if (old != null) {
                    bi.createDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.modifyDate) {
                if (basic!!.modifyDate != null) {
                    bi.modifyDate = basic!!.modifyDate
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.modifyDate
                if (old != null) {
                    bi.modifyDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.creatorTool) {
                if (basic!!.creatorTool != null) {
                    bi.creatorTool = basic!!.creatorTool
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.creatorTool
                if (old != null) {
                    bi.creatorTool = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.identifiers) {
                if (basic!!.identifiers != null) {
                    for (i in basic!!.identifiers!!) {
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
            if (basicEnabled!!.label) {
                if (basic!!.label != null) {
                    bi.label = basic!!.label
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.label
                if (old != null) {
                    bi.label = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.metadataDate) {
                if (basic!!.metadataDate != null) {
                    bi.metadataDate = basic!!.metadataDate
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.metadataDate
                if (old != null) {
                    bi.metadataDate = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.nickname) {
                if (basic!!.nickname != null) {
                    bi.nickname = basic!!.nickname
                    atLeastOneXmpBasicSet = true
                }
            } else if (biOld != null) {
                val old = biOld.nickname
                if (old != null) {
                    bi.nickname = old
                    atLeastOneXmpBasicSet = true
                }
            }
            if (basicEnabled!!.rating) {
                if (basic!!.rating != null) {
                    bi.rating = basic!!.rating
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
        if (pdfEnabled!!.atLeastOne() || piOld != null) {
            val pi = xmpNew.createAndAddAdobePDFSchema()
            if (pdfEnabled!!.keywords) {
                if (pdf!!.keywords != null) {
                    pi.keywords = pdf!!.keywords
                    atLeastOneXmpPdfSet = true
                }
            } else if (piOld != null) {
                val old = piOld.keywords
                if (old != null) {
                    pi.keywords = old
                    atLeastOneXmpPdfSet = true
                }
            }
            if (pdfEnabled!!.producer) {
                if (pdf!!.producer != null) {
                    pi.producer = pdf!!.producer
                    atLeastOneXmpPdfSet = true
                }
            } else if (piOld != null) {
                val old = piOld.producer
                if (old != null) {
                    pi.producer = old
                    atLeastOneXmpPdfSet = true
                }
            }
            if (pdfEnabled!!.pdfVersion) {
                if (pdf!!.pdfVersion != null) {
                    pi.pdfVersion = pdf!!.pdfVersion
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
        if (dcEnabled!!.atLeastOne() || dcOld != null) {
            val dcS = xmpNew.createAndAddDublinCoreSchema()
            if (dcEnabled!!.title) {
                if (dc!!.title != null) {
                    dcS.title = dc!!.title
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
            if (dcEnabled!!.contributors) {
                if (dc!!.contributors != null) {
                    for (i in dc!!.contributors!!) {
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
            if (dcEnabled!!.publishers) {
                if (dc!!.publishers != null) {
                    for (i in dc!!.publishers!!) {
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
            if (dcEnabled!!.relationships) {
                if (dc!!.relationships != null) {
                    for (i in dc!!.relationships!!) {
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
            if (dcEnabled!!.subjects) {
                if (dc!!.subjects != null) {
                    for (i in dc!!.subjects!!) {
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
            if (dcEnabled!!.types) {
                if (dc!!.types != null) {
                    for (i in dc!!.types!!) {
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
            if (dcEnabled!!.languages) {
                if (dc!!.languages != null) {
                    for (i in dc!!.languages!!) {
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
            if (dcEnabled!!.creators) {
                if (dc!!.creators != null) {
                    for (i in dc!!.creators!!) {
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
            if (dcEnabled!!.coverage) {
                if (dc!!.coverage != null) {
                    dcS.coverage = dc!!.coverage
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.coverage
                if (old != null) {
                    dcS.coverage = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled!!.format) {
                if (dc!!.format != null) {
                    dcS.format = dc!!.format
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.format
                if (old != null) {
                    dcS.format = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled!!.identifier) {
                if (dc!!.identifier != null) {
                    dcS.identifier = dc!!.identifier
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.identifier
                if (old != null) {
                    dcS.identifier = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled!!.rights) {
                if (dc!!.rights != null) {
                    dcS.addRights(null, dc!!.rights)
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
            if (dcEnabled!!.source) {
                if (dc!!.source != null) {
                    dcS.source = dc!!.source
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.source
                if (old != null) {
                    dcS.source = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled!!.description) {
                if (dc!!.description != null) {
                    dcS.description = dc!!.description
                    atLeastOneXmpDcSet = true
                }
            } else if (dcOld != null) {
                val old = dcOld.description
                if (old != null) {
                    dcS.description = old
                    atLeastOneXmpDcSet = true
                }
            }
            if (dcEnabled!!.dates) {
                if (dc!!.dates != null) {
                    for (date in dc!!.dates!!) {
                        dcS.addDate(date)
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
        if (rightsEnabled!!.atLeastOne() || riOld != null) {
            val ri = xmpNew.createAndAddXMPRightsManagementSchema()
            if (rightsEnabled!!.certificate) {
                if (rights!!.certificate != null) {
                    ri.certificate = rights!!.certificate
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.certificate
                if (old != null) {
                    ri.certificate = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled!!.marked) {
                if (rights!!.marked != null) {
                    ri.marked = rights!!.marked
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.marked
                if (old != null) {
                    ri.marked = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled!!.owner) {
                if (rights!!.owner != null) {
                    for (i in rights!!.owner!!) {
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
            if (rightsEnabled!!.usageTerms) {
                if (rights!!.usageTerms != null) {
                    ri.usageTerms = rights!!.usageTerms
                    atLeastOneXmpRightsSet = true
                }
            } else if (riOld != null) {
                val old = riOld.usageTerms
                if (old != null) {
                    ri.usageTerms = old
                    atLeastOneXmpRightsSet = true
                }
            }
            if (rightsEnabled!!.webStatement) {
                if (rights!!.webStatement != null) {
                    ri.webStatement = rights!!.webStatement
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
        if (basicEnabled!!.atLeastOne() || pdfEnabled!!.atLeastOne() || dcEnabled!!.atLeastOne() || rightsEnabled!!.atLeastOne() ||
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
        document.save(pdfFile.absolutePath)
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun saveAsPDF(pdfFile: File, newFile: File? = null) {
        Loader.loadPDF(pdfFile, MemoryUsageSetting.setupMixed(30720))
            .use { document -> saveToPDF(document, newFile ?: pdfFile) }
    }

    fun copyDocToXMP() {
        pdf!!.keywords = doc!!.keywords
        pdf!!.producer = doc!!.producer
        pdfEnabled!!.keywords = docEnabled!!.keywords
        pdfEnabled!!.producer = docEnabled!!.producer
        basic!!.createDate = doc!!.creationDate
        basic!!.modifyDate = doc!!.modificationDate
        basicEnabled!!.createDate = docEnabled!!.creationDate
        basicEnabled!!.modifyDate = docEnabled!!.modificationDate
        basic!!.creatorTool = doc!!.creator
        basicEnabled!!.creatorTool = docEnabled!!.creator
        dc!!.title = doc!!.title
        dc!!.description = doc!!.subject
        dc!!.creators = Arrays.asList(*arrayOf(doc!!.author))
        dcEnabled!!.title = docEnabled!!.title
        dcEnabled!!.description = docEnabled!!.subject
        dcEnabled!!.creators = docEnabled!!.author
    }

    fun copyXMPToDoc() {
        doc!!.keywords = pdf!!.keywords
        doc!!.producer = pdf!!.producer
        docEnabled!!.keywords = pdfEnabled!!.keywords
        docEnabled!!.producer = pdfEnabled!!.producer
        doc!!.creationDate = basic!!.createDate
        doc!!.modificationDate = basic!!.modifyDate
        docEnabled!!.creationDate = basicEnabled!!.createDate
        docEnabled!!.modificationDate = basicEnabled!!.modifyDate
        doc!!.creator = basic!!.creatorTool
        docEnabled!!.creator = basicEnabled!!.creatorTool
        doc!!.title = dc!!.title
        doc!!.subject = dc!!.description
        var author: String? = ""
        if (dc!!.creators != null) {
            var delim = ""
            for (creator in dc!!.creators!!) {
                author += delim + creator
                delim = ", "
            }
        } else {
            author = null
        }
        doc!!.author = author
        docEnabled!!.title = dcEnabled!!.title
        docEnabled!!.subject = dcEnabled!!.description
        docEnabled!!.author = dcEnabled!!.creators
    }

    fun setEnabled(value: Boolean) {
        docEnabled!!.setAll(value)
        basicEnabled!!.setAll(value)
        pdfEnabled!!.setAll(value)
        dcEnabled!!.setAll(value)
        rightsEnabled!!.setAll(value)
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

    override fun clone(): MetadataInfo {
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

    fun copyUnsetExpanded(other: MetadataInfo, expandInfo: MetadataInfo?) {
        for (fieldName in keys()) {
            val o = get(fieldName)
            if (o == null) {
                var otherVal = other[fieldName]
                if (otherVal is String) {
                    val ts = TemplateString(otherVal)
                    otherVal = ts.process(expandInfo)
                }
                set(fieldName, otherVal)
            }
        }
    }

    fun expand(expandInfo: MetadataInfo?) {
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
        docEnabled!!.setAll(false)
        basicEnabled!!.setAll(false)
        pdfEnabled!!.setAll(false)
        dcEnabled!!.setAll(false)
        rightsEnabled!!.setAll(false)
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
                if (t is Calendar) {
                    return@asFlatMap formatDateTimeFull((t as Calendar?)!!)
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
                val cal = Calendar.getInstance()
                cal.time = t
                return@fromFlatMap cal
            }
            t
        }
    }

    fun isEquivalent(other: MetadataInfo): Boolean {
        for ((key, value) in _mdFields!!) {
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
            if (fd.isList && fd.type === FieldID.FieldType.DATE) {
                val tl = t as List<Calendar>
                val ol = o as List<Calendar>
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
                    if (tc.timeInMillis / 1000 != oc.timeInMillis / 1000) {
                        return false
                    }
                }
            } else if (t is Calendar && o is Calendar) {
                if (t.timeInMillis / 1000 != o.timeInMillis / 1000) {
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
        for (key in _mdFields!!.keys) {
            if (map[key] == null) {
                map.remove(key)
            }
        }
        val enabledMap: MutableMap<String, Boolean> = LinkedHashMap()
        // Don't store true values as they are the default
        for (keyEnabled in _mdEnabledFields!!.keys) {
            if (!isEnabled(keyEnabled)) {
                enabledMap[keyEnabled] = false
            }
        }
        if (enabledMap.size > 0) {
            map["_enabled"] = enabledMap
        }
        val options = DumperOptions()
        options.width = 0xFFFF
        val yaml = Yaml(options)
        return yaml.dump(map)
    }

    class FieldDescription {
        val name: String
        val field: Field
        val type: FieldID.FieldType?
        val isList: Boolean
        val isWritable: Boolean

        constructor(name: String, field: Field, type: FieldID.FieldType?, isWritable: Boolean) {
            this.name = name
            this.field = field
            this.type = type
            this.isWritable = isWritable
            isList = MutableList::class.java.isAssignableFrom(field.type)
        }

        constructor(name: String, field: Field, isWritable: Boolean) {
            val klass = field.type
            if (Boolean::class.java.isAssignableFrom(klass)) {
                type = FieldID.FieldType.BOOL
            } else if (Calendar::class.java.isAssignableFrom(klass)) {
                type = FieldID.FieldType.DATE
            } else if (Int::class.java.isAssignableFrom(klass)) {
                type = FieldID.FieldType.INT
            } else if (Long::class.java.isAssignableFrom(klass)) {
                type = FieldID.FieldType.LONG
            } else {
                type = FieldID.FieldType.STRING
            }
            this.name = name
            this.field = field
            this.isWritable = isWritable
            isList = MutableList::class.java.isAssignableFrom(klass)
        }

        fun makeStringFromValue(value: Any?): String {
            if (value == null) {
                return ""
            }
            return if (isList) {
                humanReadable(value as List<*>?)
            } else if (type === FieldID.FieldType.DATE) {
                formatDateTime((value as Calendar?)!!)
            } else if (type === FieldID.FieldType.BOOL) {
                if (value as Boolean) "true" else "false"
            } else {
                value.toString()
            }
        }

        fun makeValueFromString(value: String?): Any? {
            if (value == null) {
                return null
            }
            if (isList) {
                if (type === FieldID.FieldType.STRING) {
                    return Arrays.asList(value)
                } else if (type === FieldID.FieldType.TEXT) {
                    return Arrays.asList(*value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                } else if (type === FieldID.FieldType.INT) {
                    // TODO: possible allow comma separated interger list
                    return Arrays.asList(value.toInt())
                } else if (type === FieldID.FieldType.BOOL) {
                    // TODO: possible allow comma separated boolean list
                    val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
                    var b: Boolean? = null
                    if (v == "true" || v == "yes") b = true
                    if (v == "false" || v == "no") b = false
                    return Arrays.asList(b)
                } else if (type === FieldID.FieldType.DATE) {
                    val rval: MutableList<Calendar> = ArrayList()
                    for (line in value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        try {
                            rval.add(parseDate(line.trim { it <= ' ' }))
                        } catch (e: ParseError) {
                            throw RuntimeException("makeValueFromString() Invalid date format:$line")
                        }
                    }
                    return rval
                }
            } else {
                if (type === FieldID.FieldType.STRING) {
                    return value
                } else if (type === FieldID.FieldType.TEXT) {
                    return value
                } else if (type === FieldID.FieldType.INT) {
                    return value.toInt()
                } else if (type === FieldID.FieldType.BOOL) {
                    val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
                    if (v == "true" || v == "yes") return true
                    return if (v == "false" || v == "no") false else null
                } else if (type === FieldID.FieldType.DATE) {
                    return try {
                        parseDate(value)
                    } catch (e: ParseError) {
                        throw RuntimeException("makeValueFromString() Invalid date format:$value")
                    }
                }
            }
            throw RuntimeException("makeValueFromString() :Don't know how to convert to type:$type")
        }
    }

    init {
        clear()
    }

    protected fun getStructObject(
        id: String,
        mdFields: Map<String, List<FieldDescription>>?,
        parent: Boolean,
        toString: Boolean,
        useDefault: Boolean,
        defaultValue: Any
    ): Any {
        val fields = mdFields!![id]
        if (fields == null || fields.size == 0) {
            if (useDefault) {
                return defaultValue
            }
            throw RuntimeException("getStructObject: No field for '$id'")
        }
        var current: Any = this
        var fieldD: FieldDescription? = null
        for (i in 0 until fields.size - if (parent) 1 else 0) {
            try {
                fieldD = fields[i]
                current = fieldD.field[current]
            } catch (e: IllegalArgumentException) {
                if (useDefault) {
                    return defaultValue
                }
                throw RuntimeException("_getStructObject('$id') IllegalArgumentException:$e")
            } catch (e: IllegalAccessException) {
                if (useDefault) {
                    return defaultValue
                }
                throw RuntimeException("_getStructObject('$id') IllegalAccessException$e")
            }
        }
        return if (toString) {
            fieldD!!.makeStringFromValue(current)
        } else current
    }

    operator fun get(id: String): Any {
        return getStructObject(id, _mdFields, false, false, false, null)
    }

    fun getString(id: String): String {
        return getStructObject(id, _mdFields, false, true, false, null) as String
    }

    operator fun get(id: String, defaultValue: Any): Any {
        return getStructObject(id, _mdFields, false, false, true, defaultValue)
    }

    fun getString(id: String, defaultValue: String): String {
        return getStructObject(id, _mdFields, false, true, true, defaultValue) as String
    }

    protected fun _getObjectEnabled(id: String): Boolean {
        return getStructObject(id, _mdEnabledFields, false, false, true, false) as Boolean
    }

    protected fun _setStructObject(
        id: String,
        value: Any?,
        append: Boolean,
        fromString: Boolean,
        mdFields: Map<String, List<FieldDescription>>?
    ) {
        var value = value
        val fields = mdFields!![id]
        if (fields == null || fields.size == 0) {
            throw RuntimeException("_setStructObject('$id') No such field")
        }
        val current = getStructObject(id, mdFields, true, false, false, null)
            ?: throw RuntimeException("_setStructObject('$id') No such field")
        try {
            val fieldD = fields[fields.size - 1]
            if (fromString && value != null) {
                value = fieldD.makeValueFromString(value.toString())
            }
            if (fieldD.isList && append) {
                var l = fieldD.field[current] as MutableList<Any?>
                if (l == null) {
                    l = ArrayList()
                }
                if (MutableList::class.java.isAssignableFrom(value!!.javaClass)) {
                    l.addAll((value as List<*>?)!!)
                } else {
                    l.add(value)
                }
                fieldD.field[current] = l
            } else {
                fieldD.field[current] = value
            }
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("_setStructObject('$id') IllegalArgumentException:$e")
        } catch (e: IllegalAccessException) {
            throw RuntimeException("_setStructObject('$id') IllegalAccessException$e")
        }
    }

    operator fun set(id: String, value: Any?) {
        _setStructObject(id, value, false, false, _mdFields)
    }

    fun setAppend(id: String, value: Any?) {
        _setStructObject(id, value, true, false, _mdFields)
    }

    fun setFromString(id: String, value: String?) {
        _setStructObject(id, value, false, true, _mdFields)
    }

    fun setAppendFromString(id: String, value: String?) {
        _setStructObject(id, value, true, true, _mdFields)
    }

    protected fun _setObjectEnabled(id: String, value: Boolean) {
        _setStructObject(id, value, false, false, _mdEnabledFields)
    }

    companion object {
        protected var hrSizes = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
        fun keys(): List<String> {
            return ArrayList(_mdFields!!.keys)
        }

        fun keyIsWritable(key: String): Boolean {
            val fd = getFieldDescription(key)
            return fd?.isWritable ?: false
        }

        fun fromPersistenceString(yamlString: String?): MetadataInfo {
            val yaml = Yaml()
            val map = yaml.load<Any>(yamlString) as Map<String, Any>
            val md = MetadataInfo()
            md.fromYAML(yamlString)
            val enMap = map["_enabled"]
            if (enMap != null && MutableMap::class.java.isAssignableFrom(enMap.javaClass)) {
                val enabledMap = enMap as Map<String, Any>
                for (fieldName in _mdEnabledFields!!.keys) {
                    if (enabledMap.containsKey(fieldName)) {
                        md.setEnabled(fieldName, (enabledMap[fieldName] as Boolean?)!!)
                    }
                }
            }
            return md
        }

        private fun traverseFields(
            ancestors: List<FieldDescription>,
            all: Boolean,
            klass: Class<*>,
            mdType: StructType,
            consumer: Consumer<List<FieldDescription>>
        ) {
            for (field in klass.fields) {
                val mdStruct = field.getAnnotation(MdStruct::class.java)
                if (mdStruct != null && mdStruct.type === mdType) {
                    var prefix = if (ancestors.size > 0) ancestors[ancestors.size - 1].name else ""
                    if (prefix.length > 0) {
                        prefix += "."
                    }
                    val name = if (mdStruct.name.length > 0) mdStruct.name else field.name
                    val t = FieldDescription(prefix + name, field, null, mdStruct.access === MdStruct.Access.READ_WRITE)
                    val a: MutableList<FieldDescription> = ArrayList(ancestors)
                    a.add(t)
                    traverseFields(a, true, field.type, mdType, consumer)
                } else {
                    val fieldId = field.getAnnotation(FieldID::class.java)
                    val isParentWritable = if (ancestors.size > 0) ancestors[ancestors.size - 1].isWritable else true
                    if (fieldId != null) {
                        var prefix = if (ancestors.size > 0) ancestors[ancestors.size - 1].name else ""
                        if (prefix.length > 0) {
                            prefix += "."
                        }
                        val t = FieldDescription(prefix + fieldId.value, field, fieldId.type, isParentWritable)
                        val a: MutableList<FieldDescription> = ArrayList(ancestors)
                        a.add(t)
                        consumer.accept(a)
                    } else if (all) {
                        var prefix = if (ancestors.size > 0) ancestors[ancestors.size - 1].name else ""
                        if (prefix.length > 0) {
                            prefix += "."
                        }
                        val t = FieldDescription(prefix + field.name, field, isParentWritable)
                        val a: MutableList<FieldDescription> = ArrayList(ancestors)
                        a.add(t)
                        consumer.accept(a)
                    }
                }
            }
        }

        val _mdFields: MutableMap<String, List<FieldDescription>>? = null
        val _mdEnabledFields: MutableMap<String, List<FieldDescription>>? = null

        init {
            _mdFields = LinkedHashMap()
            _mdEnabledFields = LinkedHashMap()
            traverseFields(
                ArrayList(),
                false,
                MetadataInfo::class.java,
                StructType.MD_STRUCT
            ) { fieldDescs: List<FieldDescription> ->
                if (fieldDescs.size > 0) {
                    _mdFields[fieldDescs[fieldDescs.size - 1].name] = fieldDescs
                }
            }
            traverseFields(
                ArrayList(),
                false,
                MetadataInfo::class.java,
                StructType.MD_ENABLE_STRUCT
            ) { fieldDescs: List<FieldDescription> ->
                if (fieldDescs.size > 0) {
                    _mdEnabledFields[fieldDescs[fieldDescs.size - 1].name] = fieldDescs
                }
            }
        }

        fun getFieldDescription(id: String): FieldDescription? {
            val fields = _mdFields!![id]!!
            return if (fields.size > 0) {
                fields[fields.size - 1]
            } else null
        }

        @JvmStatic
        val sampleMetadata: MetadataInfo
            get() {
                val md = MetadataInfo()
                // Spec is at : http://partners.adobe.com/public/developer/en/xmp/sdk/XMPspecification.pdf
                md.doc!!.title = "Dracula"
                md.doc!!.author = "Bram Stoker"
                md.doc!!.subject =
                    "Horror tales, Epistolary fiction, Gothic fiction (Literary genre), Vampires -- Fiction, Dracula, Count (Fictitious character) -- Fiction, Transylvania (Romania) -- Fiction, Whitby (England) -- Fiction"
                md.doc!!.keywords = "Horror, Gothic, Vampires"
                md.doc!!.creator = "Adobe InDesign CS4 (6.0.6)"
                md.doc!!.producer = "Adobe PDF Library 9.0"
                md.doc!!.creationDate = parseDateOrNull("2012-12-12 00:00:00")
                md.doc!!.modificationDate = parseDateOrNull("2012-12-13 00:00:00")
                md.doc!!.trapped = "True"
                md.basic!!.creatorTool = "Adobe InDesign CS4 (6.0.6)"
                md.basic!!.createDate = md.doc!!.creationDate
                md.basic!!.modifyDate = md.doc!!.modificationDate
                md.basic!!.baseURL = "https://www.gutenberg.org/"
                md.basic!!.rating = 3
                md.basic!!.label = "Horror Fiction Collection"
                md.basic!!.nickname = "dracula"
                md.basic!!.identifiers = mutableListOf("Dracula_original_edition")
                //md.xmpBasic.advisories ;
                md.basic!!.metadataDate = parseDateOrNull("2012-12-14 00:00:00")
                md.pdf!!.pdfVersion = "1.5"
                md.pdf!!.keywords = md.doc!!.keywords
                md.pdf!!.producer = "Adobe PDF Library 9.0"
                md.dc!!.title = md.doc!!.title
                md.dc!!.description = "The famous Bram Stocker book"
                md.dc!!.creators = ArrayList()
                md.dc!!.creators.add("Bram Stocker")
                md.dc!!.subjects =
                    Arrays.asList(*md.doc!!.subject!!.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())
                return md
            }
    }
}
