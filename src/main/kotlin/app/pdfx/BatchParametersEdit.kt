package app.pdfx

import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

open class BatchParametersEdit
/**
 * Create the frame.
 */
/**
 * @wbp.parser.constructor
 */
@JvmOverloads constructor(parameters: BatchOperationParameters?, owner: Frame? = null) :
    BatchParametersWindow(parameters, owner) {
    protected var defaultMetadataPane: MetadataEditPane? = null
    protected var lblSelectFieldsTo: JLabel? = null
    protected fun setMessage(message: String?) {
        lblSelectFieldsTo!!.text = message
    }

    override fun createContentPane() {
        title = "Batch set parameters"
        minimumSize = Dimension(640, 480)
        val contentPane = JPanel()
        setContentPane(contentPane)
        val gbl_contentPane = GridBagLayout()
        gbl_contentPane.columnWidths = intArrayOf(520, 0)
        gbl_contentPane.rowHeights = intArrayOf(0, 0, 29, 0)
        gbl_contentPane.columnWeights = doubleArrayOf(1.0, Double.MIN_VALUE)
        gbl_contentPane.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, Double.MIN_VALUE)
        contentPane.layout = gbl_contentPane
        val panel = JPanel()
        val gbc_panel = GridBagConstraints()
        gbc_panel.fill = GridBagConstraints.BOTH
        gbc_panel.insets = Insets(5, 5, 5, 5)
        gbc_panel.gridx = 0
        gbc_panel.gridy = 0
        contentPane.add(panel, gbc_panel)
        val gbl_panel = GridBagLayout()
        gbl_panel.columnWidths = intArrayOf(520, 0, 0, 0)
        gbl_panel.rowHeights = intArrayOf(0, 0)
        gbl_panel.columnWeights = doubleArrayOf(1.0, 0.0, 0.0, Double.MIN_VALUE)
        gbl_panel.rowWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
        panel.layout = gbl_panel
        lblSelectFieldsTo = JLabel("The selected fields below will be set in all files")
        val gbc_lblSelectFieldsTo = GridBagConstraints()
        gbc_lblSelectFieldsTo.insets = Insets(0, 0, 0, 5)
        gbc_lblSelectFieldsTo.fill = GridBagConstraints.VERTICAL
        gbc_lblSelectFieldsTo.gridx = 0
        gbc_lblSelectFieldsTo.gridy = 0
        panel.add(lblSelectFieldsTo, gbc_lblSelectFieldsTo)
        val btnSelectAll = JButton("Select all")
        btnSelectAll.addActionListener { e: ActionEvent? ->
            if (parameters != null) {
                parameters.metadata.setEnabled(true)
                defaultMetadataPane!!.fillFromMetadata(parameters.metadata)
            }
        }
        val gbc_btnSelectAll = GridBagConstraints()
        gbc_btnSelectAll.insets = Insets(0, 0, 0, 5)
        gbc_btnSelectAll.gridx = 1
        gbc_btnSelectAll.gridy = 0
        panel.add(btnSelectAll, gbc_btnSelectAll)
        val button = JButton("Select none")
        button.addActionListener { e: ActionEvent? ->
            if (parameters != null) {
                parameters.metadata.setEnabled(false)
                defaultMetadataPane!!.fillFromMetadata(parameters.metadata)
            }
        }
        val gbc_button = GridBagConstraints()
        gbc_button.gridx = 2
        gbc_button.gridy = 0
        panel.add(button, gbc_button)
        val gbc_md = GridBagConstraints()
        gbc_md.weightx = 1.0
        gbc_md.weighty = 1.0
        gbc_md.insets = Insets(5, 5, 5, 5)
        gbc_md.anchor = GridBagConstraints.NORTH
        gbc_md.fill = GridBagConstraints.BOTH
        gbc_md.gridx = 0
        gbc_md.gridy = 1
        defaultMetadataPane = MetadataEditPane()
        contentPane.add(defaultMetadataPane!!.tabbedaPane, gbc_md)
        val btnClose = JButton("Close")
        val gbc_btnClose = GridBagConstraints()
        gbc_btnClose.insets = Insets(0, 0, 5, 5)
        gbc_btnClose.anchor = GridBagConstraints.NORTHEAST
        gbc_btnClose.gridx = 0
        gbc_btnClose.gridy = 2
        contentPane.add(btnClose, gbc_btnClose)
        btnClose.addActionListener { e: ActionEvent? ->
            isVisible = false
            windowClosed()
        }
        defaultMetadataPane!!.showEnabled(true)
        defaultMetadataPane!!.fillFromMetadata(parameters.metadata)
        contentPane.doLayout()
    }

    override fun windowClosed() {
        defaultMetadataPane!!.copyToMetadata(parameters.metadata)
        super.windowClosed()
    }
}
