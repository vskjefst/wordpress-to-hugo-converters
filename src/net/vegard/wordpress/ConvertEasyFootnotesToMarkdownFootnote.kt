package net.vegard.wordpress

import java.io.File

class ConvertEasyFootnotesToMarkdownFootnote : Configuration() {

    fun run() {
        if (convertEasyFootnotesToMarkdownFootnote) {
            Util().log("convertEasyFootnotesToMarkdownFootnote is turned ON, converting...")
            convert(markdownBasePath)
            Util().log("ConvertEasyFootnotesToMarkdownFootnote finished.")
        } else {
            Util().log("convertEasyFootnotesToMarkdownFootnote is turned OFF.")
        }
    }

    private fun convert(path: String) {
        Util().log("Now entering \"$path\".")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    Util().log("Checking Markdown file: \"${file.name}\".")
                    val tempFile = createTempFile()
                    var fileFootnoteNumber = 0
                    var fileFootnotes = emptySequence<MatchResult>()
                    tempFile.printWriter().use { writer ->
                        file.forEachLine {originalLine ->
                            val lineFootnotes = Regex("<span id='easy-footnote(.*?)</sup></a></span>").findAll(originalLine)
                            if (lineFootnotes.any()) {
                                fileFootnotes = fileFootnotes.plus(lineFootnotes)
                                var convertedLine = originalLine
                                lineFootnotes.forEach{ matchResult ->
                                    fileFootnoteNumber++
                                    Util().log("--> Converting to [^$fileFootnoteNumber]: \"${matchResult.value}\".")
                                    convertedLine = convertedLine.replace(matchResult.value, "[^$fileFootnoteNumber]")
                                }
                                writer.write("$convertedLine\n")
                            } else {
                                writer.write("$originalLine\n")
                            }
                        }
                    }

                    if (fileFootnotes.any()) {
                        fileFootnotes.forEachIndexed { index, matchResult ->
                            val footnoteText =
                                "[^${index + 1}]: ${fixLink(Regex("title='(.*?)'>").find(matchResult.value)!!.groups[1]?.value)}"
                            Util().log("--> Adding footnote: \"$footnoteText\".")
                            tempFile.appendText("\n $footnoteText")
                        }
                        Util().log("--> Writing updated version of \"${file.absolutePath}\".")
                        check(file.delete() && tempFile.renameTo(File(file.absolutePath))) { "Failed to update file" }
                    } else {
                        tempFile.delete()
                    }
                }
            } else {
                convert(file.absolutePath)
            }
        }
    }

    private fun fixLink(value: CharSequence?): String {
        var innerValue = value ?: ""
        val links = Regex("<a href=&quot;(.*?)&quot;>(.*?)</a>").findAll(innerValue)
        if(links.any()) {
            links.forEach {
                Util().log("--> Fixing link: ${it.groups[0]?.value}")
                innerValue = innerValue.toString().replace(it.groups[0]!!.value, "[${it.groups[2]?.value}](${it.groups[1]?.value})")
            }
        }

        return innerValue.toString()
    }
}