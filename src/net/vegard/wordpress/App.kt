package net.vegard.wordpress

fun main() {
    ConvertToHugoPageLeafBundle().run()
    ConvertEasyFootnotesToMarkdownFootnote().run()
    ConvertHtmlLinksToMarkdownLinks().run()
}