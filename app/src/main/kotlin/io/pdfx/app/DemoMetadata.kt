package io.pdfx.app

import io.pdfx.app.metadata.MetadataInfo

val DEMO_METADATA: MetadataInfo = MetadataInfo().apply {
    // Spec is at : http://partners.adobe.com/public/developer/en/xmp/sdk/XMPspecification.pdf
    doc.title = "Dracula"
    doc.author = "Bram Stoker"
    doc.subject =
        "Horror tales, Epistolary fiction, Gothic fiction (Literary genre), Vampires -- Fiction, Dracula, Count (Fictitious character) -- Fiction, Transylvania (Romania) -- Fiction, Whitby (England) -- Fiction"
    doc.keywords = "Horror, Gothic, Vampires"
    doc.creator = "Adobe InDesign CS4 (6.0.6)"
    doc.producer = "Adobe PDF Library 9.0"
    doc.creationDate = DateFormat.parseDateOrNull("2012-12-12 00:00:00")
    doc.modificationDate = DateFormat.parseDateOrNull("2012-12-13 00:00:00")
    doc.trapped = "True"
    basic.creatorTool = "Adobe InDesign CS4 (6.0.6)"
    basic.createDate = doc.creationDate
    basic.modifyDate = doc.modificationDate
    basic.baseURL = "https://www.gutenberg.org/"
    basic.rating = 3
    basic.label = "Horror Fiction Collection"
    basic.nickname = "dracula"
    basic.identifiers = mutableListOf("Dracula_original_edition")
    //xmpBasic.advisories ;
    basic.metadataDate = DateFormat.parseDateOrNull("2012-12-14 00:00:00")
    pdf.pdfVersion = "1.5"
    pdf.keywords = doc.keywords
    pdf.producer = "Adobe PDF Library 9.0"
    dc.title = doc.title
    dc.description = "The famous Bram Stocker book"
    dc.creators = listOf("Bram Stocker")
    dc.subjects =
        doc.subject!!.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }
}
