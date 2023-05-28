package app.pdfx

import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent

class FileDropSelectMessage : JComponent() {
    var mousePos: Point? = null
    fun setDropPos(point: Point?) {
        mousePos = point
    }

    var isOpenFile = false
    @JvmField
    var isBatchOperation = false
    override fun paintComponent(g1: Graphics) {
        val g = g1 as Graphics2D
        val d = size

        // Draw mask
        g.color = Color(128, 128, 128, 128)
        g.fillRect(0, 0, d.width, d.height)
        val font = g.font.deriveFont(36.0.toFloat())
        g.font = font
        val metrics = g.getFontMetrics(font)
        val textHeight = metrics.height
        val textAscent = metrics.ascent
        val textWidth1 = metrics.stringWidth(openFilesText)
        val textWidth2 = metrics.stringWidth(batchOperationText)
        val x = 3
        val y = 3
        val width = d.width - 6
        val height1 = (d.height - 6) / 3
        val height2 = d.height - 6 - height1
        val y2 = y + height1
        g.color = Color(255, 255, 255, 170)
        val inset = 3

        // Draw background rectangles
        if (mousePos != null) {
            isOpenFile = mousePos!!.x >= x && mousePos!!.x <= width && mousePos!!.y >= y && mousePos!!.y <= height1
            isBatchOperation =
                mousePos!!.x >= x && mousePos!!.x <= width && mousePos!!.y >= y2 && mousePos!!.y <= y2 + height2
        }
        if (isOpenFile) {
            g.fill(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height1.toFloat(), 5f, 5f))
        } else {
            g.fill(
                RoundRectangle2D.Float(
                    ((width - textWidth1) / 2 - inset).toFloat(),
                    ((height1 - textHeight) / 2 - textAscent - inset).toFloat(),
                    (textWidth1 + 2 * inset).toFloat(),
                    (textHeight + 2 * inset).toFloat(),
                    3f,
                    3f
                )
            )
        }
        if (isBatchOperation) {
            g.fill(RoundRectangle2D.Float(x.toFloat(), y2.toFloat(), width.toFloat(), height2.toFloat(), 5f, 5f))
        } else {
            g.fill(
                RoundRectangle2D.Float(
                    ((width - textWidth2) / 2 - inset).toFloat(),
                    (y2 + (height2 - textHeight) / 2 - textAscent - inset).toFloat(),
                    (textWidth2 + 2 * inset).toFloat(),
                    (textHeight + 2 * inset).toFloat(),
                    3f,
                    3f
                )
            )
        }

        // Draw borders
        g.color = Color(64, 64, 64, 192)
        g.stroke = BasicStroke(3f)
        g.draw(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height1.toFloat(), 5f, 5f))
        g.draw(RoundRectangle2D.Float(x.toFloat(), y2.toFloat(), width.toFloat(), height2.toFloat(), 5f, 5f))


        // Finally draw text
        g.color = Color(64, 64, 64, 192)
        g.drawString(openFilesText, (width - textWidth1) / 2, (height1 - textHeight) / 2)
        g.drawString(batchOperationText, (width - textWidth2) / 2, y2 + (height2 - textHeight) / 2)
    }

    companion object {
        var openFilesText = "Open file(s)"
        var batchOperationText = "Batch operation"
    }
}
