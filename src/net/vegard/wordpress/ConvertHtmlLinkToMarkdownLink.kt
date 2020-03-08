package net.vegard.wordpress

import java.io.File

class ConvertHtmlLinkToMarkdownLink : Configuration() {

    fun run() {
        if (convertHtmlLinkToMarkdownLink) {
            Util().log("convertHtmlLinkToMarkdownLink is turned ON, converting...")
            convert(markdownBasePath)
            Util().log("ConvertHtmlLinkToMarkdownLink finished.")
        } else {
            Util().log("convertHtmlLinkToMarkdownLink is turned OFF.")
        }
    }

    private fun convert(path: String) {
        Util().log("Now entering \"$path\".")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    Util().log("Checking Markdown file: \"${file.name}\".")
                    val tempFile = createTempFile()
                    var fileLinks = emptySequence<MatchResult>()
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val lineLinks = Regex("<a.*?href=[\"|'](.*?)[\"|'].*?>(.*?)</a>").findAll(originalLine)
                            if (lineLinks.any()) {
                                fileLinks = fileLinks.plus(lineLinks)
                                var convertedLine = originalLine
                                lineLinks.forEach {
                                    Util().log("--> Converting link: \"${it.groupValues[0]}\"")
                                    val titleMatchResult = Regex("title=[\"|'](.*?)[\"|']").find(it.groupValues[0])
                                    convertedLine = convertedLine.replace(it.groupValues[0],"[${it.groupValues[2]}]" +
                                            "(${it.groupValues[1]}" +
                                            "${if (titleMatchResult != null) " \"" + titleMatchResult.groupValues[1] + "\"" else ""})"
                                    )
                                }
                                writer.write("$convertedLine\n")
                            } else {
                                writer.write("$originalLine\n")
                            }
                        }
                    }

                    if (fileLinks.any()) {
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
}