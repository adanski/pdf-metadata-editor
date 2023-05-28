package app.pdfx

import java.awt.Frame
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.KeyStroke

abstract class BatchParametersWindow @JvmOverloads constructor(
    parameters: BatchOperationParameters?,
    owner: Frame? = null
) : JDialog(owner, true) {
    var parameters: BatchOperationParameters? = null
    var onClose: Runnable? = null

    /**
     * Create the frame.
     */
    init {
        setLocationRelativeTo(owner)
        if (parameters != null) {
            this.parameters = parameters
        } else {
            this.parameters = BatchOperationParameters()
        }
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(arg0: WindowEvent) {
                this@BatchParametersWindow.windowClosed()
            }
        })
        getRootPane().registerKeyboardAction(
            {
                isVisible = false
                windowClosed()
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )
        createContentPane()
    }

    protected abstract fun createContentPane()
    fun onCloseAction(newAction: Runnable?) {
        onClose = newAction
    }

    open fun windowClosed() {
        if (onClose != null) {
            onClose!!.run()
        }
    }
}