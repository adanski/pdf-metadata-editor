package io.pdfx.app

import io.pdfx.app.BatchOperationParameters.Companion.loadForCommand
import io.pdfx.app.PdfMetadataEditBatch.ActionStatus
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowEvent
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.ExecutionException
import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.text.BadLocationException
import javax.swing.text.StyleConstants

private const val LAST_USED_COMMAND_KEY = "lastUsedBatchCommand"

class BatchOperationWindow(command: CommandDescription?) : JFrame() {
    private val statusText: JTextPane
    private val closeWindowActionListener = ActionListener { e: ActionEvent? ->
        dispatchEvent(
            WindowEvent(
                this@BatchOperationWindow,
                WindowEvent.WINDOW_CLOSING
            )
        )
    }
    private val btnAction: JButton
    private val btnCancel: JButton
    private val statusScrollPane: JScrollPane
    private val fileList: JTextPane
    private val lblStatus: JLabel
    private val scrollPane_1: JScrollPane
    private val selectedBatchOperation: JComboBox<CommandDescription>
    private var parametersWindow: BatchParametersWindow? = null
    private val batchParameters: MutableMap<String, BatchOperationParameters> = HashMap()
    var batchFileList: MutableList<File> = ArrayList()
    var hasErrors = false
    private val btnParameters: JButton

