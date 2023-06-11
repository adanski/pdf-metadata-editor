package io.pdfx.app

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.regex.Pattern

object FileList {
    var isGlob = Pattern.compile("[\\[\\]\\{\\}\\*\\?]")
    fun fileList(fileNames: List<String?>): List<File> {
        val rval = ArrayList<File>()
        for (fileName in fileNames) {
            val file = File(fileName)
            val m = isGlob.matcher(file.name)
            if (m.find()) {
                var dir = file.parent
                if (dir == null) {
                    dir = "."
                }
                val finder = Finder(file.name)
                try {
                    Files.walkFileTree(File(dir).toPath(), finder)
                } catch (e: IOException) {
                    System.err.println(e)
                }
                rval.addAll(finder.fileList)
            } else {
                rval.add(file)
            }
        }
        return rval
    }

    fun fileList(fileNames: Array<String?>): List<File> {
        return fileList(listOf(*fileNames))
    }

    internal class Finder(pattern: String) : SimpleFileVisitor<Path>() {
        private val matcher: PathMatcher
        var fileList: MutableList<File> = ArrayList()

        init {
            matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
        }

        // Compares the glob pattern against
        // the file or directory name.
        fun find(file: Path) {
            val name = file.fileName
            if (name != null && matcher.matches(name)) {
                fileList.add(file.toFile())
            }
        }

        // Invoke the pattern matching
        // method on each file.
        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            find(file)
            return FileVisitResult.CONTINUE
        }

        // Invoke the pattern matching
        // method on each directory.
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            find(dir)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
            System.err.println(exc)
            return FileVisitResult.CONTINUE
        }
    }
}
