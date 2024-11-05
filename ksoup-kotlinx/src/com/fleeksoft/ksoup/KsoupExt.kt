package com.fleeksoft.ksoup

import com.fleeksoft.io.kotlinx.asInputStream
import com.fleeksoft.ksoup.helper.DataUtil
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.parser.Parser
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem


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
public fun Ksoup.parseFile(
    filePath: String,
    baseUri: String = filePath,
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    return parseFile(
        path = Path(filePath),
        baseUri = baseUri,
        charsetName = charsetName,
        parser = parser
    )
}

public fun Ksoup.parseFile(
    path: Path,
    baseUri: String = path.name,
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {

    return parseSource(
        source = SystemFileSystem.source(path),
        baseUri = baseUri,
        charsetName = charsetName,
        parser = parser
    )
}

public fun Ksoup.parseSource(
    source: RawSource,
    baseUri: String = "",
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    return DataUtil.load(
        input = source.asInputStream(),
        baseUri = baseUri,
        charsetName = charsetName,
        parser = parser
    )
}


public fun Ksoup.parseSource(
    source: Source,
    baseUri: String = "",
    charsetName: String? = null,
    parser: Parser = Parser.htmlParser(),
): Document {
    return DataUtil.load(
        input = source.asInputStream(),
        baseUri = baseUri,
        charsetName = charsetName,
        parser = parser
    )
}