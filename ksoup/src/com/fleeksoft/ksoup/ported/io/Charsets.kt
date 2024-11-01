package com.fleeksoft.ksoup.ported.io

import com.fleeksoft.charset.Charset

object Charsets {
    val UTF8: Charset = com.fleeksoft.charset.Charsets.UTF8

    fun forName(name: String): Charset = com.fleeksoft.charset.Charsets.forName(name)

    const val isOnlyUtf8 = false
}