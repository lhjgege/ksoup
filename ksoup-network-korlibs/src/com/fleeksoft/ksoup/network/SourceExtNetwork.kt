package com.fleeksoft.ksoup.network

import com.fleeksoft.ksoup.io.asInputStream
import korlibs.io.net.http.HttpClient
import korlibs.io.stream.toAsyncStream
import korlibs.io.stream.toSyncOrNull

suspend fun HttpClient.Response.asInputStream() = this.content.toAsyncStream().toSyncOrNull()!!.asInputStream()