    init {
        title = "Batch PDF metadata edit"
        setBounds(100, 100, 640, 480)
        minimumSize = Dimension(640, 480)
        val gridBagLayout = GridBagLayout()
        gridBagLayout.columnWidths = intArrayOf(1, 487, 113, 0)
        gridBagLayout.rowHeights = intArrayOf(29, 70, 16, 217, 45, 29, 0)
        gridBagLayout.columnWeights = doubleArrayOf(0.0, 1.0, 0.0, Double.MIN_VALUE)
        gridBagLayout.rowWeights = doubleArrayOf(0.0, 1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE)
        contentPane.layout = gridBagLayout
        btnParameters = JButton("Parameters")
        btnParameters.addActionListener { e: ActionEvent? -> createBatchParametersWindow() }
        selectedBatchOperation = JComboBox()
        val gbc_selectedBatchOperation = GridBagConstraints()
        gbc_selectedBatchOperation.fill = GridBagConstraints.HORIZONTAL
        gbc_selectedBatchOperation.insets = Insets(10, 10, 5, 5)
        gbc_selectedBatchOperation.gridwidth = 2
        gbc_selectedBatchOperation.gridx = 0
        gbc_selectedBatchOperation.gridy = 0
        contentPane.add(selectedBatchOperation, gbc_selectedBatchOperation)
        selectedBatchOperation.addActionListener { e: ActionEvent? -> createBatchParametersWindowButton() }
        val gbc_btnParameters = GridBagConstraints()
        gbc_btnParameters.anchor = GridBagConstraints.NORTHWEST
        gbc_btnParameters.insets = Insets(10, 0, 5, 10)
        gbc_btnParameters.gridx = 2
        gbc_btnParameters.gridy = 0
        contentPane.add(btnParameters, gbc_btnParameters)
        scrollPane_1 = JScrollPane()
        val gbc_scrollPane_1 = GridBagConstraints()
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH
        gbc_scrollPane_1.insets = Insets(0, 10, 5, 10)
        gbc_scrollPane_1.gridwidth = 3
        gbc_scrollPane_1.gridx = 0
        gbc_scrollPane_1.gridy = 1
        contentPane.add(scrollPane_1, gbc_scrollPane_1)
        fileList = JTextPane()
        fileList.text = "Drop files here to batch process them ..."
        scrollPane_1.setViewportView(fileList)
        fileList.isEditable = false
        lblStatus = JLabel("Status")
        val gbc_lblStatus = GridBagConstraints()
        gbc_lblStatus.anchor = GridBagConstraints.NORTHWEST
        gbc_lblStatus.insets = Insets(0, 10, 5, 10)
        gbc_lblStatus.gridwidth = 2
        gbc_lblStatus.gridx = 0
        gbc_lblStatus.gridy = 2
        contentPane.add(lblStatus, gbc_lblStatus)
        statusScrollPane = JScrollPane()
        val gbc_statusScrollPane = GridBagConstraints()
        gbc_statusScrollPane.fill = GridBagConstraints.BOTH
        gbc_statusScrollPane.insets = Insets(0, 10, 5, 10)
        gbc_statusScrollPane.gridwidth = 3
        gbc_statusScrollPane.gridx = 0
        gbc_statusScrollPane.gridy = 3
        contentPane.add(statusScrollPane, gbc_statusScrollPane)
        statusText = JTextPane()
        statusScrollPane.setViewportView(statusText)
        statusText.isEditable = false
        val estyle = statusText.addStyle("ERROR", null)
        val txtpnnoBatchLicense: JTextPane = JTextPane()
        txtpnnoBatchLicense.isEditable = false
        txtpnnoBatchLicense.background = UIManager.getColor("Panel.background")
        txtpnnoBatchLicense.contentType = "text/html"
        txtpnnoBatchLicense.text = "<p align=center>No batch license. In order to use batch operations please get a license from <a href='" + Constants.batchLicenseUrl + "'>" + Constants.batchLicenseUrl + "<a></p>"
        val gbc_txtpnnoBatchLicense = GridBagConstraints()
        gbc_txtpnnoBatchLicense.fill = GridBagConstraints.BOTH
        gbc_txtpnnoBatchLicense.insets = Insets(0, 10, 5, 10)
        gbc_txtpnnoBatchLicense.gridwidth = 3
        gbc_txtpnnoBatchLicense.gridx = 0
        gbc_txtpnnoBatchLicense.gridy = 4
        contentPane.add(txtpnnoBatchLicense, gbc_txtpnnoBatchLicense)
        txtpnnoBatchLicense.addHyperlinkListener { e: HyperlinkEvent ->
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
        val panel = JPanel()
        val gbc_panel = GridBagConstraints()
        gbc_panel.anchor = GridBagConstraints.WEST
        gbc_panel.insets = Insets(0, 0, 0, 5)
        gbc_panel.gridx = 0
        gbc_panel.gridy = 5
        contentPane.add(panel, gbc_panel)
        panel.layout = BorderLayout(0, 0)
        btnCancel = JButton("Cancel")
        btnCancel.addActionListener(closeWindowActionListener)
        val gbc_btnCancel = GridBagConstraints()
        gbc_btnCancel.anchor = GridBagConstraints.WEST
        gbc_btnCancel.insets = Insets(0, 10, 10, 5)
        gbc_btnCancel.gridx = 1
        gbc_btnCancel.gridy = 5
        contentPane.add(btnCancel, gbc_btnCancel)
        btnAction = JButton("Begin")
        btnAction.addActionListener { e: ActionEvent? -> runBatch() }
        val gbc_btnAction = GridBagConstraints()
        gbc_btnAction.insets = Insets(0, 0, 10, 10)
        gbc_btnAction.anchor = GridBagConstraints.NORTHEAST
        gbc_btnAction.gridx = 2
        gbc_btnAction.gridy = 5
        contentPane.add(btnAction, gbc_btnAction)
        if (command != null) {
            selectedBatchOperation.setModel(DefaultComboBoxModel(arrayOf(command)))
        } else {
            selectedBatchOperation.setModel(DefaultComboBoxModel(CommandDescription.batchCommands))
            val lastUsedCommand = Main.preferences[LAST_USED_COMMAND_KEY, null]
            if (lastUsedCommand != null) {
                val lastCommand = CommandDescription.getBatchCommand(lastUsedCommand)
                if (lastCommand != null) {
                    selectedBatchOperation.selectedItem = lastCommand
                }
            }
        }
        StyleConstants.setForeground(estyle, Color.red)
        createBatchParametersWindowButton()
        FileDrop(this, object : FileDrop.Listener {
            override fun filesDropped(files: Array<File>, where: Point) {
                glassPane.isVisible = false
                repaint()
                appendFiles(listOf(*files))
            }

            override fun dragEnter() {
                glassPane.isVisible = true
            }

            override fun dragLeave() {
                glassPane.isVisible = false
                repaint()
            }

            override fun dragOver(where: Point) {}
        })
        glassPane = FileDropMessage()
        btnAction.isEnabled = true
        gridBagLayout.rowHeights[4] = 0
        contentPane.remove(txtpnnoBatchLicense)
        gridBagLayout.removeLayoutComponent(txtpnnoBatchLicense)
        val imgURL = PdfMetadataEditWindow::class.java
            .getResource("pdf-metadata-edit.png")
        val icoImg = ImageIcon(imgURL)
        iconImage = icoImg.image
    }

    fun append(s: String?) {
        try {
            val doc = statusText.document
            doc.insertString(doc.length, s, null)
            statusScrollPane.verticalScrollBar.value = statusScrollPane.verticalScrollBar.maximum
        } catch (exc: BadLocationException) {
            exc.printStackTrace()
        }
    }

    fun appendError(s: String?) {
        hasErrors = true
        try {
            val doc = statusText.styledDocument
            doc.insertString(doc.length, s, statusText.getStyle("ERROR"))
            statusScrollPane.verticalScrollBar.value = statusScrollPane.verticalScrollBar.maximum
        } catch (exc: BadLocationException) {
            exc.printStackTrace()
        }
    }

    fun appendError(e: Throwable) {
        hasErrors = true
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            sw.toString() // stack trace as a string
            val doc = statusText.styledDocument
            doc.insertString(doc.length, sw.toString(), statusText.getStyle("ERROR"))
            statusScrollPane.verticalScrollBar.value = statusScrollPane.verticalScrollBar.maximum
        } catch (exc: BadLocationException) {
            exc.printStackTrace()
        }
    }

