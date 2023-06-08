package app.pdfx

import app.pdfx.annotations.FieldEnabled
import app.pdfx.annotations.FieldId
import com.toedter.calendar.JDateChooser
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.time.Instant
import java.util.*
import javax.swing.*
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter
import javax.swing.text.PlainDocument

class MetadataEditPane {
    interface FieldSetGet {
        fun apply(field: Any?, anno: FieldId)
    }

    interface FieldEnabledCheckBox {
        fun apply(field: JCheckBox, anno: FieldEnabled)
    }

    var basicMetaPanel: JPanel? = null

    @FieldId("doc.title")
    var basicTitle: JTextField? = null

    @FieldId("doc.author")
    var basicAuthor: JTextField? = null

    @FieldId("doc.subject")
    var basicSubject: JTextArea? = null

    @FieldId(value = "doc.keywords")
    var basicKeywords: JTextArea? = null

    @FieldId("doc.creator")
    var basicCreator: JTextField? = null

    @FieldId("doc.producer")
    var basicProducer: JTextField? = null

    @FieldId("doc.trapped")
    var basicTrapped: JComboBox<String>? = null

    @FieldId(value = "doc.creationDate", type = FieldId.FieldType.DATE)
    var basicCreationDate: JDateChooser? = null

    @FieldId(value = "doc.modificationDate", type = FieldId.FieldType.DATE)
    var basicModificationDate: JDateChooser? = null

    @FieldId("basic.creatorTool")
    var xmpBasicCreatorTool: JTextField? = null

    @FieldId("basic.baseURL")
    var xmpBasicBaseURL: JTextField? = null

    @FieldId("basic.rating")
    var xmpBasicRating: JTextField? = null

    @FieldId("basic.label")
    var xmpBasicLabel: JTextField? = null

    @FieldId("basic.nickname")
    var xmpBasicNickname: JTextField? = null

    @FieldId(value = "basic.identifiers", type = FieldId.FieldType.TEXT)
    var xmpBasicIdentifiers: JTextArea? = null

    @FieldId(value = "basic.advisories", type = FieldId.FieldType.TEXT)
    var xmpBasicAdvisories: JTextArea? = null

    @FieldId(value = "basic.modifyDate", type = FieldId.FieldType.DATE)
    var xmpBasicModifyDate: JDateChooser? = null

    @FieldId(value = "basic.createDate", type = FieldId.FieldType.DATE)
    var xmpBasicCreateDate: JDateChooser? = null

    @FieldId(value = "basic.metadataDate", type = FieldId.FieldType.DATE)
    var xmpBasicMetadataDate: JDateChooser? = null

    @FieldId("pdf.keywords")
    var xmpPdfKeywords: JTextArea? = null

    @FieldId("pdf.pdfVersion")
    var xmpPdfVersion: JTextField? = null

    @FieldId("pdf.producer")
    var xmpPdfProducer: JTextField? = null

    @FieldId("dc.title")
    var xmpDcTitle: JTextField? = null

    @FieldId("dc.coverage")
    var xmpDcCoverage: JTextField? = null

    @FieldId("dc.description")
    var xmpDcDescription: JTextField? = null

    @FieldId(value = "dc.dates", type = FieldId.FieldType.TEXT)
    var xmpDcDates: JTextArea? = null

    @FieldId("dc.format")
    var xmpDcFormat: JTextField? = null

    @FieldId("dc.identifier")
    var xmpDcIdentifier: JTextField? = null

    @FieldId("dc.rights")
    var xmpDcRights: JTextField? = null

    @FieldId("dc.source")
    var xmpDcSource: JTextField? = null

    @FieldId(value = "dc.creators", type = FieldId.FieldType.TEXT)
    var xmpDcCreators: JTextArea? = null

    @FieldId(value = "dc.contributors", type = FieldId.FieldType.TEXT)
    var xmpDcContributors: JTextArea? = null

    @FieldId(value = "dc.languages", type = FieldId.FieldType.TEXT)
    var xmpDcLanguages: JTextArea? = null

    @FieldId(value = "dc.publishers", type = FieldId.FieldType.TEXT)
    var xmpDcPublishers: JTextArea? = null

    @FieldId(value = "dc.relationships", type = FieldId.FieldType.TEXT)
    var xmpDcRelationships: JTextArea? = null

    @FieldId(value = "dc.subjects", type = FieldId.FieldType.TEXT)
    var xmpDcSubjects: JTextArea? = null

    @FieldId(value = "dc.types", type = FieldId.FieldType.TEXT)
    var xmpDcTypes: JTextArea? = null

    @FieldId("rights.certificate")
    var xmpRightsCertificate: JTextField? = null

    @FieldId(value = "rights.marked", type = FieldId.FieldType.BOOL)
    var xmpRightsMarked: JComboBox<String>? = null

    @FieldId(value = "rights.owner", type = FieldId.FieldType.TEXT)
    var xmpRightsOwner: JTextArea? = null

    @FieldId("rights.usageTerms")
    var xmpRightsUsageTerms: JTextArea? = null

    @FieldId("rights.webStatement")
    var xmpRightsWebStatement: JTextField? = null
    var xmlBasicMetaPanel: JPanel? = null
    var xmlPdfMetaPanel: JPanel? = null
    var xmpDcMetaPanel: JPanel? = null
    var xmpRightsMetaPanel: JPanel? = null
    @JvmField
    var tabbedaPane: JTabbedPane? = null
    private var scrollPane: JScrollPane? = null
    private var scrollPane_1: JScrollPane? = null
    private var scrollPane_2: JScrollPane? = null

    @FieldEnabled("doc.title")
    var basicTitleEnabled: JCheckBox? = null

    @FieldEnabled("doc.author")
    var basicAuthorEnabled: JCheckBox? = null

    @FieldEnabled("doc.subject")
    var basicSubjectEnabled: JCheckBox? = null

    @FieldEnabled("doc.keywords")
    var basicKeywordsEnabled: JCheckBox? = null

    @FieldEnabled("doc.creator")
    var basicCreatorEnabled: JCheckBox? = null

    @FieldEnabled("doc.producer")
    var basicProducerEnabled: JCheckBox? = null

    @FieldEnabled("doc.creationDate")
    var basicCreationDateEnabled: JCheckBox? = null

    @FieldEnabled("doc.modificationDate")
    var basicModificationDateEnabled: JCheckBox? = null

    @FieldEnabled("doc.trapped")
    var basicTrappedEnabled: JCheckBox? = null

    @FieldEnabled("basic.creatorTool")
    var xmpBasicCreatorToolEnabled: JCheckBox? = null

    @FieldEnabled("basic.createDate")
    var xmpBasicCreateDateEnabled: JCheckBox? = null

    @FieldEnabled("basic.modifyDate")
    var xmpBasicModifyDateEnabled: JCheckBox? = null

    @FieldEnabled("basic.baseURL")
    var xmpBasicBaseURLEnabled: JCheckBox? = null

    @FieldEnabled("basic.rating")
    var xmpBasicRatingEnable: JCheckBox? = null

    @FieldEnabled("basic.label")
    var xmpBasicLabelEnabled: JCheckBox? = null

    @FieldEnabled("basic.nickname")
    var xmpBasicNicknameEnabled: JCheckBox? = null

    @FieldEnabled("basic.identifiers")
    var xmpBasicIdentifiersEnabled: JCheckBox? = null

    @FieldEnabled("basic.advisories")
    var xmpBasicAdvisoriesEnabled: JCheckBox? = null

    @FieldEnabled("basic.metadataDate")
    var xmpBasicMetadataDateEnabled: JCheckBox? = null

    @FieldEnabled("pdf.keywords")
    var xmpPdfKeywordsEnabled: JCheckBox? = null

    @FieldEnabled("pdf.pdfVersion")
    var xmpPdfVersionEnabled: JCheckBox? = null

    @FieldEnabled("pdf.producer")
    var xmpPdfProducerEnabled: JCheckBox? = null

    @FieldEnabled("dc.title")
    var xmlDcTitleEnabled: JCheckBox? = null

    @FieldEnabled("dc.description")
    var xmpDcDescriptionEnabled: JCheckBox? = null

    @FieldEnabled("dc.creators")
    var xmpDcCreatorsEnabled: JCheckBox? = null

    @FieldEnabled("dc.contributors")
    var xmpDcContributorsEnabled: JCheckBox? = null

    @FieldEnabled("dc.coverage")
    var xmpDcCoverageEnabled: JCheckBox? = null

    @FieldEnabled("dc.dates")
    var xmpDcDatesEnabled: JCheckBox? = null

    @FieldEnabled("dc.format")
    var xmpDcFormatEnabled: JCheckBox? = null

    @FieldEnabled("dc.identifier")
    var xmpDcIdentifierEnabled: JCheckBox? = null

    @FieldEnabled("dc.languages")
    var xmpDcLanguagesEnabled: JCheckBox? = null

    @FieldEnabled("dc.publishers")
    var xmpDcPublishersEnabled: JCheckBox? = null

    @FieldEnabled("dc.relationships")
    var xmpDcRelationshipsEnabled: JCheckBox? = null

    @FieldEnabled("dc.rights")
    var xmpDcRightsEnabled: JCheckBox? = null

    @FieldEnabled("dc.source")
    var xmpDcSourceEnabled: JCheckBox? = null

    @FieldEnabled("dc.subjects")
    var xmpDcSubjectsEnabled: JCheckBox? = null

    @FieldEnabled("dc.types")
    var xmpDcTypesEnabled: JCheckBox? = null

    @FieldEnabled("rights.certificate")
    var xmpRightsCertificateEnabled: JCheckBox? = null

    @FieldEnabled("rights.marked")
    var xmpRightsMarkedEnabled: JCheckBox? = null

    @FieldEnabled("rights.owner")
    var xmpRightsOwnerEnabled: JCheckBox? = null

    @FieldEnabled("rights.usageTerms")
    var xmpRightsUsageTermsEnabled: JCheckBox? = null

    @FieldEnabled("rights.webStatement")
    var xmpRightsWebStatementEnabled: JCheckBox? = null
    private val scrollPane_3: JScrollPane? = null
    private val scrollPane_4: JScrollPane? = null

    init {
        initialize()
    }

