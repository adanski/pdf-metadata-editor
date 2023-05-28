package app.pdfx

import app.pdfx.Main.executeCommand
import app.pdfx.Main.preferences
import net.miginfocom.swing.MigLayout
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import javax.swing.*
import javax.swing.plaf.basic.BasicArrowButton

class PDFMetadataEditWindow(filePath: String?) : JFrame() {
    val fc: JFileChooser
    var currentFile: File? = null
        private set
    private var metadataInfo: MetadataInfo? = MetadataInfo()
    private val defaultMetadata: MetadataInfo
    private var filename: JTextField? = null
    private var preferencesWindow: PreferencesWindow? = null
    private fun clear() {
        filename!!.text = ""
        metadataEditor!!.clear()
    }

    fun loadFile(fileName: String?) {
        currentFile = File(fileName)
        reloadFile()
    }

    fun reloadFile() {
        clear()
        try {
            filename!!.text = currentFile!!.absolutePath
            metadataInfo = MetadataInfo()
            metadataInfo!!.loadFromPDF(currentFile!!)
            metadataInfo!!.copyUnsetExpanded(defaultMetadata, metadataInfo)
            metadataEditor!!.fillFromMetadata(metadataInfo!!)
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error while opening file:\n$e"
            )
        }
    }

    private fun saveFile(newFile: File?) {
        try {
            metadataEditor!!.copyToMetadata(metadataInfo!!)
            metadataInfo!!.copyUnsetExpanded(defaultMetadata, metadataInfo)
            metadataInfo!!.saveAsPDF(currentFile!!, newFile)
            metadataEditor!!.fillFromMetadata(metadataInfo!!)
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error while saving file:\n$e"
            )
        }
    }

    private var metadataEditor: MetadataEditPane? = null
    val saveAction = ActionListener { e: ActionEvent? -> saveFile(null) }
    val saveRenameAction: ActionListener = object : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            saveFile(null)
            val renameTemplate = preferences!!["renameTemplate", null] ?: return
            val ts = TemplateString(renameTemplate)
            val toName = ts.process(metadataInfo)
            val toDir = currentFile!!.parent
            val to = File(toDir, toName)
            try {
                Files.move(currentFile!!.toPath(), to.toPath())
                currentFile = to
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(
                    this@PDFMetadataEditWindow,
                    "Error while renaming file:\n$e1"
                )
            }
            reloadFile()
        }
    }
    val saveAsAction: ActionListener = object : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            val fcSaveAs = JFileChooser()
            val dir = preferences!!["LastDir", null]
            if (dir != null) {
                try {
                    fcSaveAs.currentDirectory = File(dir)
                } catch (e1: Exception) {
                }
            }
            val returnVal = fcSaveAs.showSaveDialog(this@PDFMetadataEditWindow)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                var selected = fcSaveAs.selectedFile
                if (!selected.name.lowercase(Locale.getDefault()).endsWith(".pdf")) {
                    selected = File(selected.absolutePath + ".pdf")
                }
                saveFile(selected)
                currentFile = selected
                reloadFile()

                // save dir as last opened
                preferences!!.put("LastDir", currentFile!!.parent)
            }
        }
    }
    val updateSaveButton = Runnable {
        val saveActionS = preferences!!["defaultSaveAction", "save"]
        for (l in btnSave!!.actionListeners) {
            btnSave!!.removeActionListener(l)
        }
        if (saveActionS == "saveRename") {
            btnSave!!.text = "Save & rename"
            btnSave!!.addActionListener(saveRenameAction)
        } else if (saveActionS == "saveAs") {
            btnSave!!.text = "Save As ..."
            btnSave!!.addActionListener(saveAsAction)
        } else {
            btnSave!!.text = "Save"
            btnSave!!.addActionListener(saveAction)
        }
    }
    protected var btnSave: JButton? = null
        private set

    /**
     * Create the application.
     */
    init {
        fc = JFileChooser()
        defaultMetadata = MetadataInfo()
        initialize()
        val pdfFilter = PdfFilter()
        fc.addChoosableFileFilter(pdfFilter)
        fc.fileFilter = pdfFilter
        clear()
        if (filePath != null) {
            try {
                currentFile = File(filePath)
                reloadFile()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error while opening file:\n$e"
                )
            }
        }
        FileDrop(this, object : FileDrop.Listener {
            override fun filesDropped(files: Array<File?>?, where: Point?) {
                val fdm = glassPane as FileDropSelectMessage
                glassPane.isVisible = false
                repaint()
                val fileNames: MutableList<String> = ArrayList()
                for (file in files!!) {
                    fileNames.add(file!!.absolutePath)
                }
                executeCommand(CommandLine(fileNames, fdm.isBatchOperation))
            }

            override fun dragEnter() {
                glassPane.isVisible = true
            }

            override fun dragLeave() {
                glassPane.isVisible = false
                repaint()
            }

            override fun dragOver(where: Point?) {
                (glassPane as FileDropSelectMessage).setDropPos(where)
                repaint()
            }
        })
        glassPane = FileDropSelectMessage()
    }

    private fun initialize() {
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "PDFx Metadata Editor"
        setBounds(100, 100, 640, 480)
        minimumSize = Dimension(640, 480)
        contentPane.layout = MigLayout("insets 5", "[grow,fill]", "[][grow,fill][grow]")
        val panel = JPanel()
        contentPane.add(panel, "cell 0 0,growx")
        val gbl_panel = GridBagLayout()
        gbl_panel.columnWidths = intArrayOf(105, 421, 20, 40, 0)
        gbl_panel.rowHeights = intArrayOf(36, 0)
        gbl_panel.columnWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_panel.rowWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        panel.layout = gbl_panel
        val btnOpenPdf = JButton("Open PDF")
        val gbc_btnOpenPdf = GridBagConstraints()
        gbc_btnOpenPdf.anchor = GridBagConstraints.WEST
        gbc_btnOpenPdf.insets = Insets(0, 0, 0, 5)
        gbc_btnOpenPdf.gridx = 0
        gbc_btnOpenPdf.gridy = 0
        panel.add(btnOpenPdf, gbc_btnOpenPdf)
        btnOpenPdf.addActionListener { event: ActionEvent? ->
            val dir = preferences!!["LastDir", null]
            if (dir != null) {
                try {
                    fc.currentDirectory = File(dir)
                } catch (e: Exception) {
                }
            }
            val returnVal = fc.showOpenDialog(this@PDFMetadataEditWindow)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentFile = fc.selectedFile
                // This is where a real application would open the file.
                reloadFile()
                // save dir as last opened
                preferences!!.put("LastDir", currentFile.getParent())
            }
        }
        filename = JTextField()
        val gbc_filename = GridBagConstraints()
        gbc_filename.fill = GridBagConstraints.HORIZONTAL
        gbc_filename.insets = Insets(0, 0, 0, 5)
        gbc_filename.gridx = 1
        gbc_filename.gridy = 0
        panel.add(filename, gbc_filename)
        filename!!.isEditable = false
        filename!!.columns = 10
        val horizontalStrut = Box.createHorizontalStrut(20)
        val gbc_horizontalStrut = GridBagConstraints()
        gbc_horizontalStrut.anchor = GridBagConstraints.WEST
        gbc_horizontalStrut.insets = Insets(0, 0, 0, 5)
        gbc_horizontalStrut.gridx = 2
        gbc_horizontalStrut.gridy = 0
        panel.add(horizontalStrut, gbc_horizontalStrut)
        val prefImgURL = PDFMetadataEditWindow::class.java
            .getResource("settings-icon.png")
        val img = ImageIcon(prefImgURL)
        val btnPreferences = JButton("")
        val gbc_btnPreferences = GridBagConstraints()
        gbc_btnPreferences.anchor = GridBagConstraints.WEST
        gbc_btnPreferences.gridx = 3
        gbc_btnPreferences.gridy = 0
        panel.add(btnPreferences, gbc_btnPreferences)
        btnPreferences.icon = img
        btnPreferences.addActionListener { e: ActionEvent? ->
            SwingUtilities.invokeLater {
                if (preferencesWindow == null) {
                    preferencesWindow = PreferencesWindow(preferences, defaultMetadata, this@PDFMetadataEditWindow)
                    preferencesWindow!!.onSaveAction(updateSaveButton)
                }
                preferencesWindow!!.isVisible = true
            }
        }
        metadataEditor = MetadataEditPane()
        contentPane.add(metadataEditor!!.tabbedaPane, "cell 0 1,grow")
        metadataEditor!!.showEnabled(false)


