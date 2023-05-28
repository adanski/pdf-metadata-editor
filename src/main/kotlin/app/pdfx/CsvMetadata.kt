package app.pdfx

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.opencsv.ICSVParser
import java.io.File
import java.io.FileReader
import java.util.*

object CsvMetadata {
    @JvmStatic
    @Throws(Exception::class)
    fun readFile(filename: File?): List<MetadataInfo> {
        val parsed = ArrayList<MetadataInfo>()
        val reader = CSVReaderBuilder(FileReader(filename))
            .withCSVParser(
                CSVParserBuilder()
                    .withEscapeChar(ICSVParser.NULL_CHARACTER)
                    .build()
            )
            .build()
        val entries = reader.readAll()
        reader.close()
        val header = entries.removeAt(0)
        for (i in header.indices) {
            header[i] = header[i].trim { it <= ' ' }
        }
        if (!Arrays.asList(*header).contains("file.fullPath")) {
            throw Exception("The header must specify a 'file.fullPath' column")
        }
        for (row in entries) {
            val metadata = MetadataInfo()
            for (idx in row.indices) {
                val id = header[idx]
                if (CommandLine.validMdNames.contains(id)) {
                    val value = row[idx].trim { it <= ' ' }
                    metadata.setAppendFromString(id, value)
                    metadata.setEnabled(id, true)
                }
            }
            parsed.add(metadata)
        }
        return parsed
    }
}