    private fun initialize() {
        val startTime = System.nanoTime()
        val cnt: Long = 1
        tabbedaPane = JTabbedPane(JTabbedPane.TOP)
        val basicScrollpane = JScrollPane()
        basicScrollpane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        tabbedaPane!!.addTab("Document", null, basicScrollpane, null)
        basicMetaPanel = JPanel()
        basicScrollpane.setViewportView(basicMetaPanel)
        val gbl_basicMetaPanel = GridBagLayout()
        gbl_basicMetaPanel.columnWidths = intArrayOf(112, 0, 284, 0)
        gbl_basicMetaPanel.rowHeights = intArrayOf(26, 26, 16, 16, 26, 26, 26, 26, 27, 0)
        gbl_basicMetaPanel.columnWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_basicMetaPanel.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
        basicMetaPanel!!.layout = gbl_basicMetaPanel
        val lblTitle = JLabel("Title")
        val gbc_lblTitle = GridBagConstraints()
        gbc_lblTitle.anchor = GridBagConstraints.EAST
        gbc_lblTitle.insets = Insets(0, 0, 5, 5)
        gbc_lblTitle.gridx = 0
        gbc_lblTitle.gridy = 0
        basicMetaPanel!!.add(lblTitle, gbc_lblTitle)
        basicTitleEnabled = JCheckBox("")
        basicTitleEnabled!!.isSelected = true
        basicTitleEnabled!!.isEnabled = false
        val gbc_basicTitleEnabled = GridBagConstraints()
        gbc_basicTitleEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicTitleEnabled.gridx = 1
        gbc_basicTitleEnabled.gridy = 0
        basicMetaPanel!!.add(basicTitleEnabled, gbc_basicTitleEnabled)
        basicTitle = JTextField()
        val gbc_basicTitle = GridBagConstraints()
        gbc_basicTitle.weightx = 1.0
        gbc_basicTitle.anchor = GridBagConstraints.WEST
        gbc_basicTitle.fill = GridBagConstraints.HORIZONTAL
        gbc_basicTitle.insets = Insets(0, 0, 5, 0)
        gbc_basicTitle.gridx = 2
        gbc_basicTitle.gridy = 0
        basicMetaPanel!!.add(basicTitle, gbc_basicTitle)
        basicTitle!!.columns = 10
        val lblNewLabel = JLabel("Author")
        val gbc_lblNewLabel = GridBagConstraints()
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST
        gbc_lblNewLabel.insets = Insets(0, 0, 5, 5)
        gbc_lblNewLabel.gridx = 0
        gbc_lblNewLabel.gridy = 1
        basicMetaPanel!!.add(lblNewLabel, gbc_lblNewLabel)
        basicAuthorEnabled = JCheckBox("")
        basicAuthorEnabled!!.isSelected = true
        basicAuthorEnabled!!.isEnabled = false
        val gbc_basicAuthorEnabled = GridBagConstraints()
        gbc_basicAuthorEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicAuthorEnabled.gridx = 1
        gbc_basicAuthorEnabled.gridy = 1
        basicMetaPanel!!.add(basicAuthorEnabled, gbc_basicAuthorEnabled)
        basicAuthor = JTextField()
        val gbc_basicAuthor = GridBagConstraints()
        gbc_basicAuthor.weightx = 1.0
        gbc_basicAuthor.anchor = GridBagConstraints.WEST
        gbc_basicAuthor.fill = GridBagConstraints.HORIZONTAL
        gbc_basicAuthor.insets = Insets(0, 0, 5, 0)
        gbc_basicAuthor.gridx = 2
        gbc_basicAuthor.gridy = 1
        basicMetaPanel!!.add(basicAuthor, gbc_basicAuthor)
        basicAuthor!!.columns = 10
        val lblSubject = JLabel("Subject")
        val gbc_lblSubject = GridBagConstraints()
        gbc_lblSubject.anchor = GridBagConstraints.NORTHEAST
        gbc_lblSubject.insets = Insets(0, 0, 5, 5)
        gbc_lblSubject.gridx = 0
        gbc_lblSubject.gridy = 2
        basicMetaPanel!!.add(lblSubject, gbc_lblSubject)
        basicSubjectEnabled = JCheckBox("")
        basicSubjectEnabled!!.isSelected = true
        basicSubjectEnabled!!.isEnabled = false
        val gbc_basicSubjectEnabled = GridBagConstraints()
        gbc_basicSubjectEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicSubjectEnabled.gridx = 1
        gbc_basicSubjectEnabled.gridy = 2
        basicMetaPanel!!.add(basicSubjectEnabled, gbc_basicSubjectEnabled)
        scrollPane_1 = JScrollPane()
        val gbc_scrollPane_1 = GridBagConstraints()
        gbc_scrollPane_1.weighty = 0.5
        gbc_scrollPane_1.anchor = GridBagConstraints.WEST
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH
        gbc_scrollPane_1.insets = Insets(0, 0, 5, 0)
        gbc_scrollPane_1.gridx = 2
        gbc_scrollPane_1.gridy = 2
        basicMetaPanel!!.add(scrollPane_1, gbc_scrollPane_1)
        basicSubject = JTextArea()
        basicSubject!!.lineWrap = true
        basicSubject!!.wrapStyleWord = true
        scrollPane_1!!.setViewportView(basicSubject)
        val lblKeywords = JLabel("Keywords")
        val gbc_lblKeywords = GridBagConstraints()
        gbc_lblKeywords.anchor = GridBagConstraints.NORTHEAST
        gbc_lblKeywords.insets = Insets(0, 0, 5, 5)
        gbc_lblKeywords.gridx = 0
        gbc_lblKeywords.gridy = 3
        basicMetaPanel!!.add(lblKeywords, gbc_lblKeywords)
        basicKeywordsEnabled = JCheckBox("")
        basicKeywordsEnabled!!.isEnabled = false
        basicKeywordsEnabled!!.isSelected = true
        val gbc_basicKeywordsEnabled = GridBagConstraints()
        gbc_basicKeywordsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicKeywordsEnabled.gridx = 1
        gbc_basicKeywordsEnabled.gridy = 3
        basicMetaPanel!!.add(basicKeywordsEnabled, gbc_basicKeywordsEnabled)
        scrollPane_2 = JScrollPane()
        val gbc_scrollPane_2 = GridBagConstraints()
        gbc_scrollPane_2.weighty = 0.5
        gbc_scrollPane_2.anchor = GridBagConstraints.WEST
        gbc_scrollPane_2.fill = GridBagConstraints.BOTH
        gbc_scrollPane_2.insets = Insets(0, 0, 5, 0)
        gbc_scrollPane_2.gridx = 2
        gbc_scrollPane_2.gridy = 3
        basicMetaPanel!!.add(scrollPane_2, gbc_scrollPane_2)
        basicKeywords = JTextArea()
        basicKeywords!!.lineWrap = true
        basicKeywords!!.wrapStyleWord = true
        scrollPane_2!!.setViewportView(basicKeywords)
        val lblCreator = JLabel("Creator")
        val gbc_lblCreator = GridBagConstraints()
        gbc_lblCreator.anchor = GridBagConstraints.EAST
        gbc_lblCreator.insets = Insets(0, 0, 5, 5)
        gbc_lblCreator.gridx = 0
        gbc_lblCreator.gridy = 4
        basicMetaPanel!!.add(lblCreator, gbc_lblCreator)
        basicCreatorEnabled = JCheckBox("")
        basicCreatorEnabled!!.isEnabled = false
        basicCreatorEnabled!!.isSelected = true
        val gbc_basicCreatorEnabled = GridBagConstraints()
        gbc_basicCreatorEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicCreatorEnabled.gridx = 1
        gbc_basicCreatorEnabled.gridy = 4
        basicMetaPanel!!.add(basicCreatorEnabled, gbc_basicCreatorEnabled)
        basicCreator = JTextField()
        val gbc_basicCreator = GridBagConstraints()
        gbc_basicCreator.weightx = 1.0
        gbc_basicCreator.anchor = GridBagConstraints.WEST
        gbc_basicCreator.fill = GridBagConstraints.HORIZONTAL
        gbc_basicCreator.insets = Insets(0, 0, 5, 0)
        gbc_basicCreator.gridx = 2
        gbc_basicCreator.gridy = 4
        basicMetaPanel!!.add(basicCreator, gbc_basicCreator)
        basicCreator!!.columns = 10
        val lblProducer = JLabel("Producer")
        val gbc_lblProducer = GridBagConstraints()
        gbc_lblProducer.anchor = GridBagConstraints.EAST
        gbc_lblProducer.insets = Insets(0, 0, 5, 5)
        gbc_lblProducer.gridx = 0
        gbc_lblProducer.gridy = 5
        basicMetaPanel!!.add(lblProducer, gbc_lblProducer)
        basicProducerEnabled = JCheckBox("")
        basicProducerEnabled!!.isEnabled = false
        basicProducerEnabled!!.isSelected = true
        val gbc_basicProducerEnabled = GridBagConstraints()
        gbc_basicProducerEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicProducerEnabled.gridx = 1
        gbc_basicProducerEnabled.gridy = 5
        basicMetaPanel!!.add(basicProducerEnabled, gbc_basicProducerEnabled)
        basicProducer = JTextField()
        val gbc_basicProducer = GridBagConstraints()
        gbc_basicProducer.weightx = 1.0
        gbc_basicProducer.anchor = GridBagConstraints.WEST
        gbc_basicProducer.fill = GridBagConstraints.HORIZONTAL
        gbc_basicProducer.insets = Insets(0, 0, 5, 0)
        gbc_basicProducer.gridx = 2
        gbc_basicProducer.gridy = 5
        basicMetaPanel!!.add(basicProducer, gbc_basicProducer)
        basicProducer!!.columns = 10
        val lblCreationDate = JLabel("Creation Date")
        val gbc_lblCreationDate = GridBagConstraints()
        gbc_lblCreationDate.anchor = GridBagConstraints.EAST
        gbc_lblCreationDate.insets = Insets(0, 0, 5, 5)
        gbc_lblCreationDate.gridx = 0
        gbc_lblCreationDate.gridy = 6
        basicMetaPanel!!.add(lblCreationDate, gbc_lblCreationDate)
        basicCreationDateEnabled = JCheckBox("")
        basicCreationDateEnabled!!.isEnabled = false
        basicCreationDateEnabled!!.isSelected = true
        val gbc_basicCreationDateEnabled = GridBagConstraints()
        gbc_basicCreationDateEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicCreationDateEnabled.gridx = 1
        gbc_basicCreationDateEnabled.gridy = 6
        basicMetaPanel!!.add(basicCreationDateEnabled, gbc_basicCreationDateEnabled)
        basicCreationDate = JDateChooser()
        basicCreationDate!!.dateFormatString = "yyyy-MM-dd HH:mm:ss"
        val gbc_basicCreationDate = GridBagConstraints()
        gbc_basicCreationDate.anchor = GridBagConstraints.WEST
        gbc_basicCreationDate.insets = Insets(0, 0, 5, 0)
        gbc_basicCreationDate.gridx = 2
        gbc_basicCreationDate.gridy = 6
        basicMetaPanel!!.add(basicCreationDate, gbc_basicCreationDate)
        val lblModificationDate = JLabel("Modification Date")
        val gbc_lblModificationDate = GridBagConstraints()
        gbc_lblModificationDate.anchor = GridBagConstraints.WEST
        gbc_lblModificationDate.insets = Insets(0, 0, 5, 5)
        gbc_lblModificationDate.gridx = 0
        gbc_lblModificationDate.gridy = 7
        basicMetaPanel!!.add(lblModificationDate, gbc_lblModificationDate)
        basicModificationDateEnabled = JCheckBox("")
        basicModificationDateEnabled!!.isEnabled = false
        basicModificationDateEnabled!!.isSelected = true
        val gbc_basicModificationDateEnabled = GridBagConstraints()
        gbc_basicModificationDateEnabled.insets = Insets(0, 0, 5, 5)
        gbc_basicModificationDateEnabled.gridx = 1
        gbc_basicModificationDateEnabled.gridy = 7
        basicMetaPanel!!.add(basicModificationDateEnabled, gbc_basicModificationDateEnabled)
        basicModificationDate = JDateChooser()
        basicModificationDate!!.dateFormatString = "yyyy-MM-dd HH:mm:ss"
        val gbc_basicModificationDate = GridBagConstraints()
        gbc_basicModificationDate.anchor = GridBagConstraints.WEST
        gbc_basicModificationDate.insets = Insets(0, 0, 5, 0)
        gbc_basicModificationDate.gridx = 2
        gbc_basicModificationDate.gridy = 7
        basicMetaPanel!!.add(basicModificationDate, gbc_basicModificationDate)
        val lblTrapped = JLabel("Trapped")
        val gbc_lblTrapped = GridBagConstraints()
        gbc_lblTrapped.anchor = GridBagConstraints.EAST
        gbc_lblTrapped.insets = Insets(0, 0, 0, 5)
        gbc_lblTrapped.gridx = 0
        gbc_lblTrapped.gridy = 8
        basicMetaPanel!!.add(lblTrapped, gbc_lblTrapped)
        basicTrappedEnabled = JCheckBox("")
        basicTrappedEnabled!!.isEnabled = false
        basicTrappedEnabled!!.isSelected = true
        val gbc_basicTrappedEnabled = GridBagConstraints()
        gbc_basicTrappedEnabled.insets = Insets(0, 0, 0, 5)
        gbc_basicTrappedEnabled.gridx = 1
        gbc_basicTrappedEnabled.gridy = 8
        basicMetaPanel!!.add(basicTrappedEnabled, gbc_basicTrappedEnabled)
        basicTrapped = JComboBox<String>()
        basicTrapped!!.setModel(DefaultComboBoxModel(arrayOf("True", "False", "Unknown")))
        val gbc_basicTrapped = GridBagConstraints()
        gbc_basicTrapped.anchor = GridBagConstraints.WEST
        gbc_basicTrapped.gridx = 2
        gbc_basicTrapped.gridy = 8
        basicMetaPanel!!.add(basicTrapped, gbc_basicTrapped)
        val xmpBasicScrollpane = JScrollPane()
        xmpBasicScrollpane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        tabbedaPane!!.addTab("XMP Basic", null, xmpBasicScrollpane, null)
        xmlBasicMetaPanel = JPanel()
        xmpBasicScrollpane.setViewportView(xmlBasicMetaPanel)
        val gbl_xmlBasicMetaPanel = GridBagLayout()
        gbl_xmlBasicMetaPanel.columnWidths = intArrayOf(112, 0, 284, 0)
        gbl_xmlBasicMetaPanel.rowHeights = intArrayOf(26, 26, 26, 26, 26, 26, 26, 16, 16, 26, 0)
        gbl_xmlBasicMetaPanel.columnWeights = doubleArrayOf(0.0, 0.0, 1.0, Double.MIN_VALUE)
        gbl_xmlBasicMetaPanel.rowWeights =
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
        xmlBasicMetaPanel!!.layout = gbl_xmlBasicMetaPanel
        val lblCreatorTool = JLabel("Creator tool")
        val gbc_lblCreatorTool = GridBagConstraints()
        gbc_lblCreatorTool.anchor = GridBagConstraints.EAST
        gbc_lblCreatorTool.insets = Insets(0, 0, 5, 5)
        gbc_lblCreatorTool.gridx = 0
        gbc_lblCreatorTool.gridy = 0
        xmlBasicMetaPanel!!.add(lblCreatorTool, gbc_lblCreatorTool)
        xmpBasicCreatorToolEnabled = JCheckBox("")
        xmpBasicCreatorToolEnabled!!.isEnabled = false
        xmpBasicCreatorToolEnabled!!.isSelected = true
        val gbc_xmpBasicCreatorToolEnabled = GridBagConstraints()
        gbc_xmpBasicCreatorToolEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicCreatorToolEnabled.gridx = 1
        gbc_xmpBasicCreatorToolEnabled.gridy = 0
        xmlBasicMetaPanel!!.add(xmpBasicCreatorToolEnabled, gbc_xmpBasicCreatorToolEnabled)
        xmpBasicCreatorTool = JTextField()
        val gbc_xmpBasicCreatorTool = GridBagConstraints()
        gbc_xmpBasicCreatorTool.weightx = 1.0
        gbc_xmpBasicCreatorTool.anchor = GridBagConstraints.WEST
        gbc_xmpBasicCreatorTool.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicCreatorTool.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicCreatorTool.gridx = 2
        gbc_xmpBasicCreatorTool.gridy = 0
        xmlBasicMetaPanel!!.add(xmpBasicCreatorTool, gbc_xmpBasicCreatorTool)
        xmpBasicCreatorTool!!.columns = 10
        val lblCreateDate = JLabel("Create Date")
        val gbc_lblCreateDate = GridBagConstraints()
        gbc_lblCreateDate.anchor = GridBagConstraints.EAST
        gbc_lblCreateDate.insets = Insets(0, 0, 5, 5)
        gbc_lblCreateDate.gridx = 0
        gbc_lblCreateDate.gridy = 1
        xmlBasicMetaPanel!!.add(lblCreateDate, gbc_lblCreateDate)
        xmpBasicCreateDateEnabled = JCheckBox("")
        xmpBasicCreateDateEnabled!!.isEnabled = false
        xmpBasicCreateDateEnabled!!.isSelected = true
        val gbc_xmpBasicCreateDateEnabled = GridBagConstraints()
        gbc_xmpBasicCreateDateEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicCreateDateEnabled.gridx = 1
        gbc_xmpBasicCreateDateEnabled.gridy = 1
        xmlBasicMetaPanel!!.add(xmpBasicCreateDateEnabled, gbc_xmpBasicCreateDateEnabled)
        xmpBasicCreateDate = JDateChooser()
        xmpBasicCreateDate!!.dateFormatString = "yyyy-MM-dd HH:mm:ss"
        val gbc_xmpBasicCreateDate = GridBagConstraints()
        gbc_xmpBasicCreateDate.anchor = GridBagConstraints.WEST
        gbc_xmpBasicCreateDate.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicCreateDate.gridx = 2
        gbc_xmpBasicCreateDate.gridy = 1
        xmlBasicMetaPanel!!.add(xmpBasicCreateDate, gbc_xmpBasicCreateDate)
        val lblModifyDate = JLabel("Modify Date")
        val gbc_lblModifyDate = GridBagConstraints()
        gbc_lblModifyDate.anchor = GridBagConstraints.EAST
        gbc_lblModifyDate.insets = Insets(0, 0, 5, 5)
        gbc_lblModifyDate.gridx = 0
        gbc_lblModifyDate.gridy = 2
        xmlBasicMetaPanel!!.add(lblModifyDate, gbc_lblModifyDate)
        xmpBasicModifyDateEnabled = JCheckBox("")
        xmpBasicModifyDateEnabled!!.isEnabled = false
        xmpBasicModifyDateEnabled!!.isSelected = true
        val gbc_xmpBasicModifyDateEnabled = GridBagConstraints()
        gbc_xmpBasicModifyDateEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicModifyDateEnabled.gridx = 1
        gbc_xmpBasicModifyDateEnabled.gridy = 2
        xmlBasicMetaPanel!!.add(xmpBasicModifyDateEnabled, gbc_xmpBasicModifyDateEnabled)
        xmpBasicModifyDate = JDateChooser()
        xmpBasicModifyDate!!.dateFormatString = "yyyy-MM-dd HH:mm:ss"
        val gbc_xmpBasicModifyDate = GridBagConstraints()
        gbc_xmpBasicModifyDate.anchor = GridBagConstraints.WEST
        gbc_xmpBasicModifyDate.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicModifyDate.gridx = 2
        gbc_xmpBasicModifyDate.gridy = 2
        xmlBasicMetaPanel!!.add(xmpBasicModifyDate, gbc_xmpBasicModifyDate)
        val lblBaseUrl = JLabel("Base URL")
        val gbc_lblBaseUrl = GridBagConstraints()
        gbc_lblBaseUrl.anchor = GridBagConstraints.EAST
        gbc_lblBaseUrl.insets = Insets(0, 0, 5, 5)
        gbc_lblBaseUrl.gridx = 0
        gbc_lblBaseUrl.gridy = 3
        xmlBasicMetaPanel!!.add(lblBaseUrl, gbc_lblBaseUrl)
        xmpBasicBaseURLEnabled = JCheckBox("")
        xmpBasicBaseURLEnabled!!.isEnabled = false
        xmpBasicBaseURLEnabled!!.isSelected = true
        val gbc_xmpBasicBaseURLEnabled = GridBagConstraints()
        gbc_xmpBasicBaseURLEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicBaseURLEnabled.gridx = 1
        gbc_xmpBasicBaseURLEnabled.gridy = 3
        xmlBasicMetaPanel!!.add(xmpBasicBaseURLEnabled, gbc_xmpBasicBaseURLEnabled)
        xmpBasicBaseURL = JTextField()
        val gbc_xmpBasicBaseURL = GridBagConstraints()
        gbc_xmpBasicBaseURL.anchor = GridBagConstraints.WEST
        gbc_xmpBasicBaseURL.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicBaseURL.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicBaseURL.gridx = 2
        gbc_xmpBasicBaseURL.gridy = 3
        xmlBasicMetaPanel!!.add(xmpBasicBaseURL, gbc_xmpBasicBaseURL)
        xmpBasicBaseURL!!.columns = 10
        val lblRating = JLabel("Rating")
        val gbc_lblRating = GridBagConstraints()
        gbc_lblRating.anchor = GridBagConstraints.EAST
        gbc_lblRating.insets = Insets(0, 0, 5, 5)
        gbc_lblRating.gridx = 0
        gbc_lblRating.gridy = 4
        xmlBasicMetaPanel!!.add(lblRating, gbc_lblRating)
        xmpBasicRatingEnable = JCheckBox("")
        xmpBasicRatingEnable!!.isEnabled = false
        xmpBasicRatingEnable!!.isSelected = true
        val gbc_xmpBasicRatingEnable = GridBagConstraints()
        gbc_xmpBasicRatingEnable.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicRatingEnable.gridx = 1
        gbc_xmpBasicRatingEnable.gridy = 4
        xmlBasicMetaPanel!!.add(xmpBasicRatingEnable, gbc_xmpBasicRatingEnable)
        xmpBasicRating = JTextField()
        val gbc_xmpBasicRating = GridBagConstraints()
        gbc_xmpBasicRating.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicRating.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicRating.gridx = 2
        gbc_xmpBasicRating.gridy = 4
        xmlBasicMetaPanel!!.add(xmpBasicRating, gbc_xmpBasicRating)
        val lblLabel = JLabel("Label")
        val gbc_lblLabel = GridBagConstraints()
        gbc_lblLabel.anchor = GridBagConstraints.EAST
        gbc_lblLabel.insets = Insets(0, 0, 5, 5)
        gbc_lblLabel.gridx = 0
        gbc_lblLabel.gridy = 5
        xmlBasicMetaPanel!!.add(lblLabel, gbc_lblLabel)
        xmpBasicLabelEnabled = JCheckBox("")
        xmpBasicLabelEnabled!!.isEnabled = false
        xmpBasicLabelEnabled!!.isSelected = true
        val gbc_xmpBasicLabelEnabled = GridBagConstraints()
        gbc_xmpBasicLabelEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicLabelEnabled.gridx = 1
        gbc_xmpBasicLabelEnabled.gridy = 5
        xmlBasicMetaPanel!!.add(xmpBasicLabelEnabled, gbc_xmpBasicLabelEnabled)
        xmpBasicLabel = JTextField()
        val gbc_xmpBasicLabel = GridBagConstraints()
        gbc_xmpBasicLabel.anchor = GridBagConstraints.WEST
        gbc_xmpBasicLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicLabel.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicLabel.gridx = 2
        gbc_xmpBasicLabel.gridy = 5
        xmlBasicMetaPanel!!.add(xmpBasicLabel, gbc_xmpBasicLabel)
        xmpBasicLabel!!.columns = 10
        val lblNickname = JLabel("Nickname")
        val gbc_lblNickname = GridBagConstraints()
        gbc_lblNickname.anchor = GridBagConstraints.EAST
        gbc_lblNickname.insets = Insets(0, 0, 5, 5)
        gbc_lblNickname.gridx = 0
        gbc_lblNickname.gridy = 6
        xmlBasicMetaPanel!!.add(lblNickname, gbc_lblNickname)
        xmpBasicNicknameEnabled = JCheckBox("")
        xmpBasicNicknameEnabled!!.isEnabled = false
        xmpBasicNicknameEnabled!!.isSelected = true
        val gbc_xmpBasicNicknameEnabled = GridBagConstraints()
        gbc_xmpBasicNicknameEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicNicknameEnabled.gridx = 1
        gbc_xmpBasicNicknameEnabled.gridy = 6
        xmlBasicMetaPanel!!.add(xmpBasicNicknameEnabled, gbc_xmpBasicNicknameEnabled)
        xmpBasicNickname = JTextField()
        val gbc_xmpBasicNickname = GridBagConstraints()
        gbc_xmpBasicNickname.anchor = GridBagConstraints.WEST
        gbc_xmpBasicNickname.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicNickname.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicNickname.gridx = 2
        gbc_xmpBasicNickname.gridy = 6
        xmlBasicMetaPanel!!.add(xmpBasicNickname, gbc_xmpBasicNickname)
        xmpBasicNickname!!.columns = 10
        val label_1 = JLabel("Identifiers")
        val gbc_label_1 = GridBagConstraints()
        gbc_label_1.anchor = GridBagConstraints.NORTHEAST
        gbc_label_1.insets = Insets(0, 0, 5, 5)
        gbc_label_1.gridx = 0
        gbc_label_1.gridy = 7
        xmlBasicMetaPanel!!.add(label_1, gbc_label_1)
        xmpBasicIdentifiersEnabled = JCheckBox("")
        xmpBasicIdentifiersEnabled!!.isEnabled = false
        xmpBasicIdentifiersEnabled!!.isSelected = true
        val gbc_xmpBasicIdentifiersEnabled = GridBagConstraints()
        gbc_xmpBasicIdentifiersEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicIdentifiersEnabled.gridx = 1
        gbc_xmpBasicIdentifiersEnabled.gridy = 7
        xmlBasicMetaPanel!!.add(xmpBasicIdentifiersEnabled, gbc_xmpBasicIdentifiersEnabled)
        xmpBasicIdentifiers = JTextArea()
        val gbc_xmpBasicIdentifiers = GridBagConstraints()
        gbc_xmpBasicIdentifiers.weighty = 0.5
        gbc_xmpBasicIdentifiers.anchor = GridBagConstraints.WEST
        gbc_xmpBasicIdentifiers.fill = GridBagConstraints.BOTH
        gbc_xmpBasicIdentifiers.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicIdentifiers.gridx = 2
        gbc_xmpBasicIdentifiers.gridy = 7
        xmlBasicMetaPanel!!.add(xmpBasicIdentifiers, gbc_xmpBasicIdentifiers)
        val label = JLabel("Advisories")
        val gbc_label = GridBagConstraints()
        gbc_label.anchor = GridBagConstraints.NORTHEAST
        gbc_label.insets = Insets(0, 0, 5, 5)
        gbc_label.gridx = 0
        gbc_label.gridy = 8
        xmlBasicMetaPanel!!.add(label, gbc_label)
        xmpBasicAdvisoriesEnabled = JCheckBox("")
        xmpBasicAdvisoriesEnabled!!.isEnabled = false
        xmpBasicAdvisoriesEnabled!!.isSelected = true
        val gbc_xmpBasicAdvisoriesEnabled = GridBagConstraints()
        gbc_xmpBasicAdvisoriesEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpBasicAdvisoriesEnabled.gridx = 1
        gbc_xmpBasicAdvisoriesEnabled.gridy = 8
        xmlBasicMetaPanel!!.add(xmpBasicAdvisoriesEnabled, gbc_xmpBasicAdvisoriesEnabled)
        xmpBasicAdvisories = JTextArea()
        val gbc_xmpBasicAdvisories = GridBagConstraints()
        gbc_xmpBasicAdvisories.weighty = 0.5
        gbc_xmpBasicAdvisories.anchor = GridBagConstraints.WEST
        gbc_xmpBasicAdvisories.fill = GridBagConstraints.BOTH
        gbc_xmpBasicAdvisories.insets = Insets(0, 0, 5, 0)
        gbc_xmpBasicAdvisories.gridx = 2
        gbc_xmpBasicAdvisories.gridy = 8
        xmlBasicMetaPanel!!.add(xmpBasicAdvisories, gbc_xmpBasicAdvisories)
        val lblMetadataDate = JLabel("Metadata Date")
        val gbc_lblMetadataDate = GridBagConstraints()
        gbc_lblMetadataDate.anchor = GridBagConstraints.WEST
        gbc_lblMetadataDate.insets = Insets(0, 0, 0, 5)
        gbc_lblMetadataDate.gridx = 0
        gbc_lblMetadataDate.gridy = 9
        xmlBasicMetaPanel!!.add(lblMetadataDate, gbc_lblMetadataDate)
        xmpBasicMetadataDateEnabled = JCheckBox("")
        xmpBasicMetadataDateEnabled!!.isEnabled = false
        xmpBasicMetadataDateEnabled!!.isSelected = true
        val gbc_xmpBasicMetadataDateEnabled = GridBagConstraints()
        gbc_xmpBasicMetadataDateEnabled.insets = Insets(0, 0, 0, 5)
        gbc_xmpBasicMetadataDateEnabled.gridx = 1
        gbc_xmpBasicMetadataDateEnabled.gridy = 9
        xmlBasicMetaPanel!!.add(xmpBasicMetadataDateEnabled, gbc_xmpBasicMetadataDateEnabled)
        xmpBasicMetadataDate = JDateChooser()
        xmpBasicMetadataDate!!.dateFormatString = "yyyy-MM-dd HH:mm:ss"
        val gbc_xmpBasicMetadataDate = GridBagConstraints()
        gbc_xmpBasicMetadataDate.anchor = GridBagConstraints.WEST
        gbc_xmpBasicMetadataDate.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpBasicMetadataDate.gridx = 2
        gbc_xmpBasicMetadataDate.gridy = 9
        xmlBasicMetaPanel!!.add(xmpBasicMetadataDate, gbc_xmpBasicMetadataDate)
        val xmpPdfScrollpane = JScrollPane()
        xmpPdfScrollpane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        tabbedaPane!!.addTab("XMP PDF", null, xmpPdfScrollpane, null)
        xmlPdfMetaPanel = JPanel()
        xmpPdfScrollpane.setViewportView(xmlPdfMetaPanel)
        val gbl_xmlPdfMetaPanel = GridBagLayout()
        gbl_xmlPdfMetaPanel.columnWidths = intArrayOf(112, 0, 284, 0)
        gbl_xmlPdfMetaPanel.rowHeights = intArrayOf(16, 26, 26, 0)
        gbl_xmlPdfMetaPanel.columnWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_xmlPdfMetaPanel.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        xmlPdfMetaPanel!!.layout = gbl_xmlPdfMetaPanel
        val lblKeywords_1 = JLabel("Keywords")
        val gbc_lblKeywords_1 = GridBagConstraints()
        gbc_lblKeywords_1.anchor = GridBagConstraints.EAST
        gbc_lblKeywords_1.insets = Insets(0, 0, 5, 5)
        gbc_lblKeywords_1.gridx = 0
        gbc_lblKeywords_1.gridy = 0
        xmlPdfMetaPanel!!.add(lblKeywords_1, gbc_lblKeywords_1)
        xmpPdfKeywordsEnabled = JCheckBox("")
        xmpPdfKeywordsEnabled!!.isEnabled = false
        xmpPdfKeywordsEnabled!!.isSelected = true
        val gbc_xmpPdfKeywordsEnabled = GridBagConstraints()
        gbc_xmpPdfKeywordsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpPdfKeywordsEnabled.gridx = 1
        gbc_xmpPdfKeywordsEnabled.gridy = 0
        xmlPdfMetaPanel!!.add(xmpPdfKeywordsEnabled, gbc_xmpPdfKeywordsEnabled)
        scrollPane = JScrollPane()
        val gbc_scrollPane = GridBagConstraints()
        gbc_scrollPane.weighty = 1.0
        gbc_scrollPane.weightx = 1.0
        gbc_scrollPane.anchor = GridBagConstraints.WEST
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.insets = Insets(0, 0, 5, 0)
        gbc_scrollPane.gridx = 2
        gbc_scrollPane.gridy = 0
        xmlPdfMetaPanel!!.add(scrollPane, gbc_scrollPane)
        xmpPdfKeywords = JTextArea()
        scrollPane!!.setViewportView(xmpPdfKeywords)
        xmpPdfKeywords!!.wrapStyleWord = true
        xmpPdfKeywords!!.lineWrap = true
        xmpPdfKeywords!!.columns = 10
        val lblPdfVersion = JLabel("PDF Version")
        val gbc_lblPdfVersion = GridBagConstraints()
        gbc_lblPdfVersion.anchor = GridBagConstraints.EAST
        gbc_lblPdfVersion.insets = Insets(0, 0, 5, 5)
        gbc_lblPdfVersion.gridx = 0
        gbc_lblPdfVersion.gridy = 1
        xmlPdfMetaPanel!!.add(lblPdfVersion, gbc_lblPdfVersion)
        xmpPdfVersionEnabled = JCheckBox("")
        xmpPdfVersionEnabled!!.isEnabled = false
        xmpPdfVersionEnabled!!.isSelected = true
        val gbc_xmpPdfVersionEnabled = GridBagConstraints()
        gbc_xmpPdfVersionEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpPdfVersionEnabled.gridx = 1
        gbc_xmpPdfVersionEnabled.gridy = 1
        xmlPdfMetaPanel!!.add(xmpPdfVersionEnabled, gbc_xmpPdfVersionEnabled)
        xmpPdfVersion = JTextField()
        xmpPdfVersion!!.isEditable = false
        val gbc_xmpPdfVersion = GridBagConstraints()
        gbc_xmpPdfVersion.anchor = GridBagConstraints.WEST
        gbc_xmpPdfVersion.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpPdfVersion.insets = Insets(0, 0, 5, 0)
        gbc_xmpPdfVersion.gridx = 2
        gbc_xmpPdfVersion.gridy = 1
        xmlPdfMetaPanel!!.add(xmpPdfVersion, gbc_xmpPdfVersion)
        xmpPdfVersion!!.columns = 10
        val lblProducer_1 = JLabel("Producer")
        val gbc_lblProducer_1 = GridBagConstraints()
        gbc_lblProducer_1.anchor = GridBagConstraints.EAST
        gbc_lblProducer_1.insets = Insets(0, 0, 0, 5)
        gbc_lblProducer_1.gridx = 0
        gbc_lblProducer_1.gridy = 2
        xmlPdfMetaPanel!!.add(lblProducer_1, gbc_lblProducer_1)
        xmpPdfProducerEnabled = JCheckBox("")
        xmpPdfProducerEnabled!!.isEnabled = false
        xmpPdfProducerEnabled!!.isSelected = true
        val gbc_xmpPdfProducerEnabled = GridBagConstraints()
        gbc_xmpPdfProducerEnabled.insets = Insets(0, 0, 0, 5)
        gbc_xmpPdfProducerEnabled.gridx = 1
        gbc_xmpPdfProducerEnabled.gridy = 2
        xmlPdfMetaPanel!!.add(xmpPdfProducerEnabled, gbc_xmpPdfProducerEnabled)
        xmpPdfProducer = JTextField()
        val gbc_xmpPdfProducer = GridBagConstraints()
        gbc_xmpPdfProducer.anchor = GridBagConstraints.WEST
        gbc_xmpPdfProducer.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpPdfProducer.gridx = 2
        gbc_xmpPdfProducer.gridy = 2
        xmlPdfMetaPanel!!.add(xmpPdfProducer, gbc_xmpPdfProducer)
        xmpPdfProducer!!.columns = 10
        val xmpDcScrollpane = JScrollPane()
        xmpDcScrollpane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        tabbedaPane!!.addTab("XMP Dublin Core", null, xmpDcScrollpane, null)
        xmpDcMetaPanel = JPanel()
        xmpDcScrollpane.setViewportView(xmpDcMetaPanel)
        val gbl_xmpDcMetaPanel = GridBagLayout()
        gbl_xmpDcMetaPanel.columnWidths = intArrayOf(112, 0, 284, 0)
        gbl_xmpDcMetaPanel.rowHeights = intArrayOf(26, 26, 16, 16, 26, 16, 26, 26, 16, 16, 16, 26, 26, 16, 16, 0)
        gbl_xmpDcMetaPanel.columnWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_xmpDcMetaPanel.rowWeights = doubleArrayOf(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, Double.MIN_VALUE
        )
        xmpDcMetaPanel!!.layout = gbl_xmpDcMetaPanel
        val lblTitle_2 = JLabel("Title")
        val gbc_lblTitle_2 = GridBagConstraints()
        gbc_lblTitle_2.anchor = GridBagConstraints.EAST
        gbc_lblTitle_2.insets = Insets(0, 0, 5, 5)
        gbc_lblTitle_2.gridx = 0
        gbc_lblTitle_2.gridy = 0
        xmpDcMetaPanel!!.add(lblTitle_2, gbc_lblTitle_2)
        xmlDcTitleEnabled = JCheckBox("")
        xmlDcTitleEnabled!!.isEnabled = false
        xmlDcTitleEnabled!!.isSelected = true
        val gbc_xmlDcTitleEnabled = GridBagConstraints()
        gbc_xmlDcTitleEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmlDcTitleEnabled.gridx = 1
        gbc_xmlDcTitleEnabled.gridy = 0
        xmpDcMetaPanel!!.add(xmlDcTitleEnabled, gbc_xmlDcTitleEnabled)
        xmpDcTitle = JTextField()
        val gbc_xmpDcTitle = GridBagConstraints()
        gbc_xmpDcTitle.weightx = 1.0
        gbc_xmpDcTitle.anchor = GridBagConstraints.WEST
        gbc_xmpDcTitle.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcTitle.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcTitle.gridx = 2
        gbc_xmpDcTitle.gridy = 0
        xmpDcMetaPanel!!.add(xmpDcTitle, gbc_xmpDcTitle)
        xmpDcTitle!!.columns = 10
        val lblDescription = JLabel("Description")
        val gbc_lblDescription = GridBagConstraints()
        gbc_lblDescription.anchor = GridBagConstraints.EAST
        gbc_lblDescription.insets = Insets(0, 0, 5, 5)
        gbc_lblDescription.gridx = 0
        gbc_lblDescription.gridy = 1
        xmpDcMetaPanel!!.add(lblDescription, gbc_lblDescription)
        xmpDcDescriptionEnabled = JCheckBox("")
        xmpDcDescriptionEnabled!!.isEnabled = false
        xmpDcDescriptionEnabled!!.isSelected = true
        val gbc_xmpDcDescriptionEnabled = GridBagConstraints()
        gbc_xmpDcDescriptionEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcDescriptionEnabled.gridx = 1
        gbc_xmpDcDescriptionEnabled.gridy = 1
        xmpDcMetaPanel!!.add(xmpDcDescriptionEnabled, gbc_xmpDcDescriptionEnabled)
        xmpDcDescription = JTextField()
        val gbc_xmpDcDescription = GridBagConstraints()
        gbc_xmpDcDescription.anchor = GridBagConstraints.WEST
        gbc_xmpDcDescription.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcDescription.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcDescription.gridx = 2
        gbc_xmpDcDescription.gridy = 1
        xmpDcMetaPanel!!.add(xmpDcDescription, gbc_xmpDcDescription)
        xmpDcDescription!!.columns = 10
        val lblCreators = JLabel("Creators")
        val gbc_lblCreators = GridBagConstraints()
        gbc_lblCreators.anchor = GridBagConstraints.NORTHEAST
        gbc_lblCreators.insets = Insets(0, 0, 5, 5)
        gbc_lblCreators.gridx = 0
        gbc_lblCreators.gridy = 2
        xmpDcMetaPanel!!.add(lblCreators, gbc_lblCreators)
        xmpDcCreatorsEnabled = JCheckBox("")
        xmpDcCreatorsEnabled!!.isEnabled = false
        xmpDcCreatorsEnabled!!.isSelected = true
        val gbc_xmpDcCreatorsEnabled = GridBagConstraints()
        gbc_xmpDcCreatorsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcCreatorsEnabled.gridx = 1
        gbc_xmpDcCreatorsEnabled.gridy = 2
        xmpDcMetaPanel!!.add(xmpDcCreatorsEnabled, gbc_xmpDcCreatorsEnabled)
        xmpDcCreators = JTextArea()
        val gbc_xmpDcCreators = GridBagConstraints()
        gbc_xmpDcCreators.weighty = 0.125
        gbc_xmpDcCreators.anchor = GridBagConstraints.WEST
        gbc_xmpDcCreators.fill = GridBagConstraints.BOTH
        gbc_xmpDcCreators.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcCreators.gridx = 2
        gbc_xmpDcCreators.gridy = 2
        xmpDcMetaPanel!!.add(xmpDcCreators, gbc_xmpDcCreators)
        val lblContributors = JLabel("Contributors")
        val gbc_lblContributors = GridBagConstraints()
        gbc_lblContributors.anchor = GridBagConstraints.NORTHEAST
        gbc_lblContributors.insets = Insets(0, 0, 5, 5)
        gbc_lblContributors.gridx = 0
        gbc_lblContributors.gridy = 3
        xmpDcMetaPanel!!.add(lblContributors, gbc_lblContributors)
        xmpDcContributorsEnabled = JCheckBox("")
        xmpDcContributorsEnabled!!.isEnabled = false
        xmpDcContributorsEnabled!!.isSelected = true
        val gbc_xmpDcContributorsEnabled = GridBagConstraints()
        gbc_xmpDcContributorsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcContributorsEnabled.gridx = 1
        gbc_xmpDcContributorsEnabled.gridy = 3
        xmpDcMetaPanel!!.add(xmpDcContributorsEnabled, gbc_xmpDcContributorsEnabled)
        xmpDcContributors = JTextArea()
        val gbc_xmpDcContributors = GridBagConstraints()
        gbc_xmpDcContributors.weighty = 0.125
        gbc_xmpDcContributors.anchor = GridBagConstraints.WEST
        gbc_xmpDcContributors.fill = GridBagConstraints.BOTH
        gbc_xmpDcContributors.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcContributors.gridx = 2
        gbc_xmpDcContributors.gridy = 3
        xmpDcMetaPanel!!.add(xmpDcContributors, gbc_xmpDcContributors)
        val lblCoverage = JLabel("Coverage")
        val gbc_lblCoverage = GridBagConstraints()
        gbc_lblCoverage.anchor = GridBagConstraints.EAST
        gbc_lblCoverage.insets = Insets(0, 0, 5, 5)
        gbc_lblCoverage.gridx = 0
        gbc_lblCoverage.gridy = 4
        xmpDcMetaPanel!!.add(lblCoverage, gbc_lblCoverage)
        xmpDcCoverageEnabled = JCheckBox("")
        xmpDcCoverageEnabled!!.isEnabled = false
        xmpDcCoverageEnabled!!.isSelected = true
        val gbc_xmpDcCoverageEnabled = GridBagConstraints()
        gbc_xmpDcCoverageEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcCoverageEnabled.gridx = 1
        gbc_xmpDcCoverageEnabled.gridy = 4
        xmpDcMetaPanel!!.add(xmpDcCoverageEnabled, gbc_xmpDcCoverageEnabled)
        xmpDcCoverage = JTextField()
        val gbc_xmpDcCoverage = GridBagConstraints()
        gbc_xmpDcCoverage.anchor = GridBagConstraints.WEST
        gbc_xmpDcCoverage.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcCoverage.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcCoverage.gridx = 2
        gbc_xmpDcCoverage.gridy = 4
        xmpDcMetaPanel!!.add(xmpDcCoverage, gbc_xmpDcCoverage)
        xmpDcCoverage!!.columns = 10
        val lblDates = JLabel("Dates")
        val gbc_lblDates = GridBagConstraints()
        gbc_lblDates.anchor = GridBagConstraints.NORTHEAST
        gbc_lblDates.insets = Insets(0, 0, 5, 5)
        gbc_lblDates.gridx = 0
        gbc_lblDates.gridy = 5
        xmpDcMetaPanel!!.add(lblDates, gbc_lblDates)
        xmpDcDatesEnabled = JCheckBox("")
        xmpDcDatesEnabled!!.isEnabled = false
        xmpDcDatesEnabled!!.isSelected = true
        val gbc_xmpDcDatesEnabled = GridBagConstraints()
        gbc_xmpDcDatesEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcDatesEnabled.gridx = 1
        gbc_xmpDcDatesEnabled.gridy = 5
        xmpDcMetaPanel!!.add(xmpDcDatesEnabled, gbc_xmpDcDatesEnabled)
        xmpDcDates = JTextArea()
        xmpDcDates!!.isEditable = false
        val gbc_xmpDcDates = GridBagConstraints()
        gbc_xmpDcDates.weighty = 0.125
        gbc_xmpDcDates.anchor = GridBagConstraints.WEST
        gbc_xmpDcDates.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcDates.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcDates.gridx = 2
        gbc_xmpDcDates.gridy = 5
        xmpDcMetaPanel!!.add(xmpDcDates, gbc_xmpDcDates)
        xmpDcDates!!.columns = 10
        val lblFormat = JLabel("Format")
        val gbc_lblFormat = GridBagConstraints()
        gbc_lblFormat.anchor = GridBagConstraints.EAST
        gbc_lblFormat.insets = Insets(0, 0, 5, 5)
        gbc_lblFormat.gridx = 0
        gbc_lblFormat.gridy = 6
        xmpDcMetaPanel!!.add(lblFormat, gbc_lblFormat)
        xmpDcFormatEnabled = JCheckBox("")
        xmpDcFormatEnabled!!.isEnabled = false
        xmpDcFormatEnabled!!.isSelected = true
        val gbc_xmpDcFormatEnabled = GridBagConstraints()
        gbc_xmpDcFormatEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcFormatEnabled.gridx = 1
        gbc_xmpDcFormatEnabled.gridy = 6
        xmpDcMetaPanel!!.add(xmpDcFormatEnabled, gbc_xmpDcFormatEnabled)
        xmpDcFormat = JTextField()
        val gbc_xmpDcFormat = GridBagConstraints()
        gbc_xmpDcFormat.anchor = GridBagConstraints.WEST
        gbc_xmpDcFormat.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcFormat.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcFormat.gridx = 2
        gbc_xmpDcFormat.gridy = 6
        xmpDcMetaPanel!!.add(xmpDcFormat, gbc_xmpDcFormat)
        xmpDcFormat!!.columns = 10
        val lblIdentifier = JLabel("Identifier")
        val gbc_lblIdentifier = GridBagConstraints()
        gbc_lblIdentifier.anchor = GridBagConstraints.EAST
        gbc_lblIdentifier.insets = Insets(0, 0, 5, 5)
        gbc_lblIdentifier.gridx = 0
        gbc_lblIdentifier.gridy = 7
        xmpDcMetaPanel!!.add(lblIdentifier, gbc_lblIdentifier)
        xmpDcIdentifierEnabled = JCheckBox("")
        xmpDcIdentifierEnabled!!.isEnabled = false
        xmpDcIdentifierEnabled!!.isSelected = true
        val gbc_xmpDcIdentifierEnabled = GridBagConstraints()
        gbc_xmpDcIdentifierEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcIdentifierEnabled.gridx = 1
        gbc_xmpDcIdentifierEnabled.gridy = 7
        xmpDcMetaPanel!!.add(xmpDcIdentifierEnabled, gbc_xmpDcIdentifierEnabled)
        xmpDcIdentifier = JTextField()
        val gbc_xmpDcIdentifier = GridBagConstraints()
        gbc_xmpDcIdentifier.anchor = GridBagConstraints.WEST
        gbc_xmpDcIdentifier.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcIdentifier.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcIdentifier.gridx = 2
        gbc_xmpDcIdentifier.gridy = 7
        xmpDcMetaPanel!!.add(xmpDcIdentifier, gbc_xmpDcIdentifier)
        xmpDcIdentifier!!.columns = 10
        val lblLanguages = JLabel("Languages")
        val gbc_lblLanguages = GridBagConstraints()
        gbc_lblLanguages.anchor = GridBagConstraints.NORTHEAST
        gbc_lblLanguages.insets = Insets(0, 0, 5, 5)
        gbc_lblLanguages.gridx = 0
        gbc_lblLanguages.gridy = 8
        xmpDcMetaPanel!!.add(lblLanguages, gbc_lblLanguages)
        xmpDcLanguagesEnabled = JCheckBox("")
        xmpDcLanguagesEnabled!!.isEnabled = false
        xmpDcLanguagesEnabled!!.isSelected = true
        val gbc_xmpDcLanguagesEnabled = GridBagConstraints()
        gbc_xmpDcLanguagesEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcLanguagesEnabled.gridx = 1
        gbc_xmpDcLanguagesEnabled.gridy = 8
        xmpDcMetaPanel!!.add(xmpDcLanguagesEnabled, gbc_xmpDcLanguagesEnabled)
        xmpDcLanguages = JTextArea()
        val gbc_xmpDcLanguages = GridBagConstraints()
        gbc_xmpDcLanguages.weighty = 0.125
        gbc_xmpDcLanguages.anchor = GridBagConstraints.WEST
        gbc_xmpDcLanguages.fill = GridBagConstraints.BOTH
        gbc_xmpDcLanguages.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcLanguages.gridx = 2
        gbc_xmpDcLanguages.gridy = 8
        xmpDcMetaPanel!!.add(xmpDcLanguages, gbc_xmpDcLanguages)
        val lblPublishers = JLabel("Publishers")
        val gbc_lblPublishers = GridBagConstraints()
        gbc_lblPublishers.anchor = GridBagConstraints.NORTHEAST
        gbc_lblPublishers.insets = Insets(0, 0, 5, 5)
        gbc_lblPublishers.gridx = 0
        gbc_lblPublishers.gridy = 9
        xmpDcMetaPanel!!.add(lblPublishers, gbc_lblPublishers)
        xmpDcPublishersEnabled = JCheckBox("")
        xmpDcPublishersEnabled!!.isEnabled = false
        xmpDcPublishersEnabled!!.isSelected = true
        val gbc_xmpDcPublishersEnabled = GridBagConstraints()
        gbc_xmpDcPublishersEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcPublishersEnabled.gridx = 1
        gbc_xmpDcPublishersEnabled.gridy = 9
        xmpDcMetaPanel!!.add(xmpDcPublishersEnabled, gbc_xmpDcPublishersEnabled)
        xmpDcPublishers = JTextArea()
        val gbc_xmpDcPublishers = GridBagConstraints()
        gbc_xmpDcPublishers.weighty = 0.125
        gbc_xmpDcPublishers.anchor = GridBagConstraints.WEST
        gbc_xmpDcPublishers.fill = GridBagConstraints.BOTH
        gbc_xmpDcPublishers.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcPublishers.gridx = 2
        gbc_xmpDcPublishers.gridy = 9
        xmpDcMetaPanel!!.add(xmpDcPublishers, gbc_xmpDcPublishers)
        val lblRelationships = JLabel("Relationships")
        lblRelationships.horizontalAlignment = SwingConstants.TRAILING
        val gbc_lblRelationships = GridBagConstraints()
        gbc_lblRelationships.anchor = GridBagConstraints.NORTHEAST
        gbc_lblRelationships.insets = Insets(0, 0, 5, 5)
        gbc_lblRelationships.gridx = 0
        gbc_lblRelationships.gridy = 10
        xmpDcMetaPanel!!.add(lblRelationships, gbc_lblRelationships)
        xmpDcRelationshipsEnabled = JCheckBox("")
        xmpDcRelationshipsEnabled!!.isEnabled = false
        xmpDcRelationshipsEnabled!!.isSelected = true
        val gbc_xmpDcRelationshipsEnabled = GridBagConstraints()
        gbc_xmpDcRelationshipsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcRelationshipsEnabled.gridx = 1
        gbc_xmpDcRelationshipsEnabled.gridy = 10
        xmpDcMetaPanel!!.add(xmpDcRelationshipsEnabled, gbc_xmpDcRelationshipsEnabled)
        xmpDcRelationships = JTextArea()
        val gbc_xmpDcRelationships = GridBagConstraints()
        gbc_xmpDcRelationships.weighty = 0.125
        gbc_xmpDcRelationships.anchor = GridBagConstraints.WEST
        gbc_xmpDcRelationships.fill = GridBagConstraints.BOTH
        gbc_xmpDcRelationships.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcRelationships.gridx = 2
        gbc_xmpDcRelationships.gridy = 10
        xmpDcMetaPanel!!.add(xmpDcRelationships, gbc_xmpDcRelationships)
        val lblRights = JLabel("Rights")
        val gbc_lblRights = GridBagConstraints()
        gbc_lblRights.anchor = GridBagConstraints.EAST
        gbc_lblRights.insets = Insets(0, 0, 5, 5)
        gbc_lblRights.gridx = 0
        gbc_lblRights.gridy = 11
        xmpDcMetaPanel!!.add(lblRights, gbc_lblRights)
        xmpDcRightsEnabled = JCheckBox("")
        xmpDcRightsEnabled!!.isEnabled = false
        xmpDcRightsEnabled!!.isSelected = true
        val gbc_xmpDcRightsEnabled = GridBagConstraints()
        gbc_xmpDcRightsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcRightsEnabled.gridx = 1
        gbc_xmpDcRightsEnabled.gridy = 11
        xmpDcMetaPanel!!.add(xmpDcRightsEnabled, gbc_xmpDcRightsEnabled)
        xmpDcRights = JTextField()
        val gbc_xmpDcRights = GridBagConstraints()
        gbc_xmpDcRights.anchor = GridBagConstraints.WEST
        gbc_xmpDcRights.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcRights.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcRights.gridx = 2
        gbc_xmpDcRights.gridy = 11
        xmpDcMetaPanel!!.add(xmpDcRights, gbc_xmpDcRights)
        xmpDcRights!!.columns = 10
        val lblSource = JLabel("Source")
        val gbc_lblSource = GridBagConstraints()
        gbc_lblSource.anchor = GridBagConstraints.EAST
        gbc_lblSource.insets = Insets(0, 0, 5, 5)
        gbc_lblSource.gridx = 0
        gbc_lblSource.gridy = 12
        xmpDcMetaPanel!!.add(lblSource, gbc_lblSource)
        xmpDcSourceEnabled = JCheckBox("")
        xmpDcSourceEnabled!!.isEnabled = false
        xmpDcSourceEnabled!!.isSelected = true
        val gbc_xmpDcSourceEnabled = GridBagConstraints()
        gbc_xmpDcSourceEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcSourceEnabled.gridx = 1
        gbc_xmpDcSourceEnabled.gridy = 12
        xmpDcMetaPanel!!.add(xmpDcSourceEnabled, gbc_xmpDcSourceEnabled)
        xmpDcSource = JTextField()
        val gbc_xmpDcSource = GridBagConstraints()
        gbc_xmpDcSource.anchor = GridBagConstraints.WEST
        gbc_xmpDcSource.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpDcSource.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcSource.gridx = 2
        gbc_xmpDcSource.gridy = 12
        xmpDcMetaPanel!!.add(xmpDcSource, gbc_xmpDcSource)
        xmpDcSource!!.columns = 10
        val lblSubjects = JLabel("Subjects")
        val gbc_lblSubjects = GridBagConstraints()
        gbc_lblSubjects.anchor = GridBagConstraints.NORTHEAST
        gbc_lblSubjects.insets = Insets(0, 0, 5, 5)
        gbc_lblSubjects.gridx = 0
        gbc_lblSubjects.gridy = 13
        xmpDcMetaPanel!!.add(lblSubjects, gbc_lblSubjects)
        xmpDcSubjectsEnabled = JCheckBox("")
        xmpDcSubjectsEnabled!!.isEnabled = false
        xmpDcSubjectsEnabled!!.isSelected = true
        val gbc_xmpDcSubjectsEnabled = GridBagConstraints()
        gbc_xmpDcSubjectsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcSubjectsEnabled.gridx = 1
        gbc_xmpDcSubjectsEnabled.gridy = 13
        xmpDcMetaPanel!!.add(xmpDcSubjectsEnabled, gbc_xmpDcSubjectsEnabled)
        xmpDcSubjects = JTextArea()
        val gbc_xmpDcSubjects = GridBagConstraints()
        gbc_xmpDcSubjects.weighty = 0.125
        gbc_xmpDcSubjects.anchor = GridBagConstraints.WEST
        gbc_xmpDcSubjects.fill = GridBagConstraints.BOTH
        gbc_xmpDcSubjects.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcSubjects.gridx = 2
        gbc_xmpDcSubjects.gridy = 13
        xmpDcMetaPanel!!.add(xmpDcSubjects, gbc_xmpDcSubjects)
        val lblTypes = JLabel("Types")
        val gbc_lblTypes = GridBagConstraints()
        gbc_lblTypes.anchor = GridBagConstraints.NORTHEAST
        gbc_lblTypes.insets = Insets(0, 0, 0, 5)
        gbc_lblTypes.gridx = 0
        gbc_lblTypes.gridy = 14
        xmpDcMetaPanel!!.add(lblTypes, gbc_lblTypes)
        xmpDcTypesEnabled = JCheckBox("")
        xmpDcTypesEnabled!!.isEnabled = false
        xmpDcTypesEnabled!!.isSelected = true
        val gbc_xmpDcTypesEnabled = GridBagConstraints()
        gbc_xmpDcTypesEnabled.insets = Insets(0, 0, 0, 5)
        gbc_xmpDcTypesEnabled.gridx = 1
        gbc_xmpDcTypesEnabled.gridy = 14
        xmpDcMetaPanel!!.add(xmpDcTypesEnabled, gbc_xmpDcTypesEnabled)
        xmpDcTypes = JTextArea()
        val gbc_xmpDcTypes = GridBagConstraints()
        gbc_xmpDcTypes.insets = Insets(0, 0, 5, 5)
        gbc_xmpDcTypes.weighty = 0.125
        gbc_xmpDcTypes.anchor = GridBagConstraints.WEST
        gbc_xmpDcTypes.fill = GridBagConstraints.BOTH
        gbc_xmpDcTypes.gridx = 2
        gbc_xmpDcTypes.gridy = 14
        xmpDcMetaPanel!!.add(xmpDcTypes, gbc_xmpDcTypes)
        val xmpRightsScrollpane = JScrollPane()
        xmpRightsScrollpane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        tabbedaPane!!.addTab("XMP Rights", null, xmpRightsScrollpane, null)
        xmpRightsMetaPanel = JPanel()
        xmpRightsScrollpane.setViewportView(xmpRightsMetaPanel)
        val gbl_xmpRightsMetaPanel = GridBagLayout()
        gbl_xmpRightsMetaPanel.columnWidths = intArrayOf(112, 0, 284, 0)
        gbl_xmpRightsMetaPanel.rowHeights = intArrayOf(16, 26, 26, 26, 26, 0)
        gbl_xmpRightsMetaPanel.columnWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_xmpRightsMetaPanel.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
        xmpRightsMetaPanel!!.layout = gbl_xmpRightsMetaPanel
        val lblRightsCertificate = JLabel("Certificate")
        val gbc_lblRightsCertificate = GridBagConstraints()
        gbc_lblRightsCertificate.anchor = GridBagConstraints.EAST
        gbc_lblRightsCertificate.insets = Insets(0, 0, 5, 5)
        gbc_lblRightsCertificate.gridx = 0
        gbc_lblRightsCertificate.gridy = 0
        xmpRightsMetaPanel!!.add(lblRightsCertificate, gbc_lblRightsCertificate)
        xmpRightsCertificateEnabled = JCheckBox("")
        xmpRightsCertificateEnabled!!.isEnabled = false
        xmpRightsCertificateEnabled!!.isSelected = true
        val gbc_xmpRightsCertificateEnabled = GridBagConstraints()
        gbc_xmpRightsCertificateEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpRightsCertificateEnabled.gridx = 1
        gbc_xmpRightsCertificateEnabled.gridy = 0
        xmpRightsMetaPanel!!.add(xmpRightsCertificateEnabled, gbc_xmpRightsCertificateEnabled)
        xmpRightsCertificate = JTextField()
        val gbc_xmpRightsCertificate = GridBagConstraints()
        gbc_xmpRightsCertificate.anchor = GridBagConstraints.WEST
        gbc_xmpRightsCertificate.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpRightsCertificate.insets = Insets(0, 0, 5, 0)
        gbc_xmpRightsCertificate.gridx = 2
        gbc_xmpRightsCertificate.gridy = 0
        xmpRightsMetaPanel!!.add(xmpRightsCertificate, gbc_xmpRightsCertificate)
        xmpRightsCertificate!!.columns = 10
        val lblRightsMarked = JLabel("Marked")
        val gbc_lblRightsMarked = GridBagConstraints()
        gbc_lblRightsMarked.anchor = GridBagConstraints.EAST
        gbc_lblRightsMarked.insets = Insets(0, 0, 5, 5)
        gbc_lblRightsMarked.gridx = 0
        gbc_lblRightsMarked.gridy = 1
        xmpRightsMetaPanel!!.add(lblRightsMarked, gbc_lblRightsMarked)
        xmpRightsMarkedEnabled = JCheckBox("")
        xmpRightsMarkedEnabled!!.isEnabled = false
        xmpRightsMarkedEnabled!!.isSelected = true
        val gbc_xmpRightsMarkedEnabled = GridBagConstraints()
        gbc_xmpRightsMarkedEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpRightsMarkedEnabled.gridx = 1
        gbc_xmpRightsMarkedEnabled.gridy = 1
        xmpRightsMetaPanel!!.add(xmpRightsMarkedEnabled, gbc_xmpRightsMarkedEnabled)
        xmpRightsMarked = JComboBox<String>()
        xmpRightsMarked!!.setModel(DefaultComboBoxModel(arrayOf("Unset", "Yes", "No")))
        val gbc_xmpRightsMarked = GridBagConstraints()
        gbc_xmpRightsMarked.anchor = GridBagConstraints.WEST
        gbc_xmpRightsMarked.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpRightsMarked.insets = Insets(0, 0, 5, 0)
        gbc_xmpRightsMarked.gridx = 2
        gbc_xmpRightsMarked.gridy = 1
        xmpRightsMetaPanel!!.add(xmpRightsMarked, gbc_xmpRightsMarked)
        val lblRightsOwner = JLabel("Owners")
        val gbc_lblRightsOwner = GridBagConstraints()
        gbc_lblRightsOwner.anchor = GridBagConstraints.EAST
        gbc_lblRightsOwner.insets = Insets(0, 0, 5, 5)
        gbc_lblRightsOwner.gridx = 0
        gbc_lblRightsOwner.gridy = 2
        xmpRightsMetaPanel!!.add(lblRightsOwner, gbc_lblRightsOwner)
        xmpRightsOwnerEnabled = JCheckBox("")
        xmpRightsOwnerEnabled!!.isEnabled = false
        xmpRightsOwnerEnabled!!.isSelected = true
        val gbc_xmpRightsOwnerEnabled = GridBagConstraints()
        gbc_xmpRightsOwnerEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpRightsOwnerEnabled.gridx = 1
        gbc_xmpRightsOwnerEnabled.gridy = 2
        xmpRightsMetaPanel!!.add(xmpRightsOwnerEnabled, gbc_xmpRightsOwnerEnabled)
        val xmpRightsOwnerScroll = JScrollPane()
        val gbc_xmpRightsOwner = GridBagConstraints()
        gbc_xmpRightsOwner.weighty = 1.0
        gbc_xmpRightsOwner.weightx = 1.0
        gbc_xmpRightsOwner.anchor = GridBagConstraints.WEST
        gbc_xmpRightsOwner.fill = GridBagConstraints.BOTH
        gbc_xmpRightsOwner.insets = Insets(0, 0, 5, 0)
        gbc_xmpRightsOwner.gridx = 2
        gbc_xmpRightsOwner.gridy = 2
        xmpRightsMetaPanel!!.add(xmpRightsOwnerScroll, gbc_xmpRightsOwner)
        xmpRightsOwner = JTextArea()
        xmpRightsOwnerScroll.setViewportView(xmpRightsOwner)
        xmpRightsOwner!!.wrapStyleWord = true
        xmpRightsOwner!!.lineWrap = true
        xmpRightsOwner!!.columns = 10
        val lblRightsUsageTerms = JLabel("Usage Terms")
        val gbc_lblRightsUsageTerms = GridBagConstraints()
        gbc_lblRightsUsageTerms.anchor = GridBagConstraints.EAST
        gbc_lblRightsUsageTerms.insets = Insets(0, 0, 5, 5)
        gbc_lblRightsUsageTerms.gridx = 0
        gbc_lblRightsUsageTerms.gridy = 4
        xmpRightsMetaPanel!!.add(lblRightsUsageTerms, gbc_lblRightsUsageTerms)
        xmpRightsUsageTermsEnabled = JCheckBox("")
        xmpRightsUsageTermsEnabled!!.isEnabled = false
        xmpRightsUsageTermsEnabled!!.isSelected = true
        val gbc_xmpRightsUsageTermsEnabled = GridBagConstraints()
        gbc_xmpRightsUsageTermsEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpRightsUsageTermsEnabled.gridx = 1
        gbc_xmpRightsUsageTermsEnabled.gridy = 4
        xmpRightsMetaPanel!!.add(xmpRightsUsageTermsEnabled, gbc_xmpRightsUsageTermsEnabled)
        val xmpRightsUsageTermsScroll = JScrollPane()
        val gbc_xmpRightsUsageTerms = GridBagConstraints()
        gbc_xmpRightsUsageTerms.weighty = 1.0
        gbc_xmpRightsUsageTerms.weightx = 1.0
        gbc_xmpRightsUsageTerms.anchor = GridBagConstraints.WEST
        gbc_xmpRightsUsageTerms.fill = GridBagConstraints.BOTH
        gbc_xmpRightsUsageTerms.insets = Insets(0, 0, 5, 0)
        gbc_xmpRightsUsageTerms.gridx = 2
        gbc_xmpRightsUsageTerms.gridy = 4
        xmpRightsMetaPanel!!.add(xmpRightsUsageTermsScroll, gbc_xmpRightsUsageTerms)
        xmpRightsUsageTerms = JTextArea()
        xmpRightsUsageTermsScroll.setViewportView(xmpRightsUsageTerms)
        xmpRightsUsageTerms!!.wrapStyleWord = true
        xmpRightsUsageTerms!!.lineWrap = true
        xmpRightsUsageTerms!!.columns = 10
        val lblRightsWebStatement = JLabel("Web Statement")
        val gbc_lblRightsWebStatement = GridBagConstraints()
        gbc_lblRightsWebStatement.anchor = GridBagConstraints.EAST
        gbc_lblRightsWebStatement.insets = Insets(0, 0, 5, 5)
        gbc_lblRightsWebStatement.gridx = 0
        gbc_lblRightsWebStatement.gridy = 5
        xmpRightsMetaPanel!!.add(lblRightsWebStatement, gbc_lblRightsWebStatement)
        xmpRightsWebStatementEnabled = JCheckBox("")
        xmpRightsWebStatementEnabled!!.isEnabled = false
        xmpRightsWebStatementEnabled!!.isSelected = true
        val gbc_xmpRightsWebStatementEnabled = GridBagConstraints()
        gbc_xmpRightsWebStatementEnabled.insets = Insets(0, 0, 5, 5)
        gbc_xmpRightsWebStatementEnabled.gridx = 1
        gbc_xmpRightsWebStatementEnabled.gridy = 5
        xmpRightsMetaPanel!!.add(xmpRightsWebStatementEnabled, gbc_xmpRightsWebStatementEnabled)
        xmpRightsWebStatement = JTextField()
        val gbc_xmpRightsWebStatement = GridBagConstraints()
        gbc_xmpRightsWebStatement.anchor = GridBagConstraints.WEST
        gbc_xmpRightsWebStatement.fill = GridBagConstraints.HORIZONTAL
        gbc_xmpRightsWebStatement.insets = Insets(0, 0, 5, 0)
        gbc_xmpRightsWebStatement.gridx = 2
        gbc_xmpRightsWebStatement.gridy = 5
        xmpRightsMetaPanel!!.add(xmpRightsWebStatement, gbc_xmpRightsWebStatement)


        // Make rating digits only
        val doc = PlainDocument()
        doc.documentFilter = object : DocumentFilter() {
            @Throws(BadLocationException::class)
            override fun insertString(fb: FilterBypass, off: Int, str: String, attr: AttributeSet) {
                fb.insertString(off, str.replace("\\D++".toRegex(), ""), attr) // remove non-digits
            }

            @Throws(BadLocationException::class)
            override fun replace(fb: FilterBypass, off: Int, len: Int, str: String, attr: AttributeSet) {
                fb.replace(off, len, str.replace("\\D++".toRegex(), ""), attr) // remove non-digits
            }
        }
        xmpBasicRating!!.document = doc
    }

