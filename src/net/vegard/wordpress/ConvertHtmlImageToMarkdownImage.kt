package net.vegard.wordpress

import java.io.File

class ConvertHtmlImageToMarkdownImage : Configuration() {

    fun run() {
        if (convertHtmlImageToMarkdownImage) {
            println("convertHtmlImageToMarkdownImage is turned ON, converting...")
            convert(basePath)
            println("ConvertHtmlImageToMarkdownImage finished.")
        } else {
            println("convertHtmlImageToMarkdownImage is turned OFF.")
        }
    }

    private fun convert(path: String) {
        println("Now entering \"$path\"")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    println("Checking Markdown file: \"${file.name}\"")
                    val tempFile = createTempFile()
                    var fileImages = emptySequence<MatchResult>()
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val lineImages = Regex("<img.*?src=[\"|'](.*?)[\"|'].*?/>").findAll(originalLine)
                            if (lineImages.any()) {
                                fileImages = fileImages.plus(lineImages)
                                var convertedLine = originalLine
                                lineImages.forEach {
                                    println("--> Converting image: ${it.groupValues[0]}")
                                    val altTextMatchResult = Regex("alt=[\"|'](.*?)[\"|']").find(it.groupValues[0])
                                    val titleMatchResult = Regex("title=[\"|'](.*?)[\"|']").find(it.groupValues[0])
                                    convertedLine = convertedLine.replace(
                                        it.groupValues[0],
                                        "![${if (altTextMatchResult != null) altTextMatchResult.groupValues[1] else ""}]" +
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

                    if (fileImages.any()) {
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