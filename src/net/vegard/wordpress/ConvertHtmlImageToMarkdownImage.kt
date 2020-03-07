package net.vegard.wordpress

import java.io.File

class ConvertHtmlImageToMarkdownImage : Configuration() {

    fun run() {
        if (convertHtmlImageToMarkdownImage) {
            println("convertHtmlImageToMarkdownImage is turned ON, converting...")
            convert(markdownBasePath)
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
                                    val imageUrl = it.groupValues[1]
                                    convertedLine = convertedLine.replace(
                                        it.groupValues[0],
                                        "![${if (altTextMatchResult != null) altTextMatchResult.groupValues[1] else ""}]" +
                                                "($imageUrl" +
                                                "${if (titleMatchResult != null) " \"" + titleMatchResult.groupValues[1] + "\"" else ""})"
                                    )
                                    val fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
                                    if (moveFile(fileName, imagesBasePath, path)) {
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

    private fun moveFile(fileName: String, imagesBasePath: String, destinationPath: String) : Boolean {
        println("--> Looking for \"$fileName\".")
        if (File ("$destinationPath\\$fileName").exists()) {
            println("--> \"$fileName\" already exists in \"$destinationPath\".")
            return true
        }
        val file = findFile(fileName, imagesBasePath)
        if (file != null) {
            println("--> Found file \"$file\".")
            val destination = "$destinationPath\\$fileName"
            file.copyTo(File(destination))
            println("--> Copied to \"$destination\".")
            return true
        } else {
            println("--> *** WARNING *** Did not find \"$fileName\" anywhere.")
            return false
        }
    }

    private fun findFile(fileName: String, path: String): File? {
        File(path).listFiles()!!.forEach {
            if (it.isFile && it.name == fileName) {
                return it
            } else if (it.isDirectory) {
                val foundFile = findFile(fileName, it.absolutePath)
                if (foundFile != null) {
                    return foundFile
                }
            }
        }
        return null
    }
}