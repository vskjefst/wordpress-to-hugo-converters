package net.vegard.wordpress

import java.io.File

/**
 * Prepends "cover-" to the filename of every image referenced in a featured_image front matter.
 */

class PrependCoverDashToFeaturedImage : Configuration() {

    fun run() {
        if (prependCoverDashToFeaturedImage) {
            Util().log("prependCoverDashToFeaturedImage is turned ON, converting...")
            convert(markdownBasePath)
            Util().log("prependCoverDashToFeaturedImage finished.")
        } else {
            Util().log("prependCoverDashToFeaturedImage is turned OFF.")
        }
    }

    private fun convert(path: String) {
        Util().log("Now entering \"$path\".")
        File(path).listFiles()?.toList()?.forEach { file ->
            if (file.isFile) {
                if (file.extension == "md") {
                    Util().log("Checking Markdown file: \"${file.name}\"")
                    val tempFile = createTempFile()
                    var foundFeaturedImage = false
                    tempFile.printWriter().use { writer ->
                        file.forEachLine { originalLine ->
                            val featuredImage = Regex("featured_image: (.*)").find(originalLine)
                            if (featuredImage != null && !featuredImage.groupValues[1].startsWith("cover-")) {
                                Util().log("--> Found featured image \"${featuredImage.groupValues[0]}\".")
                                foundFeaturedImage = true
                                var convertedLine = originalLine
                                val fileName = featuredImage.groupValues[1]
                                val coverFileName = "cover-$fileName"
                                if (File("$path/$fileName").renameTo(File("$path/$coverFileName"))) {
                                    Util().log("Renaming $fileName to $coverFileName")
                                    convertedLine = convertedLine.replace(fileName, coverFileName)
                                } else {
                                    Util().log("Failed to rename $fileName to $coverFileName")
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
                convert(file.absolutePath)
            }
        }
    }

}