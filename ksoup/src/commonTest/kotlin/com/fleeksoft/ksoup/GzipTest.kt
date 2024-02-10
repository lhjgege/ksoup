package com.fleeksoft.ksoup

import korlibs.io.lang.Charsets
import korlibs.io.lang.toString
import korlibs.io.stream.readAll
import kotlin.test.Test
import kotlin.test.assertEquals

class GzipTest {
    @Test
    fun testReadGzipFile() {
        runTest {
            val gzipFileStr =
                readGzipFile(TestHelper.getResourceAbsolutePath("htmltests/gzip.html.gz")).readAll()
                    .toString(charset = Charsets.UTF8)
            val expected = """<title>Gzip test</title>

<p>This is a gzipped HTML file.</p>

"""
            assertEquals(expected, gzipFileStr)
        }
    }
}
