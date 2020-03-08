package net.vegard.wordpress

import java.io.File

class ConvertHtmlImageToMarkdownImage : Configuration() {

    fun run() {
        if (convertHtmlImageToMarkdownImage) {
            Util().log("convertHtmlImageToMarkdownImage is turned ON, converting...")
            convert(markdownBasePath)
            Util().log("ConvertHtmlImageToMarkdownImage finished.")
        } else {
            Util().log("convertHtmlImageToMarkdownImage is turned OFF.")
        }
    }

    private fun convert(path: String) {
        Util().log("Now entering \"$path\".")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    Util().log("Checking Markdown file: \"${file.name}\".")
                    val tempFile = createTempFile()
                    var fileImages = emptySequence<MatchResult>()
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val lineImages = Regex("<img.*?src=[\"|'](.*?)[\"|'].*?/>").findAll(originalLine)
                            if (lineImages.any()) {
                                fileImages = fileImages.plus(lineImages)
                                var convertedLine = originalLine
                                lineImages.forEach {
                                    Util().log("--> Converting image: \"${it.groupValues[0]}\".")
                                    val altTextMatchResult = Regex("alt=[\"|'](.*?)[\"|']").find(it.groupValues[0])
                                    val titleMatchResult = Regex("title=[\"|'](.*?)[\"|']").find(it.groupValues[0])
                                    val imageUrl = it.groupValues[1]
                                    convertedLine = convertedLine.replace(
                                        it.groupValues[0],
                                        "![${if (altTextMatchResult != null) altTextMatchResult.groupValues[1] else ""}]" +
                                                "($imageUrl" +
                                                "${if (titleMatchResult != null) " \"" + titleMatchResult.groupValues[1] + "\"" else ""})"
                                    )
                                    val fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
                                    if (Util().moveFile(fileName, imagesBasePath, path)) {
                                        convertedLine = convertedLine.replace(imageUrl, fileName)
                                    }
                                }
                                writer.write("$convertedLine\n")
                            } else {
                                writer.write("$originalLine\n")
                            }
                        }
                    }

                    if (fileImages.any()) {
                        Util().log("--> Writing updated version of \"${file.absolutePath}\"")
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