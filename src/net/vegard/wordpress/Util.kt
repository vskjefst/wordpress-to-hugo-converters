package net.vegard.wordpress

import java.io.File

class Util {

    fun moveFile(fileName: String, imagesBasePath: String, destinationPath: String): Boolean {
        println("--> Looking for \"$fileName\".")
        if (File("$destinationPath\\$fileName").exists()) {
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