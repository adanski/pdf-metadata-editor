package io.pdfx.app

import io.pdfx.app.metadata.MetadataInfo

class TemplateString(var template: String?, var length: Int = 0xFFFFFFF) {
    interface Entity {
        operator fun get(md: MetadataInfo): String
        fun shrinkable(): Boolean
    }

    class Variable(var name: String) : Entity {
        override fun get(md: MetadataInfo): String {
            return md.getString(name, "")
        }

        override fun shrinkable(): Boolean {
            return true
        }
    }

    class Literal(var literal: String) : Entity {
        override fun get(md: MetadataInfo): String {
            return literal
        }

        override fun shrinkable(): Boolean {
            return false
        }
    }

    var entityList: MutableList<Entity>? = null
    fun parse() {
        entityList = mutableListOf()
        if (template == null) return
        var idx = 0
        while (true) {
            val openIdx = template!!.indexOf("{", idx)
            if (openIdx > 0) entityList!!.add(Literal(template!!.substring(idx, openIdx)))
            idx = if (openIdx >= 0) {
                val closeIdx = template!!.indexOf("}", openIdx)
                if (closeIdx >= 0) {
                    val varName = template!!.substring(openIdx + 1, closeIdx)
                    entityList!!.add(Variable(varName))
                    closeIdx + 1
                } else {
                    entityList!!.add(Literal(template!!.substring(openIdx)))
                    break
                }
            } else {
                entityList!!.add(Literal(template!!.substring(idx)))
                break
            }
        }
    }

    fun process(md: MetadataInfo): String {
        if (entityList == null) parse()
        val chunks = ArrayList<String>()
        val resizable = ArrayList<Int>()
        var outSize = 0
        for (i in entityList!!.indices) {
            val e = entityList!![i]
            val value = e[md]
            chunks.add(value)
            if (e.shrinkable() && value.length > 0) resizable.add(i)
            outSize += value.length
        }
        if (outSize > length && resizable.size > 0) {
            var shirinkableSize = 0
            for (i in resizable) {
                shirinkableSize += chunks[i].length
            }
            val shrinkCoef = FloatArray(chunks.size)
            for (i in resizable) {
                shrinkCoef[i] = chunks[i].length.toFloat() / shirinkableSize
            }
            for (i in resizable) {
                val v = chunks[i]
                val reduceBy = Math.round(shrinkCoef[i] * (outSize - length))
                val endIndex = v.length - reduceBy
                if (endIndex > 0) chunks[i] = v.substring(0, endIndex) else chunks[i] = ""
            }
        }
        val result = StringBuilder()
        for (chunk in chunks) {
            result.append(chunk)
        }
        return result.toString()
    }
}
