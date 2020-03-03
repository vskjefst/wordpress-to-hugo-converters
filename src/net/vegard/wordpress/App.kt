package net.vegard.wordpress

fun main() {
    ConvertToHugoPageLeafBundle().run()
    ConvertEasyFootnotesToMarkdownFootnote().run()
    ConvertHtmlImageToMarkdownImage().run()
    ConvertHtmlLinkToMarkdownLink().run()
}