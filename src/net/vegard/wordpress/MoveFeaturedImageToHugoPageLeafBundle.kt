package net.vegard.wordpress

import java.io.File

class MoveFeaturedImageToHugoPageLeafBundle : Configuration() {

    fun run() {
        if (moveFeaturedImageToHugoPageLeafBundle) {
            println("moveFeaturedImageToHugoPageLeafBundle is turned ON, converting...")
            move(markdownBasePath)
            println("MoveFeaturedImageToHugoPageLeafBundle finished.")
        } else {
            println("moveFeaturedImageToHugoPageLeafBundle is turned OFF.")
        }
    }

    private fun move(path: String) {
        println("Now entering \"$path\"")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    println("Checking Markdown file: \"${file.name}\"")
                    val tempFile = createTempFile()
                    var foundFeaturedImage = false
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val featuredImage = Regex("featured_image: (.*)").find(originalLine)
                            if (featuredImage != null) {
                                foundFeaturedImage = true
                                var convertedLine = originalLine
                                val imageUrl = featuredImage.groupValues[1]
                                val fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
                                if (Util().moveFile(fileName, imagesBasePath, path)) {
                                    convertedLine = convertedLine.replace(imageUrl, fileName)
                                }
                                writer.write("$convertedLine\n")
                            } else {
                                writer.write("$originalLine\n")
                            }
                        }
                    }

                    if (foundFeaturedImage) {
                        check(file.delete() && tempFile.renameTo(File(file.absolutePath))) { "Failed to update file" }
                    } else {
                        tempFile.delete()
                    }
                }
            } else {
                move(file.absolutePath)
            }
        }
    }
}