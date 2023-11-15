package com.fleeksoft.ksoup.nodes

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.ported.BufferReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import kotlin.test.*

/**
 * Tests for Document.
 *
 * @author Sabeeh, fleeksoft@gmail.com
 */
class DocumentTestJvm {

    @Test
    fun testHtmlAppendable() {
        val htmlContent =
            "<html><head><title>Hello</title></head><body><p>One</p><p>Two</p></body></html>"
        val document = Ksoup.parse(htmlContent)
        val outputSettings = Document.OutputSettings()
        outputSettings.prettyPrint(false)
        document.outputSettings(outputSettings)
        assertEquals(htmlContent, document.html(StringWriter()).toString())
    }

    @Test
    fun parseAndHtmlOnDifferentThreads() {
        val html = "<p>Alrighty then it's not \uD83D\uDCA9. <span>Next</span></p>" // 💩
        val asci = "<p>Alrighty then it's not &#x1f4a9;. <span>Next</span></p>"
        val doc = Ksoup.parse(html)
        val out = arrayOfNulls<String>(1)
        val p = doc.select("p")
        assertEquals(html, p.outerHtml())
        val thread = Thread {
            out[0] = p.outerHtml()
            doc.outputSettings().charset(StandardCharsets.US_ASCII)
        }
        thread.start()
        thread.join()
        assertEquals(html, out[0])
        assertEquals(StandardCharsets.US_ASCII, doc.outputSettings().charset())
        assertEquals(asci, p.outerHtml())
    }

    @Test
    @Throws(Exception::class)
    fun testShiftJisRoundtrip() {
        val input = ("<html>"
                + "<head>"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=Shift_JIS\" />"
                + "</head>"
                + "<body>"
                + "before&nbsp;after"
                + "</body>"
                + "</html>")
        val `is`: BufferReader = BufferReader(input.toByteArray(StandardCharsets.US_ASCII))
        val doc: Document = Ksoup.parse(`is`, null, "http://example.com")
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        val output = String(
            doc.html().toByteArray(doc.outputSettings().charset()),
            doc.outputSettings().charset()
        )
        assertFalse(output.contains("?"), "Should not have contained a '?'.")
        assertTrue(
            output.contains("&#xa0;") || output.contains("&nbsp;"),
            "Should have contained a '&#xa0;' or a '&nbsp;'."
        )
    }
}