    private fun traverseFields(setGet: FieldSetGet?, fieldEnabled: FieldEnabledCheckBox?) {
        for (field in this.javaClass.fields) {
            if (setGet != null) {
                val annos = field.getAnnotation(FieldId::class.java)
                if (annos != null) {
                    if (annos.value != null && annos.value.length > 0) {
                        var f: Any? = null
                        f = try {
                            field[this]
                        } catch (e: IllegalArgumentException) {
                            System.err.println("traverseFields on (" + annos.value + ")")
                            e.printStackTrace()
                            continue
                        } catch (e: IllegalAccessException) {
                            System.err.println("traverseFields on (" + annos.value + ")")
                            e.printStackTrace()
                            continue
                        }
                        setGet.apply(f, annos)
                    }
                }
            }
            if (fieldEnabled != null) {
                val annosEnabled = field.getAnnotation(FieldEnabled::class.java)
                if (annosEnabled != null) {
                    try {
                        val f = field[this] as JCheckBox
                        fieldEnabled.apply(f, annosEnabled)
                    } catch (e: IllegalArgumentException) {
                        System.err.println("traverseFields on (" + annosEnabled.value + ")")
                        e.printStackTrace()
                        continue
                    } catch (e: IllegalAccessException) {
                        System.err.println("traverseFields on (" + annosEnabled.value + ")")
                        e.printStackTrace()
                        continue
                    }
                }
            }
        }
    }

