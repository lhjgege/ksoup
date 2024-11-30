package com.fleeksoft.ksoup

import com.fleeksoft.charset.Charset
import com.fleeksoft.io.InputStream
import com.fleeksoft.ksoup.helper.DataUtil
import com.fleeksoft.ksoup.io.SourceReader
import com.fleeksoft.ksoup.io.asInputStream
import com.fleeksoft.ksoup.model.MetaData
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.parser.Parser

@Deprecated(
    message = "Ksoup.parse(SourceReader) is deprecated, use Ksoup.parse(InputStream) instead.",
    level = DeprecationLevel.WARNING
)
public fun Ksoup.parseInput(
    sourceReader: SourceReader,
    baseUri: String,
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    return DataUtil.load(
        input = sourceReader.asInputStream(),
        baseUri = baseUri,
        charsetName = charsetName,
        parser = parser
    )
}

@Deprecated(
    message = "Ksoup.parseMetaData(SourceReader) is deprecated, use Ksoup.parseMetaData(InputStream) instead.",
    level = DeprecationLevel.WARNING
)
fun Ksoup.parseMetaData(
    sourceReader: SourceReader,
    baseUri: String = "",
    charset: Charset? = null,
    interceptor: ((head: Element, metaData: MetaData) -> Unit)? = null
): MetaData {
    val head = parseInput(
        sourceReader = sourceReader,
        baseUri = baseUri,
        charsetName = charset?.name()
    ).let { doc -> doc.headOrNull() ?: doc }
    val title = head.selectFirst("title")?.text()
    return parseMetaDataInternal(baseUri = baseUri, title = title) { query ->
        head.selectFirst(query)
    }.also {
        interceptor?.invoke(head, it)
    }
}

/**
 * Read an buffer reader, and parse it to a Document. You can provide an alternate parser, such as a simple XML
 * (non-HTML) parser.
 *
 * @param input stream reader to read. Make sure to close it after parsing.
 * @param baseUri The URL where the HTML was retrieved from, to resolve relative links against.
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param parser alternate [parser][Parser.xmlParser] to use.
 * @return sane HTML
 */
public fun Ksoup.parseInput(
    input: InputStream,
    baseUri: String,
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    return DataUtil.load(input = input, baseUri = baseUri, charsetName = charsetName, parser = parser)
}

/**
 * Parses metadata from an HTML InputStream.
 *
 * @param input HTML content as a InputStream.
 * @param baseUri Base URI to resolve relative URLs against. Defaults to an empty string.
 * @param interceptor Optional function to intercept and manipulate the head element and generated MetaData.
 * @return MetaData object containing parsed metadata information.
 */
fun Ksoup.parseMetaData(
    input: InputStream,
    baseUri: String = "",
    charset: Charset? = null,
    interceptor: ((headEl: Element, metaData: MetaData) -> Unit)? = null
): MetaData {
    val head = parseInput(input = input, baseUri = baseUri, charsetName = charset?.name()).let { doc ->
        doc.headOrNull() ?: doc
    }
    val title = head.selectFirst("title")?.text()
    return parseMetaDataInternal(baseUri = baseUri, title = title) { query ->
        head.selectFirst(query)
    }.also {
        interceptor?.invoke(head, it)
    }
}