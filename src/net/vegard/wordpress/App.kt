package net.vegard.wordpress

import java.time.LocalDateTime

fun main() {
    Util().log("---START ${LocalDateTime.now()}---")
    ConvertToHugoPageLeafBundle().run()
    MoveFeaturedImageToHugoPageLeafBundle().run()
    ConvertEasyFootnotesToMarkdownFootnote().run()
    ConvertHtmlImageToMarkdownImage().run()
    ConvertHtmlLinkToMarkdownLink().run()
    Util().log("---END ${LocalDateTime.now()}---")
}