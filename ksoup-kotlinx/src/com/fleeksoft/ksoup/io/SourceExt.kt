package com.fleeksoft.ksoup.io

import kotlinx.io.Source


@Deprecated(
    message = "SourceReader.Companion.from(source) is deprecated, use Source.asInputStream() instead.",
    level = DeprecationLevel.WARNING
)
fun SourceReader.Companion.from(source: Source): SourceReader = SourceReaderImpl(source)