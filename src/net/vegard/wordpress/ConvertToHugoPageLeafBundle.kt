package net.vegard.wordpress

import java.io.File

/**
 * Moves the Markdown files to Hugo's page leaf bundle format (https://gohugo.io/content-management/page-bundles/).
 *
 * The Markdown files have to be named using the format YYYY-MM-DD-TITLE.md, for instance 2020-02-14-my-title.md.
 * The end result will be a structure with one directory per year under the configured base path. Each of those
 * directories will contain one directory per post on the format MM-DD-TITLE. The Markdown file will be
 * moved into the directory and renamed to index.md.
 *
 */

class ConvertToHugoPageLeafBundle : Configuration() {

    fun run() {
        if (convertToHugoPageLeafBundle) {
            println("convertToHugoPageLeafBundle is turned ON, converting...")
            convert(basePath)
        } else {
            println("convertToHugoPageLeafBundle is turned OFF.")
        }
        println("convertToHugoPageLeafBundle finished.")
    }

    private fun convert(path: String) {
        println("Now entering \"$path\"")
        File(path).listFiles()!!.toList().forEach { file ->
            if (file.isFile) {
                val matchResult = Regex("([0-9]{4})-(.*).md").find(file.name)
                if (matchResult != null) {
                    val year = matchResult.groupValues[1]
                    val monthAndTitle = matchResult.groupValues[2]
                    val yearDirectory = File("$basePath//$year")
                    if (!yearDirectory.exists()) {
                        println("Creating year directory \"$yearDirectory\"")
                        check(yearDirectory.mkdir()) {"Failed to create year directory ${yearDirectory.absolutePath}"}
                    }
                    val postDirectory = File("$yearDirectory//$monthAndTitle")
                    println("Creating post directory \"$postDirectory\"")
                    check(postDirectory.mkdir()) {"Failed to create post directory ${postDirectory.absolutePath}"}
                    println("Moving ${file.absolutePath} to post directory \"$postDirectory\" as index.md.")
                    file.renameTo(File("$postDirectory//index.md"))
                }
            } else {
                convert(file.absolutePath)
            }
        }
    }

}