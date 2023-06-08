package app.pdfx

import java.io.File
import javax.swing.filechooser.FileFilter

class PdfFilter : FileFilter() {
    override fun accept(f: File): Boolean {
        if (f.isDirectory) {
            return true
        }
        val fn = f.name
        val ext = fn.substring(fn.lastIndexOf('.') + 1)
        //System.out.println(ext);
        return ext.equals("pdf", ignoreCase = true)
    }

    override fun getDescription(): String {
        return "PDF files(*.pdf)"
    }
}
