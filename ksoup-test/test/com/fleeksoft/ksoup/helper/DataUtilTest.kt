package com.fleeksoft.ksoup.helper

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.byteInputStream
import com.fleeksoft.io.inputStream
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.TestHelper
import com.fleeksoft.ksoup.io.internal.ControllableInputStream
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.parseInput
import com.fleeksoft.ksoup.parser.Parser
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class DataUtilTest {

    @Test
    fun testCharset() {
        assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html;charset=utf-8 "))
        assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"))
        assertEquals(
            "ISO-8859-1",
            DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1"),
        )
        assertNull(DataUtil.getCharsetFromContentType("text/html"))
        assertNull(DataUtil.getCharsetFromContentType(null))
        assertNull(DataUtil.getCharsetFromContentType("text/html;charset=Unknown"))
    }

    @Test
    fun testQuotedCharset() {
        assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=\"utf-8\""))
        assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html;charset=\"UTF-8\""))
        assertEquals(
            "ISO-8859-1",
            DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""),
        )
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset=\"Unsupported\""))
        assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"))
    }

    private fun stream(data: String, charset: String = "UTF-8"): ControllableInputStream {
        return ControllableInputStream.wrap(data.byteInputStream(Charsets.forName(charset)), 0)
    }

    @Test
    fun discardsSpuriousByteOrderMark() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc: Document = DataUtil.parseInputStream(
            input = stream(html),
            baseUri = "http://foo.com/",
            charsetName = "UTF-8",
            parser = Parser.htmlParser(),
        )
        assertEquals("One", doc.head().text())
    }

    @Test
    fun discardsSpuriousByteOrderMarkWhenNoCharsetSet() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val doc: Document = DataUtil.parseInputStream(
            input = stream(html),
            baseUri = "http://foo.com/",
            charsetName = null,
            parser = Parser.htmlParser(),
        )
        assertEquals("One", doc.head().text())
        assertEquals("UTF-8", doc.outputSettings().charset().name().uppercase())
    }

    @Test
    fun shouldNotThrowExceptionOnEmptyCharset() {
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset="))
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset=;"))
    }

    @Test
    fun shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        assertEquals(
            "ISO-8859-1",
            DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1, charset=1251"),
        )
    }

    @Test
    fun shouldCorrectCharsetForDuplicateCharsetString() {
        assertEquals(
            "iso-8859-1",
            DataUtil.getCharsetFromContentType("text/html; charset=charset=iso-8859-1"),
        )
    }

    @Test
    fun shouldReturnNullForIllegalCharsetNames() {
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset=\$HJKDF§$/("))
    }

    @Test
    fun generatesMimeBoundaries() {
        val m1 = DataUtil.mimeBoundary()
        val m2 = DataUtil.mimeBoundary()
        assertEquals(DataUtil.boundaryLength, m1.length)
        assertEquals(DataUtil.boundaryLength, m2.length)
        assertNotSame(m1, m2)
    }

    @Test
    fun wrongMetaCharsetFallback() {
        val html = "<html><head><meta charset=iso-8></head><body></body></html>"
        val doc: Document =
            DataUtil.parseInputStream(
                input = stream(html),
                baseUri = "http://example.com",
                charsetName = null,
                parser = Parser.htmlParser(),
            )
        val expected = """<html>
 <head>
  <meta charset="iso-8">
 </head>
 <body></body>
</html>"""
        assertEquals(expected, doc.toString())
    }

    @Test
    fun secondMetaElementWithContentTypeContainsCharsetParameter() {
        if (!TestHelper.isEUCKRSupported()) {
            // FIXME: euc-kr charset not supported
            return
        }
        val html =
            "<html><head>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html\">" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" +
                    "</head><body>한국어</body></html>"
        val doc: Document = DataUtil.parseInputStream(
            input = stream(data = html, charset = "euc-kr"),
            baseUri = "http://example.com",
            charsetName = null,
            parser = Parser.htmlParser(),
        )
        assertEquals("한국어", doc.body().text())
    }

    @Test
    fun firstMetaElementWithCharsetShouldBeUsedForDecoding() {
        val html = "<html><head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">" +
                "</head><body>Übergrößenträger</body></html>"
        val document = DataUtil.parseInputStream(
            input = stream(data = html, charset = "iso-8859-1"),
            baseUri = "http://example.com",
            charsetName = null,
            parser = Parser.htmlParser(),
        )

        assertEquals("Übergrößenträger", document.body().text())
    }

    // TODO: add parseSequenceInputStream test

    @Test
    fun supportsBOMinFiles() = runTest {
        if (!TestHelper.isUtf16Supported()) {
            // FIXME: UTF-16 charset not supported
            return@runTest
        }
        var doc = TestHelper.parseResource(resourceName = "bomtests/bom_utf16be.html", baseUri = "http://example.com")
        assertContains(doc.title(), "UTF-16BE")
        assertContains(doc.text(), "가각갂갃간갅")

        doc = TestHelper.parseResource(resourceName = "bomtests/bom_utf16le.html", baseUri = "http://example.com")
        assertContains(doc.title(), "UTF-16LE")
        assertContains(doc.text(), "가각갂갃간갅")

        if (!TestHelper.isUtf32Supported()) {
            // FIXME: UTF-32 charset not supported
            return@runTest
        }

        doc = TestHelper.parseResource(resourceName = "bomtests/bom_utf32be.html", baseUri = "http://example.com")
        assertContains(doc.title(), "UTF-32BE")
        assertContains(doc.text(), "가각갂갃간갅")

        doc = TestHelper.parseResource(resourceName = "bomtests/bom_utf32le.html", baseUri = "http://example.com")
        assertContains(doc.title(), "UTF-32LE")
        assertContains(doc.text(), "가각갂갃간갅")
    }

    @Test
    fun streamerSupportsBOMinFiles() = runTest {
        if (!TestHelper.isUtf16Supported()) {
            // FIXME: UTF-16 charset not supported
            return@runTest
        }
        // test files from http://www.i18nl10n.com/korean/utftest/
        var input = TestHelper.readResource("bomtests/bom_utf16be.html")
        val parser = Parser.htmlParser()

        var doc: Document = DataUtil.streamParser(
            input = input,
            baseUri = "http://example.com",
            charset = null,
            parser = parser
        ).complete()
        assertContains(doc.title(), "UTF-16BE")
        assertContains(doc.text(), "가각갂갃간갅")

        input = TestHelper.readResource("bomtests/bom_utf16le.html")
        doc = DataUtil.streamParser(
            input = input,
            baseUri = "http://example.com",
            charset = null,
            parser = parser
        )
            .complete()
        assertContains(doc.title(), "UTF-16LE")
        assertContains(doc.text(), "가각갂갃간갅")

        if (!TestHelper.isUtf32Supported()) {
            // FIXME: UTF-32 charset not supported
            return@runTest
        }

        input = TestHelper.readResource("bomtests/bom_utf32be.html")
        doc = DataUtil.streamParser(
            input = input,
            baseUri = "http://example.com",
            charset = null,
            parser = parser
        )
            .complete()
        assertContains(doc.title(), "UTF-32BE")
        assertContains(doc.text(), "가각갂갃간갅")

        input = TestHelper.readResource("bomtests/bom_utf32le.html")
        doc = DataUtil.streamParser(
            input = input,
            baseUri = "http://example.com",
            charset = null,
            parser = parser
        )
            .complete()
        assertContains(doc.title(), "UTF-32LE")
        assertContains(doc.text(), "가각갂갃간갅")
    }

    @Test
    fun supportsUTF8BOM() = runTest {
        val source = TestHelper.readResource("bomtests/bom_utf8.html")
        val doc: Document = Ksoup.parseInput(input = source, baseUri = "http://example.com", charsetName = null)
        assertEquals("OK", doc.head().select("title").text())
    }

    @Test
    fun noExtraNULLBytes() {
        val b = "<html><head><meta charset=\"UTF-8\"></head><body><div><u>ü</u>ü</div></body></html>"
            .toByteArray(Charsets.UTF8)
        val doc = Ksoup.parseInput(b.inputStream(), baseUri = "", charsetName = null)
        assertFalse(doc.outerHtml().contains("\u0000"))
    }

    @Test
    fun supportsZippedUTF8BOM() = runTest {
        val resourceName = "bomtests/bom_utf8.html.gz"
        val doc = TestHelper.parseResource(resourceName, baseUri = "http://example.com")

        assertEquals("OK", doc.head().select("title").text())
        assertEquals(
            "There is a UTF8 BOM at the top (before the XML decl). If not read correctly, will look like a non-joining space.",
            doc.body().text(),
        )
    }

    @Test
    fun streamerSupportsZippedUTF8BOM() = runTest {
        val source = TestHelper.readGzipResource("bomtests/bom_utf8.html.gz")
        val doc = DataUtil.streamParser(
            input = source,
            baseUri = "http://example.com",
            charset = null,
            parser = Parser.htmlParser()
        ).complete()
        assertEquals("OK", doc.head().select("title").text())
        assertEquals(
            "There is a UTF8 BOM at the top (before the XML decl). If not read correctly, will look like a non-joining space.",
            doc.body().text()
        )
    }

    @Test
    fun supportsXmlCharsetDeclaration() {
        val encoding = "iso-8859-1"
        val soup = (
                "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hellö Wörld!</html>"
                ).toByteArray(Charsets.forName(encoding)).inputStream()
        val doc: Document = Ksoup.parseInput(soup, baseUri = "", charsetName = null)
        assertEquals("Hellö Wörld!", doc.body().text())
    }

    @Test
    fun loadsGzipFile() = runTest {
        val resourceName = "htmltests/gzip.html.gz"
        val doc = TestHelper.parseResource(resourceName)
        assertEquals("Gzip test", doc.title())
        assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    fun loadsZGzipFile() = runTest {
        // compressed on win, with z suffix
        val resourceName = "htmltests/gzip.html.z"
        val doc = TestHelper.parseResource(resourceName)
        assertEquals("Gzip test", doc.title())
        assertEquals("This is a gzipped HTML file.", doc.selectFirst("p")!!.text())
    }

    @Test
    fun handlesFakeGzipFile() = runTest {
        val resourceName = "htmltests/fake-gzip.html.gz"
        val doc = TestHelper.parseResource(resourceName)
        assertEquals("This is not gzipped", doc.title())
        assertEquals("And should still be readable.", doc.selectFirst("p")!!.text())
    }

    @Test
    fun testStringVsSourceReaderParse() = runTest {
        val input: String = TestHelper.readResourceAsString("htmltests/large.html.gz")

        val expected = Ksoup.parse(input, "https://example.com")
        val doc: Document =
            Ksoup.parseInput(input = input.byteInputStream(), baseUri = "https://example.com", charsetName = null)

        assertTrue(doc.hasSameValue(expected))
    }

    @Test
    fun handlesChunkedInputStream() = runTest {
        val resourceName = "htmltests/large.html.gz"
        val input = TestHelper.readResourceAsString(resourceName)
        val stream = VaryingReadInputStream(input.byteInputStream())

        val expected = TestHelper.parseResource(resourceName, baseUri = "https://example.com", charsetName = null)
        val doc = Ksoup.parseInput(input = stream, baseUri = "https://example.com", charsetName = null)

        assertTrue(doc.hasSameValue(expected))
    }

    @Test
    fun handlesUnlimitedRead() = runTest {
        val input: String = TestHelper.readResourceAsString("htmltests/large.html.gz")
        val stream = VaryingReadInputStream(input.byteInputStream())
        val byteBuffer = DataUtil.readToByteBuffer(stream, 0)
        val read = byteBuffer.array().decodeToString(0, byteBuffer.limit())
        assertEquals(input, read)
    }

    // an input stream to give a range of output sizes, that changes on each read
    class VaryingReadInputStream(inStream: InputStream) : InputStream() {
        private val inputStream: InputStream = inStream
        private var stride = 0

        override fun read(): Int {
            return inputStream.read()
        }

        override fun read(b: ByteArray): Int {
            return inputStream.read(b, 0, minOf(b.size, ++stride))
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            return inputStream.read(b, off, minOf(len, ++stride))
        }
    }
}