    fun showEnabled(show: Boolean) {
        traverseFields(null, object : FieldEnabledCheckBox {
            override fun apply(field: JCheckBox, anno: FieldEnabled) {
                field.isVisible = show
                field.isEnabled = show
            }
        })
    }

    fun disableEdit() {
        traverseFields(object : FieldSetGet {
            override fun apply(field: Any?, anno: FieldId) {
                if (field is JComponent) {
                    field.isEnabled = false
                }
            }
        }, null)
    }

    fun clear() {
        traverseFields(object : FieldSetGet {
            override fun apply(field: Any?, anno: FieldId) {
                if (field is JTextField) {
                    field.text = null
                }
                if (field is JTextArea) {
                    field.text = null
                }
                if (field is JComboBox<*>) {
                    objectToField(field, null, anno.type === FieldId.FieldType.BOOL)
                }
                if (field is JDateChooser) {
                    objectToField(field, null)
                }
                if (field is JSpinner) {
                    objectToField(field, null)
                }
            }
        }, object : FieldEnabledCheckBox {
            override fun apply(field: JCheckBox, anno: FieldEnabled) {
                field.isSelected = true
            }
        })
    }

    fun fillFromMetadata(metadataInfo: MetadataInfo) {
        traverseFields(object : FieldSetGet {
            override fun apply(field: Any?, anno: FieldId) {
                if (field is JTextField) {
                    field.text = metadataInfo.getString(anno.value)
                }
                if (field is JTextArea) {
                    field.text = metadataInfo.getString(anno.value)
                }
                val value = metadataInfo[anno.value]
                if (field is JComboBox<*>) {
                    objectToField(field, value, anno.type === FieldId.FieldType.BOOL)
                }
                if (field is JDateChooser) {
                    objectToField(field, value)
                }
                if (field is JSpinner) {
                    objectToField(field, value)
                }
            }
        }, object : FieldEnabledCheckBox {
            override fun apply(field: JCheckBox, anno: FieldEnabled) {
                field.isSelected = metadataInfo.isEnabled(anno.value)
            }
        })
    }

