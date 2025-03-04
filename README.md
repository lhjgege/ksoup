# Ksoup: Kotlin Multiplatform HTML & XML Parser

**Ksoup** is a Kotlin Multiplatform library for working with real-world HTML and XML. It's a port of the renowned Java library, **jsoup**, and offers an easy-to-use API for URL fetching, data parsing, extraction, and manipulation using DOM and CSS selectors.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Apache-2.0](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.ksoup/ksoup.svg)](https://central.sonatype.com/artifact/com.fleeksoft.ksoup/ksoup)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-mac](http://img.shields.io/badge/platform-macos-111111.svg?style=flat)
![badge-tvos](http://img.shields.io/badge/platform-tvos-808080.svg?style=flat)
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-linux](http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)
![badge-js](https://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat)
![badge-wasm](https://img.shields.io/badge/platform-wasm-F8DB5D.svg?style=flat)

Ksoup implements the [WHATWG HTML5](https://html.spec.whatwg.org/multipage/) specification, parsing HTML to the same DOM as modern browsers do, but with support for Android, JVM, and native platforms.

## Features
- Scrape and parse HTML from a URL, file, or string
- Find and extract data using DOM traversal or CSS selectors
- Manipulate HTML elements, attributes, and text
- Clean user-submitted content against a safe-list to prevent XSS attacks
- Output tidy HTML

Ksoup is adept at handling all varieties of HTML found in the wild.

## Getting started
### Ksoup is published on Maven Central
Include the dependency in `commonMain`. Latest version [![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.ksoup/ksoup.svg)](https://central.sonatype.com/artifact/com.fleeksoft.ksoup/ksoup)

Ksoup published in four variants. Pick the one that suits your needs and start building!
1. **Lightweight variant: Use this if you only need to parse HTML from a string.**
   ```kotlin
   implementation("com.fleeksoft.ksoup:ksoup:<version>")
    ```
2. **This variant use [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for I/O and [Ktor 3](https://github.com/ktorio/ktor) for networking**
   ```kotlin
   // Ksoup.parseFile, Ksoup.parseSource
   implementation("com.fleeksoft.ksoup:ksoup-kotlinx:<version>")
   
    // Optional: Include only if you need to use network request functions such as
    // Ksoup.parseGetRequest, Ksoup.parseSubmitRequest, and Ksoup.parsePostRequest
   implementation("com.fleeksoft.ksoup:ksoup-network:<version>")
    ```

3. **This variant use [korlibs-io](https://github.com/korlibs/korlibs-io) for I/O and networking**
   ```kotlin
   // Ksoup.parseFile, Ksoup.parseStream
   implementation("com.fleeksoft.ksoup:ksoup-korlibs:<version>")

    // Optional: Include only if you need to use network request functions such as
    // Ksoup.parseGetRequest, Ksoup.parseSubmitRequest, and Ksoup.parsePostRequest
   implementation("com.fleeksoft.ksoup:ksoup-network-korlibs:<version>")
    ```

4. **This variant use [kotlinx-io](https://github.com/Kotlin/kotlinx-io) for I/O and [Ktor 2](https://github.com/ktorio/ktor) for networking**
   ```kotlin
   // Ksoup.parseFile, Ksoup.parseSource
   implementation("com.fleeksoft.ksoup:ksoup-kotlinx:<version>")

    // Optional: Include only if you need to use network request functions such as
    // Ksoup.parseGetRequest, Ksoup.parseSubmitRequest, and Ksoup.parsePostRequest
   implementation("com.fleeksoft.ksoup:ksoup-network-ktor2:<version>")
    ```
5. **This variant use [okio](https://github.com/square/okio) for I/O and [Ktor 2](https://github.com/ktorio/ktor) for networking**
   ```kotlin
   implementation("com.fleeksoft.ksoup:ksoup-okio:<version>")

    // Optional: Include only if you need to use network request functions such as
    // Ksoup.parseGetRequest, Ksoup.parseSubmitRequest, and Ksoup.parsePostRequest
   implementation("com.fleeksoft.ksoup:ksoup-network-ktor2:<version>")
    ```

#### Ksoup supports [Charsets](https://github.com/fleeksoft/fleeksoft-io/blob/main/CharsetsReadme.md)
- Standard charsets are already supported by **Ksoup IO**, but for extended charsets, plesae add `com.fleeksoft.charset:charset-ext`, For more details, visit the [Charsets Documentation](https://github.com/fleeksoft/fleeksoft-io/blob/main/CharsetsReadme.md)

### Parsing HTML from a String with Ksoup
For API documentation you can check [Jsoup](https://jsoup.org/). Most of the APIs work without any changes.
```kotlin
val html = "<html><head><title>One</title></head><body>Two</body></html>"
val doc: Document = Ksoup.parse(html = html)

println("title => ${doc.title()}") // One
println("bodyText => ${doc.body().text()}") // Two
```
This snippet demonstrates how to use `Ksoup.parse` for parsing an HTML string and extracting the title and body text.

### Fetching and Parsing HTML from a URL using Ksoup
```kotlin
//Please note that the com.fleeksoft.ksoup:ksoup-network library is required for Ksoup.parseGetRequest.
val doc: Document = Ksoup.parseGetRequest(url = "https://en.wikipedia.org/") // suspend function
// or
val doc: Document = Ksoup.parseGetRequestBlocking(url = "https://en.wikipedia.org/")

println("title: ${doc.title()}")
val headlines: Elements = doc.select("#mp-itn b a")

headlines.forEach { headline: Element ->
    val headlineTitle = headline.attr("title")
    val headlineLink = headline.absUrl("href")

    println("$headlineTitle => $headlineLink")
}
```

### Parsing XML
```kotlin
    val doc: Document = Ksoup.parse(xml, parser = Parser = Parser.xmlParser())
```

### Parsing Metadata from Website
```kotlin
//Please note that the com.fleeksoft.ksoup:ksoup-network library is required for Ksoup.parseGetRequest.
val doc: Document = Ksoup.parseGetRequest(url = "https://en.wikipedia.org/") // suspend function
val metadata: Metadata = Ksoup.parseMetaData(element = doc) // suspend function
// or
val metadata: Metadata = Ksoup.parseMetaData(html = HTML)

println("title: ${metadata.title}")
println("description: ${metadata.description}")
println("ogTitle: ${metadata.ogTitle}")
println("ogDescription: ${metadata.ogDescription}")
println("twitterTitle: ${metadata.twitterTitle}")
println("twitterDescription: ${metadata.twitterDescription}")
// Check com.fleeksoft.ksoup.model.MetaData for more fields
```

In this example, `Ksoup.parseGetRequest` fetches and parses HTML content from Wikipedia, extracting and printing news headlines and their corresponding links.
### Ksoup Public functions
  - **Ksoup.parse(html: String, baseUri: String = ""): Document**
  - **Ksoup.parse(html: String, parser: Parser, baseUri: String = ""): Document**
  - **Ksoup.parse(reader: Reader, parser: Parser, baseUri: String = ""): Document**
  - **Ksoup.clean( bodyHtml: String, safelist: Safelist = Safelist.relaxed(), baseUri: String = "", outputSettings: Document.OutputSettings? = null): String**
  - **Ksoup.isValid(bodyHtml: String, safelist: Safelist = Safelist.relaxed()): Boolean**
### Ksoup I/O Public functions
  - **Ksoup.parseInput(input: InputStream, baseUri: String, charsetName: String? = null, parser: Parser = Parser.htmlParser())** from (ksoup-io, ksoup-okio, ksoup-kotlinx, ksoup-korlibs)
  - **Ksoup.parseFile** from (ksoup-okio, ksoup-kotlinx, ksoup-korlibs)
  - **Ksoup.parseSource** from (ksoup-okio, ksoup-kotlinx)
  - **Ksoup.parseStream** from (ksoup-korlibs)

### Ksoup Network Public functions
- Suspend functions
    - **Ksoup.parseGetRequest**
    - **Ksoup.parseSubmitRequest**
    - **Ksoup.parsePostRequest**
- Blocking functions
  - **Ksoup.parseGetRequestBlocking**
  - **Ksoup.parseSubmitRequestBlocking**
  - **Ksoup.parsePostRequestBlocking**

#### For further documentation, please check here: [Jsoup](https://jsoup.org/)

### Ksoup vs. Jsoup Benchmarks: Parsing & Selecting 448KB HTML File [test.tx](https://github.com/fleeksoft/ksoup/blob/develop/ksoup-test/testResources/test.txt)
![Ksoup vs Jsoup](benchmark1.png)

## Open source
Ksoup is an open source project, a Kotlin Multiplatform port of jsoup, distributed under the Apache License, Version 2.0. The source code of Ksoup is available on [GitHub](https://github.com/fleeksoft/ksoup).


## Development and Support
For questions about usage and general inquiries, please refer to [GitHub Discussions](https://github.com/fleeksoft/ksoup/discussions).

If you wish to contribute, please read the [Contributing Guidelines](CONTRIBUTING.md).

To report any issues, visit our [GitHub issues](https://github.com/fleeksoft/ksoup/issues), Please ensure to check for duplicates before submitting a new issue.



## License

    Copyright 2024 FLEEK SOFT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
