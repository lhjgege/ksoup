package com.fleeksoft.ksoup.exception

import com.fleeksoft.io.exception.IOException

public class UncheckedIOException : Exception {
    public constructor(cause: IOException) : super(cause)
    public constructor(message: String) : super(IOException(message))

    public fun ioException(): Throwable? {
        return cause
    }
}
