package net.vegard.wordpress

import java.io.File

class ConvertHtmlImageToMarkdownImage : Configuration() {

    fun run() {
        if (convertHtmlLinkToMarkdownLink) {
            println("convertHtmlLinksToMarkdownLinks is turned ON, converting...")
            convert(basePath)
            println("ConvertHtmlLinksToMarkdownLinks finished.")
        } else {
            println("convertHtmlLinksToMarkdownLinks is turned OFF.")
        }
    }

    private fun convert(path: String) {
        println("Now entering \"$path\"")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    println("Checking Markdown file: \"${file.name}\"")
                    val tempFile = createTempFile()
                    var fileLinks = emptySequence<MatchResult>()
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val lineLinks = Regex("<a.*?href=\"(.*?)\".*?>(.*?)</a>").findAll(originalLine)
                            if (lineLinks.any()) {
                                fileLinks = fileLinks.plus(lineLinks)
                                var convertedLine = originalLine
                                lineLinks.forEach {
                                    println("--> Fixing link: ${it.groupValues[0]}")
                                    convertedLine = convertedLine.replace(it.groupValues[0],"[${it.groupValues[2]}](${it.groupValues[1]})")
                                }
                                writer.write("$convertedLine\n")
                            } else {
                                writer.write("$originalLine\n")
                            }
                        }
                    }

                    if (fileLinks.any()) {
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