    fun appendFiles(files: List<File>) {
        SwingUtilities.invokeLater {
            if (batchFileList.isEmpty() && files.size > 0) {
                val doc = fileList.document
                doc.remove(0, doc.length)
            }
            for (file in files) {
                try {
                    val doc = fileList.document
                    doc.insertString(doc.length, file.absolutePath + "\n", null)
                } catch (exc: BadLocationException) {
                    exc.printStackTrace()
                }
            }
            batchFileList.addAll(files)
        }
    }

    fun runBatch() {
        val command = selectedBatchOperation.selectedItem as CommandDescription
        Main.preferences.put(LAST_USED_COMMAND_KEY, command.name)
        object : Worker() {
            var actionStatus: ActionStatus = object : ActionStatus {
                override fun addStatus(filename: String, message: String) {
                    publish(FileOpResult(filename, message, false))
                }

                override fun addError(filename: String, error: String) {
                    publish(FileOpResult(filename, error, true))
                }
            }

            override fun doInBackground(): Unit {
                val params = getBatchParameters(command)
                params.storeForCommand(command)
                val batch = PdfMetadataEditBatch(params)
                batch.runCommand(command, batchFileList, actionStatus)
            }

            override fun done() {
                try {
                    get()
                } catch (e: InterruptedException) {
                    appendError(e)
                } catch (e: ExecutionException) {
                    appendError(e)
                }
                onDone()
            }
        }.execute()
    }

    fun onDone() {
        try {
            append("------\n")
            if (hasErrors) {
                appendError("Done (with Errors)\n")
            } else {
                append("Done")
            }
            clearActionListeners(btnAction)
            btnAction.text = "Close"
            btnAction.addActionListener(closeWindowActionListener)
            btnCancel.isVisible = false
            FileDrop.remove(this)
        } catch (ignore: Exception) {
        }
    }

    private class FileOpResult(var filename: String, var message: String, var error: Boolean)
    private abstract inner class Worker : SwingWorker<Unit, FileOpResult>() {
        override fun process(chunks: List<FileOpResult>) {
            for (chunk in chunks) {
                if (chunk.error) {
                    appendError(chunk.filename + " -> " + chunk.message + "\n")
                } else {
                    append(chunk.filename + " -> " + chunk.message + "\n")
                }
            }
        }
    }

    private fun getBatchParameters(command: CommandDescription): BatchOperationParameters {
        var params = batchParameters[command.name]
        if (params == null) {
            params = loadForCommand(command)
            batchParameters[command.name] = params
        }
        return params
    }

    private fun createBatchParametersWindow() {
        val command = selectedBatchOperation.selectedItem as CommandDescription
        if (parametersWindow != null) {
            parametersWindow!!.isVisible = false
            parametersWindow!!.dispose()
            parametersWindow = null
        }
        val params = getBatchParameters(command)
        if (command.`is`("clear")) {
            parametersWindow = BatchParametersClear(params, this)
        }
        if (command.`is`("edit")) {
            parametersWindow = BatchParametersEdit(params, this)
        }
        if (command.`is`("rename")) {
            parametersWindow = BatchParametersRename(params, this)
        }
        if (parametersWindow != null) {
            parametersWindow!!.isModal = true
            parametersWindow!!.isVisible = true
        }
    }

    fun createBatchParametersWindowButton() {
        val command = selectedBatchOperation.selectedItem as CommandDescription
        if (command.`is`("clear") || command.`is`("rename") || command.`is`("edit")) {
            btnParameters.isEnabled = true
        } else {
            btnParameters.isEnabled = false
        }
    }

}

private fun clearActionListeners(btn: AbstractButton) {
    for (al in btn.actionListeners) {
        btn.removeActionListener(al)
    }
}
