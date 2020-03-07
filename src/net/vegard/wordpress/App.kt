package net.vegard.wordpress

fun main() {
    ConvertToHugoPageLeafBundle().run()
    MoveFeaturedImageToHugoPageLeafBundle().run()
    ConvertEasyFootnotesToMarkdownFootnote().run()
    ConvertHtmlImageToMarkdownImage().run()
    ConvertHtmlLinkToMarkdownLink().run()
}