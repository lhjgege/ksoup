package com.fleeksoft.ksoup.parser

import com.fleeksoft.ksoup.Ksoup.parse
import com.fleeksoft.ksoup.nodes.Document
import korlibs.io.lang.Charsets
import korlibs.io.lang.toByteArray
import korlibs.io.stream.openSync
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun unescapeEntities() {
        val s = Parser.unescapeEntities("One &amp; Two", false)
        assertEquals("One & Two", s)
    }

    @Test
    fun unescapeEntitiesHandlesLargeInput() {
        val longBody = StringBuilder(500000)
        do {
            longBody.append("SomeNonEncodedInput")
        } while (longBody.length < 64 * 1024)
        val body = longBody.toString()
        assertEquals(body, Parser.unescapeEntities(body, false))
    }

    @Test
    fun testUtf8() {
        // testcase for https://github.com/jhy/jsoup/issues/1557. no repro.
        val parsed: Document =
            parse(
                syncStream = "<p>H\u00E9llo, w\u00F6rld!".toByteArray(Charsets.UTF8).openSync(),
                baseUri = "",
                charsetName = null,
            )
        val text = parsed.selectFirst("p")?.wholeText()
        assertEquals("H\u00E9llo, w\u00F6rld!", text)
    }
}
