package io.pdfx.desktop.app

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent

class FileDropMessage : JComponent() {
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
        val textWidth1 = metrics.stringWidth(openFilesText)
        val x = 3
        val y = 3
        val width = d.width - 6
        val height = d.height - 6
        g.color = Color(255, 255, 255, 170)
        g.fill(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 5f, 5f))

        // Draw borders
        g.color = Color(64, 64, 64, 192)
        g.stroke = BasicStroke(3f)
        g.draw(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), 5f, 5f))


        // Finally draw text
        g.color = Color(64, 64, 64, 192)
        g.drawString(openFilesText, (width - textWidth1) / 2, (height - textHeight) / 2)
    }

    companion object {
        var openFilesText = "Add file(s)"
    }
}