//		metadataEditor = createMetadataEditor();
//		getContentPane().add(metadataEditor,
//				"cell 0 1,growy");
        val panel_4 = JPanel()
        contentPane.add(panel_4, "cell 0 2,growx")
        val gbl_panel_4 = GridBagLayout()
        gbl_panel_4.columnWidths = intArrayOf(340, 286, 0)
        gbl_panel_4.rowHeights = intArrayOf(33, 29, 0)
        gbl_panel_4.columnWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
        gbl_panel_4.rowWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
        panel_4.layout = gbl_panel_4
        val btnCopyXmpTo = JButton("Copy XMP To Document")
        btnCopyXmpTo.addActionListener { e: ActionEvent? ->
            if (metadataInfo != null) {
                metadataEditor!!.copyToMetadata(metadataInfo!!)
                metadataInfo!!.copyXMPToDoc()
                metadataEditor!!.fillFromMetadata(metadataInfo!!)
            }
        }
        val gbc_btnCopyXmpTo = GridBagConstraints()
        gbc_btnCopyXmpTo.anchor = GridBagConstraints.SOUTH
        gbc_btnCopyXmpTo.fill = GridBagConstraints.HORIZONTAL
        gbc_btnCopyXmpTo.insets = Insets(0, 0, 5, 5)
        gbc_btnCopyXmpTo.gridx = 0
        gbc_btnCopyXmpTo.gridy = 0
        panel_4.add(btnCopyXmpTo, gbc_btnCopyXmpTo)
        val btnCopyDocumentTo = JButton("Copy Document To XMP")
        btnCopyDocumentTo.addActionListener { e: ActionEvent? ->
            if (metadataInfo != null) {
                metadataEditor!!.copyToMetadata(metadataInfo!!)
                metadataInfo!!.copyDocToXMP()
                metadataEditor!!.fillFromMetadata(metadataInfo!!)
            }
        }
        val panel_1 = JPanel()
        val gbc_panel_1 = GridBagConstraints()
        gbc_panel_1.fill = GridBagConstraints.BOTH
        gbc_panel_1.gridheight = 2
        gbc_panel_1.gridx = 1
        gbc_panel_1.gridy = 0
        panel_4.add(panel_1, gbc_panel_1)
        panel_1.layout = MigLayout("", "[grow,fill]0[]", "[grow,fill]")
        btnSave = JButton("Save")
        panel_1.add(btnSave, "cell 0 0,alignx left,aligny top, gapright 0")
        btnSave!!.icon = ImageIcon(
            PDFMetadataEditWindow::class.java
                .getResource("save-icon.png")
        )
        val btnSaveMenu = BasicArrowButton(BasicArrowButton.SOUTH)
        btnSaveMenu.addActionListener { e: ActionEvent? ->
            val menu = JPopupMenu()
            val save = menu.add("Save")
            save.addActionListener(saveAction)
            val saveRename = menu.add("Save & rename")
            saveRename.addActionListener(saveRenameAction)
            val saveAs = menu.add("Save As ...")
            saveAs.addActionListener(saveAsAction)
            val x: Int
            val y: Int
            val pos = btnSaveMenu.locationOnScreen
            x = pos.x
            y = pos.y + btnSaveMenu.height
            menu.show(btnSaveMenu, btnSaveMenu.width - menu.preferredSize.getWidth().toInt(), btnSaveMenu.height)
        }
        panel_1.add(btnSaveMenu, "cell 1 0,growx,aligny center, gapleft 0")
        val gbc_btnCopyDocumentTo = GridBagConstraints()
        gbc_btnCopyDocumentTo.anchor = GridBagConstraints.NORTH
        gbc_btnCopyDocumentTo.fill = GridBagConstraints.HORIZONTAL
        gbc_btnCopyDocumentTo.insets = Insets(0, 0, 0, 5)
        gbc_btnCopyDocumentTo.gridx = 0
        gbc_btnCopyDocumentTo.gridy = 1
        panel_4.add(btnCopyDocumentTo, gbc_btnCopyDocumentTo)
        updateSaveButton.run()
        val imgURL = PDFMetadataEditWindow::class.java
            .getResource("pdf-metadata-edit.png")
        val icoImg = ImageIcon(imgURL)
        iconImage = icoImg.image
        isVisible = true
    }

    fun createMetadataEditor(): MetadataEditPane {
        return MetadataEditPane()
    }
}
