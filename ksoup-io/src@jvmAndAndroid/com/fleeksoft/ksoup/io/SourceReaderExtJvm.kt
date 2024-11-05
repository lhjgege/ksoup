package com.fleeksoft.ksoup.io

import java.io.InputStream

@Deprecated(
    message = "SourceReader.Companion.from(InputStream) is deprecated, use com.fleeksoft.InputStream instead.",
    level = DeprecationLevel.WARNING
)
fun SourceReader.Companion.from(inputStream: InputStream): SourceReader = SourceReader.from(inputStream.readAllBytes())