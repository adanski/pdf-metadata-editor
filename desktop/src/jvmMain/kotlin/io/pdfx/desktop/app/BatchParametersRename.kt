package io.pdfx.desktop.app

import io.pdfx.app.CommandLine.Companion.mdFieldsHelpMessage
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.TitledBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class BatchParametersRename(parameters: io.pdfx.desktop.app.BatchOperationParameters?, owner: Frame?) :
    BatchParametersWindow(parameters, owner) {
    private var previewLabel: JLabel? = null

    constructor(params: io.pdfx.desktop.app.BatchOperationParameters?) : this(params, null)

    override fun createContentPane() {
        title = "Batch rename parameters"
        minimumSize = Dimension(640, 480)
        val gridBagLayout = GridBagLayout()
        gridBagLayout.columnWidths = intArrayOf(640, 0)
        gridBagLayout.rowHeights = intArrayOf(300, 0, 0)
        gridBagLayout.columnWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        gridBagLayout.rowWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
        contentPane.layout = gridBagLayout
        val panel = JPanel()
        panel.border = TitledBorder(
            LineBorder(Color(184, 207, 229)), "Rename template",
            TitledBorder.LEADING, TitledBorder.TOP, null, Color(51, 51, 51)
        )
        val gbc_panel = GridBagConstraints()
        gbc_panel.insets = Insets(5, 5, 5, 5)
        gbc_panel.fill = GridBagConstraints.BOTH
        gbc_panel.gridx = 0
        gbc_panel.gridy = 0
        contentPane.add(panel, gbc_panel)
        val gbl_panel = GridBagLayout()
        gbl_panel.columnWidths = intArrayOf(598, 0)
        gbl_panel.rowHeights = intArrayOf(27, 16, 329, 0)
        gbl_panel.columnWeights = doubleArrayOf(1.0, Double.MIN_VALUE)
        gbl_panel.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        panel.layout = gbl_panel
        val comboBox: JComboBox<String> = JComboBox()
        comboBox.setModel(
            DefaultComboBoxModel(
                arrayOf(
                    "", "{doc.author} - {doc.title}.pdf",
                    "{doc.author} - {doc.creationDate}.pdf"
                )
            )
        )
        comboBox.isEditable = true
        val gbc_comboBox = GridBagConstraints()
        gbc_comboBox.anchor = GridBagConstraints.NORTH
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL
        gbc_comboBox.insets = Insets(0, 0, 5, 0)
        gbc_comboBox.gridx = 0
        gbc_comboBox.gridy = 0
        panel.add(comboBox, gbc_comboBox)
        previewLabel = JLabel("Preview:<dynamic>")
        val gbc_previewLabel = GridBagConstraints()
        gbc_previewLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_previewLabel.anchor = GridBagConstraints.NORTH
        gbc_previewLabel.insets = Insets(0, 0, 5, 0)
        gbc_previewLabel.gridx = 0
        gbc_previewLabel.gridy = 1
        panel.add(previewLabel, gbc_previewLabel)
        val scrollPane = JScrollPane()
        val gbc_scrollPane = GridBagConstraints()
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.gridx = 0
        gbc_scrollPane.gridy = 2
        panel.add(scrollPane, gbc_scrollPane)
        val txtpnSupportedFieldsbasictitle_1 = JTextPane()
        txtpnSupportedFieldsbasictitle_1.isEditable = false
        txtpnSupportedFieldsbasictitle_1.background = UIManager.getColor("Panel.background")
        txtpnSupportedFieldsbasictitle_1.contentType = "text/html"
        txtpnSupportedFieldsbasictitle_1.text = """
            Supported fields:<br>
            <pre>
            <i>${mdFieldsHelpMessage(60, "  {", "}", false)}</i></pre>
            """.trimIndent()
        scrollPane.setViewportView(txtpnSupportedFieldsbasictitle_1)
        txtpnSupportedFieldsbasictitle_1.caretPosition = 0
        val button = JButton("Close")
        button.addActionListener { e: ActionEvent? ->
            isVisible = false
            windowClosed()
        }
        val gbc_button = GridBagConstraints()
        gbc_button.anchor = GridBagConstraints.EAST
        gbc_button.insets = Insets(0, 0, 5, 5)
        gbc_button.gridx = 0
        gbc_button.gridy = 1
        contentPane.add(button, gbc_button)
        val tcA = comboBox.editor.editorComponent as JTextComponent
        tcA.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(arg0: DocumentEvent) {
                showPreview(comboBox.editor.item as String)
            }

            override fun insertUpdate(arg0: DocumentEvent) {
                showPreview(comboBox.editor.item as String)
            }

            override fun removeUpdate(arg0: DocumentEvent) {
                showPreview(comboBox.editor.item as String)
            }
        })
        if (parameters.renameTemplate != null) {
            comboBox.selectedItem = parameters.renameTemplate
        }
        showPreview(comboBox.editor.item as String)
    }

    fun showPreview(template: String?) {
        parameters.renameTemplate = template
        val ts = TemplateString(template)
        previewLabel!!.text = "Preview: " + ts.process(DEMO_METADATA)
    }
}
