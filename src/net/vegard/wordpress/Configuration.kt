package net.vegard.wordpress

open class Configuration {

    val markdownBasePath: String = "ENTER BASEPATH HERE"
    val imagesBasePath: String = "ENTER IMAGE BASEPATH HERE"
    val convertToHugoPageLeafBundle = false
    val moveFeaturedImageToHugoPageLeafBundle = true
    val convertEasyFootnotesToMarkdownFootnote = false
    val convertHtmlImageToMarkdownImage = false
    val convertHtmlLinkToMarkdownLink = false

}
