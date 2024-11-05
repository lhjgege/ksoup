package com.fleeksoft.ksoup

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.parser.Parser


/**
 * Parse the contents of a file as HTML.
 *
 * @param filePath file to load HTML from. Supports gzipped files (ending in .z or .gz).
 * @param baseUri The URL where the HTML was retrieved from, to resolve relative links against.
 * @param charsetName (optional) character set of file contents. Set to `null` to determine from `http-equiv` meta tag, if
 * present, or fall back to `UTF-8` (which is often safe to do).
 * @param parser alternate [parser][Parser.xmlParser] to use.
 * @return sane HTML
 */
public suspend fun Ksoup.parseFile(
    filePath: String,
    baseUri: String = filePath,
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    TODO("not implemented for lite module use io module")
}