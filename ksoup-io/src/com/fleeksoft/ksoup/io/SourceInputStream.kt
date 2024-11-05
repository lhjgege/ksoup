package com.fleeksoft.ksoup.io

import com.fleeksoft.io.InputStream

@Deprecated(
    message = "SourceInputStream(SourceReader) is deprecated, use com.fleeksoft.io.InputStream instead.",
    level = DeprecationLevel.WARNING
)
class SourceInputStream(private val source: SourceReader) : InputStream() {
    override fun read(): Int {
        if (source.exhausted()) return -1
        return source.readInt()
    }

    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        return source.read(bytes, off, len)
    }

    override fun readAllBytes(): ByteArray {
        return source.readAllBytes()
    }

    override fun mark(readLimit: Int) {
        source.mark(readLimit.toLong())
    }

    override fun reset() {
        source.reset()
    }

    override fun markSupported(): Boolean {
        return true
    }

    override fun close() {
        source.close()
    }
}

@Deprecated(
    message = "SourceReader.asInputStream() is deprecated, use com.fleeksoft.io.InputStream instead.",
    level = DeprecationLevel.WARNING
)
fun SourceReader.asInputStream() = SourceInputStream(this)