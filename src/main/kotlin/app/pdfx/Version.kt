package app.pdfx

import java.io.IOException
import java.util.*
import java.util.regex.Pattern

object Version {
    internal var version: String? = null
    fun get(): VersionTuple {
        if (version == null) {
            val prop = Properties()
            try {
                prop.load(VersionTuple::class.java.classLoader.getResourceAsStream("app/pdfx/version.properties"))
                version = prop.getProperty("app.version", "0.0.0-dev")
            } catch (e: IOException) {
                e.printStackTrace()
                version = "0.0.0-dev"
            }
        }
        return VersionTuple(version)
    }

    class VersionTuple @JvmOverloads constructor(
        version: String?,
        pattern: String? = "^(\\d+)\\.(\\d+)\\.(\\d+)-?(\\S*)$"
    ) {
        var major = 0
        var minor = 0
        var patch = 0
        var tag: String? = ""
        var parseSuccess = false

        init {
            val matcher = Pattern.compile(pattern).matcher(version)
            if (matcher.find()) {
                major = matcher.group(1).toInt()
                minor = matcher.group(2).toInt()
                patch = matcher.group(3).toInt()
                if (matcher.groupCount() > 3) {
                    tag = matcher.group(4)
                    if (tag == null) {
                        tag = ""
                    }
                }
                parseSuccess = true
            } else {
                tag = "dev"
            }
        }

        fun cmp(other: VersionTuple): Int {
            var diff = major - other.major
            if (diff != 0) return diff
            diff = minor - other.minor
            if (diff != 0) return diff
            diff = patch - other.patch
            return if (diff != 0) diff else tag!!.compareTo(other.tag!!, ignoreCase = true)
        }

        val asString: String
            get() = Integer.toString(major) + "." +
                    Integer.toString(minor) + "." +
                    Integer.toString(patch) + if (tag!!.length > 0) "-$tag" else ""
    }
}