    fun copyToMetadata(metadataInfo: MetadataInfo) {
        traverseFields(object : FieldSetGet {
            override fun apply(field: Any?, anno: FieldId) {
                if (field is JTextField || field is JTextArea) {
                    var text = if (field is JTextField) field.text else (field as JTextArea).text
                    if (text!!.length == 0) {
                        text = null
                    }
                    metadataInfo.setFromString(anno.value, text)
                }
                if (field is JSpinner) {
                    when (anno.type) {
                        FieldId.FieldType.INT -> {
                            val i = field.model.value as Int
                            metadataInfo[anno.value] = i
                        }

                        else -> throw RuntimeException("Cannot store Integer in :" + anno.type)
                    }
                }
                if (field is JComboBox<*>) {
                    var text: String? = field.model.selectedItem as String
                    if (text != null && text.length == 0) {
                        text = null
                    }
                    when (anno.type) {
                        FieldId.FieldType.STRING -> metadataInfo[anno.value] = text
                        FieldId.FieldType.BOOL -> metadataInfo.setFromString(anno.value, text)
                        else -> throw RuntimeException("Cannot (store (choice text) in :" + anno.type)
                    }
                }
                if (field is JDateChooser) {
                    when (anno.type) {
                        FieldId.FieldType.DATE -> metadataInfo[anno.value] = field.calendar?.toInstant()
                        else -> throw RuntimeException("Cannot store Date in :" + anno.type)
                    }
                }
            }
        }, object : FieldEnabledCheckBox {
            override fun apply(field: JCheckBox, anno: FieldEnabled) {
                metadataInfo.setEnabled(anno.value, field.isSelected)
            }
        })
    }

