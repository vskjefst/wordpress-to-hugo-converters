package net.vegard.wordpress

import java.io.File

class MoveFeaturedImageToHugoPageLeafBundle : Configuration() {

    fun run() {
        if (moveFeaturedImageToHugoPageLeafBundle) {
            Util().log("moveFeaturedImageToHugoPageLeafBundle is turned ON, converting...")
            move(markdownBasePath)
            Util().log("MoveFeaturedImageToHugoPageLeafBundle finished.")
        } else {
            Util().log("moveFeaturedImageToHugoPageLeafBundle is turned OFF.")
        }
    }

    private fun move(path: String) {
        Util().log("Now entering \"$path\"")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    Util().log("Checking Markdown file: \"${file.name}\"")
                    val tempFile = createTempFile()
                    var foundFeaturedImage = false
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val featuredImage = Regex("featured_image: (.*)").find(originalLine)
                            if (featuredImage != null) {
                                Util().log("--> Found featured image \"${featuredImage.groupValues[0]}\".")
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
                        Util().log("--> Writing updated version of \"${file.absolutePath}\"")
                        check(file.delete() && tempFile.renameTo(File(file.absolutePath))) { "Failed to update file." }
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