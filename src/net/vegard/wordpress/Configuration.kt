package net.vegard.wordpress

open class Configuration {

    val logToFile = true
    val markdownBasePath = "ENTER BASEPATH HERE"
    val imagesBasePath  = "ENTER IMAGE BASEPATH HERE"
    val convertToHugoPageLeafBundle = false
    val moveFeaturedImageToHugoPageLeafBundle = false
    val convertEasyFootnotesToMarkdownFootnote = false
    val convertHtmlImageToMarkdownImage = false
    val convertHtmlLinkToMarkdownLink = false
    val prependCoverDashToFeaturedImage = false

}
