package app.pdfx

import app.pdfx.CommandLine.Companion.mdFieldsHelpMessage
import app.pdfx.Main.preferences
import app.pdfx.Version.VersionTuple
import net.miginfocom.swing.MigLayout
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang3.function.Failable
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CompletableFuture
import java.util.prefs.Preferences
import java.util.regex.Pattern
import javax.swing.*
import javax.swing.border.EtchedBorder
import javax.swing.border.LineBorder
import javax.swing.border.TitledBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.HyperlinkEvent
import javax.swing.text.JTextComponent

class PreferencesWindow @JvmOverloads constructor(
    prefs: Preferences,
    defaultMetadata: MetadataInfo?,
    owner: Frame? = null
) : JDialog(owner, true) {
    private val contentPane: JPanel
    var defaultMetadataPane: MetadataEditPane
    var copyBasicToXmp = false
    var copyXmpToBasic = false
    var renameTemplate: String? = null
    var defaultSaveAction: String? = null
    var defaultMetadata: MetadataInfo? = null
    val prefs: Preferences
    var onSave: Runnable? = null
    protected var isWindows: Boolean
    private fun checkForUpdates(): CompletableFuture<Response> {
        val httpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url("http://broken-by.me/download/pdf-metadata-editor/")
            .build()
        return CompletableFuture.supplyAsync(Failable.asSupplier { httpClient.newCall(request).execute() })
    }

    private fun showUpdatesStatus(status: CompletableFuture<Response>) {
        status.thenApplyAsync { response: Response ->
            var versionMsg = "<h3 align=center>Cannot get version information </h3>"
            updateStatusLabel.text = ""
            var file: String? = null
            for (header in response.headers("Content-Disposition")) {
                val matcher = Pattern.compile("filename=\"([^\"]+)\"").matcher(header)
                while (matcher.find()) {
                    file = matcher.group(1)
                }
            }
            if (file == null) {
                return@thenApplyAsync versionMsg
            }
            val installerPatterns = arrayOf(
                "pdfxMetadataEditor-(\\d+)\\.(\\d+)\\.(\\d+)-installer.jar",
                "pdf-metadata-edit-(\\d+)\\.(\\d+)\\.(\\d+)-installer.jar"
            )
            val current = Version.get()
            var latest: VersionTuple? = null
            for (pattern in installerPatterns) {
                latest = VersionTuple(file, pattern)
                if (latest.parseSuccess) {
                    break
                }
            }
            if (current.cmp(latest!!) < 0) {
                versionMsg =
                    ("<h3 align=center>New version available: <a href='http://broken-by.me/pdf-metadata-editor/#download'>"
                            + latest.asString + "</a> , current: " + current.asString + "</h3>")
                updateStatusLabel.text = "Newer version available:" + latest.asString
            } else {
                versionMsg = "<h3 align=center>Version " + current.asString + " is the latest version</h3>"
            }
            versionMsg
        }.whenCompleteAsync { versionMsg: String, t: Throwable ->
            var versionMsg = versionMsg
            versionMsg += "<h4 align=center>Error: " + t.localizedMessage + "</h4>"
            txtpnDf.text = aboutMsg + versionMsg
        }
    }

    fun save() {
        prefs.putBoolean("onsaveCopyXmpTo", copyXmpToBasic)
        prefs.putBoolean("onsaveCopyBasicTo", copyBasicToXmp)
        if (renameTemplate != null && renameTemplate!!.length > 0) prefs.put(
            "renameTemplate",
            renameTemplate
        ) else prefs.remove("renameTemplate")
        defaultMetadataPane.copyToMetadata(defaultMetadata!!)
        prefs.put("defaultMetadata", defaultMetadata!!.toYAML())
        prefs.put("defaultSaveAction", defaultSaveAction)
        if (onSave != null) onSave!!.run()
    }

    fun load() {
        copyBasicToXmp = prefs.getBoolean("onsaveCopyBasicTo", false)
        copyXmpToBasic = prefs.getBoolean("onsaveCopyXmpTo", false)
        renameTemplate = prefs["renameTemplate", null]
        val defaultMetadataYAML = prefs["defaultMetadata", null]
        if (defaultMetadataYAML != null && defaultMetadataYAML.length > 0) {
            defaultMetadata!!.fromYAML(defaultMetadataYAML)
        }
        defaultSaveAction = prefs["defaultSaveAction", "save"]
    }

    fun refresh() {
        onsaveCopyDocumentTo.isSelected = copyBasicToXmp
        onsaveCopyXmpTo.isSelected = copyXmpToBasic
        renameTemplateCombo.selectedItem = renameTemplate
        defaultMetadataPane.fillFromMetadata(defaultMetadata!!)
        showPreview(renameTemplate)
    }

    fun showPreview(template: String?) {
        renameTemplate = template
        val ts = TemplateString(template)
        previewLabel.text = "Preview:" + ts.process(DEMO_METADATA)
    }

    fun onSaveAction(newAction: Runnable?) {
        onSave = newAction
    }

    protected fun updateLicense() {
        labelLicenseStatus.text = "No license"
    }

    private val desc = ""
    protected val previewLabel: JLabel
    protected val renameTemplateCombo: JComboBox<*>
    private val onsaveCopyDocumentTo: JCheckBox
    private val onsaveCopyXmpTo: JCheckBox
    private val buttonGroup = ButtonGroup()
    private var aboutMsg: String? = null
    private val txtpnDf: JTextPane
    protected val updateStatusLabel: JLabel
    private val emailField: JTextField
    private val keyField: JTextField
    private val labelLicenseStatus: JLabel
    /**
     * Create the frame.
     */
    /**
     * @wbp.parser.constructor
     */
    init {
        setLocationRelativeTo(owner)
        val startTime = System.nanoTime()
        val updateCheckResponse = checkForUpdates()
        isWindows = System.getProperty("os.name").startsWith("Windows")
        this.prefs = prefs
        if (defaultMetadata != null) {
            this.defaultMetadata = defaultMetadata
        } else {
            this.defaultMetadata = MetadataInfo()
        }
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(arg0: WindowEvent) {
                save()
                if (onSave != null) {
                    onSave!!.run()
                }
            }
        })
        title = "Preferences"
        minimumSize = Dimension(640, 480)
        contentPane = JPanel()
        setContentPane(contentPane)
        val gbl_contentPane = GridBagLayout()
        gbl_contentPane.columnWidths = intArrayOf(725, 0)
        gbl_contentPane.rowHeights = intArrayOf(389, 29, 0)
        gbl_contentPane.columnWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        gbl_contentPane.rowWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
        contentPane.layout = gbl_contentPane
        val tabbedPane = JTabbedPane(JTabbedPane.TOP)
        val gbc_tabbedPane = GridBagConstraints()
        gbc_tabbedPane.weighty = 1.0
        gbc_tabbedPane.weightx = 1.0
        gbc_tabbedPane.fill = GridBagConstraints.BOTH
        gbc_tabbedPane.insets = Insets(0, 0, 5, 0)
        gbc_tabbedPane.gridx = 0
        gbc_tabbedPane.gridy = 0
        contentPane.add(tabbedPane, gbc_tabbedPane)
        val panelGeneral = JPanel()
        tabbedPane.addTab("General", null, panelGeneral, null)
        panelGeneral.layout = MigLayout("", "[grow]", "[][]")
        val panel_1 = JPanel()
        panel_1.border = TitledBorder(
            LineBorder(Color(184, 207, 229)), "On Save ...",
            TitledBorder.LEADING, TitledBorder.TOP, null, Color(51, 51, 51)
        )
        panel_1.layout = MigLayout("", "[]", "[][]")
        onsaveCopyDocumentTo = JCheckBox("Copy Document To XMP")
        onsaveCopyXmpTo = JCheckBox("Copy XMP To Document")
        onsaveCopyDocumentTo.addActionListener { e: ActionEvent? ->
            if (onsaveCopyDocumentTo.isSelected) {
                onsaveCopyXmpTo.isSelected = false
            }
            copyBasicToXmp = onsaveCopyDocumentTo.isSelected
            copyXmpToBasic = onsaveCopyXmpTo.isSelected
        }
        panel_1.add(onsaveCopyDocumentTo, "cell 0 0,alignx left,aligny top")
        onsaveCopyDocumentTo.isSelected = false

        onsaveCopyXmpTo.addActionListener { e: ActionEvent? ->
            if (onsaveCopyXmpTo.isSelected) {
                onsaveCopyDocumentTo.isSelected = false
            }
            copyBasicToXmp = onsaveCopyDocumentTo.isSelected
            copyXmpToBasic = onsaveCopyXmpTo.isSelected
        }
        panel_1.add(onsaveCopyXmpTo, "cell 0 1")
        onsaveCopyXmpTo.isSelected = false
        panelGeneral.add(panel_1, "flowx,cell 0 0,alignx left,aligny top")
        onsaveCopyXmpTo.isSelected = prefs.getBoolean("onsaveCopyXmpTo", false)
        onsaveCopyDocumentTo.isSelected = prefs.getBoolean("onsaveCopyBasicTo", false)
        val panel = JPanel()
        panel.border = TitledBorder(
            LineBorder(Color(184, 207, 229)), "Rename template",
            TitledBorder.LEADING, TitledBorder.TOP, null, Color(51, 51, 51)
        )
        panelGeneral.add(panel, "cell 0 1,grow")
        panel.layout = MigLayout("", "[grow]", "[][][]")
        previewLabel = JLabel("Preview:")
        panel.add(previewLabel, "cell 0 1")
        val scrollPane = JScrollPane()
        scrollPane.viewportBorder = null
        panel.add(scrollPane, "cell 0 2,grow")
        val txtpnAaa = JTextPane()
        txtpnAaa.background = UIManager.getColor("Panel.background")
        txtpnAaa.isEditable = false
        scrollPane.setViewportView(txtpnAaa)
        txtpnAaa.contentType = "text/html"
        txtpnAaa.text = """
         Supported fields:<br>
         <pre>
         <i>${mdFieldsHelpMessage(60, "  {", "}", false)}</i></pre>
         """.trimIndent()
        txtpnAaa.font = UIManager.getFont("TextPane.font")
        txtpnAaa.caretPosition = 0
        renameTemplateCombo = JComboBox<Any?>()
        renameTemplateCombo.addActionListener { e: ActionEvent? -> showPreview(renameTemplateCombo.model.selectedItem as String) }
        renameTemplateCombo.isEditable = true
        renameTemplateCombo.model = DefaultComboBoxModel<Any?>(
            arrayOf<String?>(
                "", "{doc.author} - {doc.title}.pdf",
                "{doc.author} - {doc.creationDate}.pdf"
            )
        )
        panel.add(renameTemplateCombo, "cell 0 0,growx")
        val saveActionPanel = JPanel()
        saveActionPanel.border =
            TitledBorder(null, "Default save action", TitledBorder.LEADING, TitledBorder.TOP, null, null)
        panelGeneral.add(saveActionPanel, "cell 0 0")
        saveActionPanel.layout = MigLayout("", "[][]", "[][]")
        val rdbtnSave = JRadioButton("Save")
        buttonGroup.add(rdbtnSave)
        saveActionPanel.add(rdbtnSave, "flowy,cell 0 0,alignx left,aligny top")
        val rdbtnSaveAndRename = JRadioButton("Save & rename")
        rdbtnSaveAndRename.addActionListener { e: ActionEvent? -> }
        buttonGroup.add(rdbtnSaveAndRename)
        val rdbtnSaveAs = JRadioButton("Save as ...")
        buttonGroup.add(rdbtnSaveAs)
        saveActionPanel.add(rdbtnSaveAndRename, "cell 0 0,alignx left,aligny top")
        saveActionPanel.add(rdbtnSaveAs, "cell 1 0,aligny top")
        val tcA = renameTemplateCombo.editor.editorComponent as JTextComponent
        val panelDefaults = JPanel()
        tabbedPane.addTab("Defaults", null, panelDefaults, null)
        val gbl_panelDefaults = GridBagLayout()
        gbl_panelDefaults.columnWidths = intArrayOf(555, 0)
        gbl_panelDefaults.rowHeights = intArrayOf(32, 100, 0)
        gbl_panelDefaults.columnWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        gbl_panelDefaults.rowWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        panelDefaults.layout = gbl_panelDefaults
        val lblDefineHereDefault = JLabel(
            "Define here default values for the fields you would like prefilled if not set in the PDF document "
        )
        val gbc_lblDefineHereDefault = GridBagConstraints()
        gbc_lblDefineHereDefault.insets = Insets(5, 5, 0, 0)
        gbc_lblDefineHereDefault.weightx = 1.0
        gbc_lblDefineHereDefault.anchor = GridBagConstraints.NORTH
        gbc_lblDefineHereDefault.fill = GridBagConstraints.HORIZONTAL
        gbc_lblDefineHereDefault.gridx = 0
        gbc_lblDefineHereDefault.gridy = 0
        panelDefaults.add(lblDefineHereDefault, gbc_lblDefineHereDefault)
        val gbc_lblDefineHereDefault1 = GridBagConstraints()
        gbc_lblDefineHereDefault1.weightx = 1.0
        gbc_lblDefineHereDefault1.weighty = 1.0
        gbc_lblDefineHereDefault1.anchor = GridBagConstraints.NORTH
        gbc_lblDefineHereDefault1.fill = GridBagConstraints.BOTH
        gbc_lblDefineHereDefault1.gridx = 0
        gbc_lblDefineHereDefault1.gridy = 1
        defaultMetadataPane = MetadataEditPane()
        panelDefaults.add(defaultMetadataPane.tabbedaPane, gbc_lblDefineHereDefault1)
        val panelOsIntegration = JPanel()
        tabbedPane.addTab("Os Integration", null, panelOsIntegration, null)
        panelOsIntegration.layout = MigLayout("", "[grow]", "[grow]")
        val panel_2 = JPanel()
        panel_2.border = TitledBorder(
            EtchedBorder(EtchedBorder.LOWERED, null, null),
            "Explorer context menu (Windows only)", TitledBorder.LEADING, TitledBorder.TOP, null,
            Color(0, 0, 0)
        )
        panelOsIntegration.add(panel_2, "cell 0 0,grow")
        panel_2.layout = MigLayout("", "[][]", "[growprio 50,grow][growprio 50,grow]")
        val btnRegister = JButton("Add to context menu")
        panel_2.add(btnRegister, "cell 0 0,growx,aligny center")
        val btnUnregister = JButton("Remove from context menu")
        val lblNewLabel_1 = JLabel("")
        panel_2.add(lblNewLabel_1, "cell 1 0 1 2")
        panel_2.add(btnUnregister, "cell 0 1,growx,aligny center")
        btnRegister.isEnabled = isWindows
        btnUnregister.isEnabled = isWindows
        val panelBatchLicense = JPanel()
        tabbedPane.addTab("License", null, panelBatchLicense, null)
        val gbl_panelBatchLicense = GridBagLayout()
        gbl_panelBatchLicense.columnWidths = intArrayOf(0, 0, 0)
        gbl_panelBatchLicense.rowHeights = intArrayOf(0, 0, 0, 0, 0)
        gbl_panelBatchLicense.columnWeights = doubleArrayOf(0.0, 1.0, Double.MIN_VALUE)
        gbl_panelBatchLicense.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
        panelBatchLicense.layout = gbl_panelBatchLicense
        val txtpnEnterLicenseInformation = JTextPane()
        txtpnEnterLicenseInformation.isEditable = false
        txtpnEnterLicenseInformation.background = UIManager.getColor("Panel.background")
        txtpnEnterLicenseInformation.contentType = "text/html"
        txtpnEnterLicenseInformation.text =
            "<h3 align='center'>Enter license information below to use batch operations.</h3><p align='center'>You can get license at <a href=\"" + Constants.batchLicenseUrl + "\">" + Constants.batchLicenseUrl + "</a></p>"
        val gbc_txtpnEnterLicenseInformation = GridBagConstraints()
        gbc_txtpnEnterLicenseInformation.gridwidth = 2
        gbc_txtpnEnterLicenseInformation.insets = Insets(15, 0, 5, 0)
        gbc_txtpnEnterLicenseInformation.fill = GridBagConstraints.HORIZONTAL
        gbc_txtpnEnterLicenseInformation.gridx = 0
        gbc_txtpnEnterLicenseInformation.gridy = 0
        panelBatchLicense.add(txtpnEnterLicenseInformation, gbc_txtpnEnterLicenseInformation)
        txtpnEnterLicenseInformation.addHyperlinkListener { e: HyperlinkEvent ->
            if (e.eventType != HyperlinkEvent.EventType.ACTIVATED) {
                return@addHyperlinkListener
            }
            if (!Desktop.isDesktopSupported()) {
                return@addHyperlinkListener
            }
            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                return@addHyperlinkListener
            }
            val uri = e.url.toURI()
            desktop.browse(uri)

        }
        val lblNewLabel_2 = JLabel("Email")
        val gbc_lblNewLabel_2 = GridBagConstraints()
        gbc_lblNewLabel_2.insets = Insets(15, 15, 5, 5)
        gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST
        gbc_lblNewLabel_2.gridx = 0
        gbc_lblNewLabel_2.gridy = 1
        panelBatchLicense.add(lblNewLabel_2, gbc_lblNewLabel_2)
        emailField = JTextField()
        val gbc_emailField = GridBagConstraints()
        gbc_emailField.insets = Insets(15, 0, 5, 15)
        gbc_emailField.fill = GridBagConstraints.HORIZONTAL
        gbc_emailField.gridx = 1
        gbc_emailField.gridy = 1
        panelBatchLicense.add(emailField, gbc_emailField)
        emailField.columns = 10
        emailField.text = preferences!!["email", ""]
        emailField.document.addDocumentListener(object : DocumentListener {
            override fun removeUpdate(e: DocumentEvent) {
                updateLicense()
            }

            override fun insertUpdate(e: DocumentEvent) {
                updateLicense()
            }

            override fun changedUpdate(e: DocumentEvent) {}
        })
        val lblLicenseKey = JLabel("License key")
        val gbc_lblLicenseKey = GridBagConstraints()
        gbc_lblLicenseKey.anchor = GridBagConstraints.EAST
        gbc_lblLicenseKey.insets = Insets(0, 15, 5, 5)
        gbc_lblLicenseKey.gridx = 0
        gbc_lblLicenseKey.gridy = 2
        panelBatchLicense.add(lblLicenseKey, gbc_lblLicenseKey)
        keyField = JTextField()
        val gbc_keyField = GridBagConstraints()
        gbc_keyField.insets = Insets(0, 0, 5, 15)
        gbc_keyField.fill = GridBagConstraints.HORIZONTAL
        gbc_keyField.gridx = 1
        gbc_keyField.gridy = 2
        panelBatchLicense.add(keyField, gbc_keyField)
        keyField.columns = 10
        keyField.text = preferences!!["key", ""]
        keyField.document.addDocumentListener(object : DocumentListener {
            override fun removeUpdate(e: DocumentEvent) {
                updateLicense()
            }

            override fun insertUpdate(e: DocumentEvent) {
                updateLicense()
            }

            override fun changedUpdate(e: DocumentEvent) {}
        })
        labelLicenseStatus = JLabel("No License")
        val gbc_labelLicenseStatus = GridBagConstraints()
        gbc_labelLicenseStatus.gridwidth = 2
        gbc_labelLicenseStatus.insets = Insets(30, 15, 0, 15)
        gbc_labelLicenseStatus.gridx = 0
        gbc_labelLicenseStatus.gridy = 3
        panelBatchLicense.add(labelLicenseStatus, gbc_labelLicenseStatus)
        val scrollPane_1 = JScrollPane()
        tabbedPane.addTab("About", null, scrollPane_1, null)
        txtpnDf = JTextPane()
        txtpnDf.addHyperlinkListener { e: HyperlinkEvent ->
            if (e.eventType != HyperlinkEvent.EventType.ACTIVATED) {
                return@addHyperlinkListener
            }
            if (!Desktop.isDesktopSupported()) {
                return@addHyperlinkListener
            }
            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                return@addHyperlinkListener
            }
            val uri = e.url.toURI()
            desktop.browse(uri)
        }
        txtpnDf.contentType = "text/html"
        txtpnDf.isEditable = false
        txtpnDf.text =
            "<h1 align=center>PDFx Metadata Editor</h1>\n\n<p align=center><a href=\"http://broken-by.me/pdf-metadata-editor/\">http://broken-by.me/pdf-metadata-editor/</a></p>\n<br>\n<p align=center>If you have suggestions, found bugs or just want to share some idea about it you can write me at : <a href=\"mailto:zarrro@gmail.com\"/>zarrro@gmail.com</a></p>\n<br>".also {
                aboutMsg = it
            }
        scrollPane_1.setViewportView(txtpnDf)
        val panel_3 = JPanel()
        val gbc_panel_3 = GridBagConstraints()
        gbc_panel_3.insets = Insets(0, 5, 0, 5)
        gbc_panel_3.fill = GridBagConstraints.BOTH
        gbc_panel_3.gridx = 0
        gbc_panel_3.gridy = 1
        contentPane.add(panel_3, gbc_panel_3)
        panel_3.layout = BorderLayout(0, 0)
        val btnClose = JButton("Close")
        panel_3.add(btnClose, BorderLayout.EAST)
        updateStatusLabel = JLabel("...")
        panel_3.add(updateStatusLabel, BorderLayout.WEST)
        btnClose.addActionListener { e: ActionEvent? ->
            isVisible = false
            save()
        }
        val onDefaultSaveAction = ActionListener { e: ActionEvent? ->
            if (rdbtnSave.isSelected) {
                defaultSaveAction = "save"
            } else if (rdbtnSaveAndRename.isSelected) {
                defaultSaveAction = "saveRename"
            } else if (rdbtnSaveAs.isSelected) {
                defaultSaveAction = "saveAs"
            }
        }
        rdbtnSave.addActionListener(onDefaultSaveAction)
        rdbtnSaveAndRename.addActionListener(onDefaultSaveAction)
        rdbtnSaveAs.addActionListener(onDefaultSaveAction)
        tcA.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(arg0: DocumentEvent) {
                showPreview(renameTemplateCombo.editor.item as String)
            }

            override fun insertUpdate(arg0: DocumentEvent) {
                showPreview(renameTemplateCombo.editor.item as String)
            }

            override fun removeUpdate(arg0: DocumentEvent) {
                showPreview(renameTemplateCombo.editor.item as String)
            }
        })
        val defaultSaveAction = prefs["defaultSaveAction", "save"]
        if (defaultSaveAction == "saveRename") {
            rdbtnSaveAndRename.isSelected = true
        } else if (defaultSaveAction == "saveAs") {
            rdbtnSaveAndRename.isSelected = true
        } else {
            rdbtnSave.isSelected = true
        }
        SwingUtilities.invokeLater {
            lblNewLabel_1.icon =
                ImageIcon(PreferencesWindow::class.java.getResource("/app/pdfx/os_integration_hint.png"))
        }
        load()
        refresh()
        contentPane.doLayout()
        showUpdatesStatus(updateCheckResponse)
        updateLicense()
    }

    companion object {
        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                try {
                    val frame = PreferencesWindow(
                        Preferences.userRoot().node("pdfxMetadataEditor"),
                        null
                    )
                    frame.isVisible = true
                    frame.defaultCloseOperation = DISPOSE_ON_CLOSE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