    private fun objectToField(field: JComboBox<*>, o: Any?, oIsBool: Boolean) {
        if (o is String) {
            field.model.setSelectedItem(o)
        } else if (o is Boolean || oIsBool) {
            var v = "Unset"
            if (o != null) {
                v = if (o as Boolean) "Yes" else "No"
            }
            field.model.setSelectedItem(v)
        } else if (o == null) {
            field.setSelectedIndex(-1)
        } else {
            val e = RuntimeException("Cannot store non-String object in JComboBox")
            e.printStackTrace()
            throw e
        }
    }

    private fun objectToField(field: JDateChooser, o: Any?) {
        if (o is Instant) {
            field.calendar = o.toCalendar()
        } else if (o == null) {
            field.calendar = null
        } else {
            val e = RuntimeException("Cannot store non-Calendar object in JDateChooser")
            e.printStackTrace()
            throw e
        }
    }

    private fun objectToField(field: JSpinner, o: Any?) {
        if (o is Int) {
            field.value = o
        } else if (o == null) {
            field.value = 0
        } else {
            val e = RuntimeException("Cannot store non-Integerr object in JSpinner")
            e.printStackTrace()
            throw e
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                try {
                    val pane = MetadataEditPane()
                    val frame = JFrame()
                    frame.contentPane.add(pane.tabbedaPane)
                    frame.isVisible = true
                    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
                    frame.setSize(640, 480)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
