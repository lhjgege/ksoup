package com.fleeksoft.ksoup.io

import com.fleeksoft.io.InputStream
import com.fleeksoft.io.asInputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class FileSourceImpl : FileSource {
    private val path: Path

    private val sourceBuffered by lazy { buffered() }

    constructor(file: Path) {
        this.path = file
    }

    constructor(filePath: String) {
        this.path = Path(filePath)
    }

    private fun FileSourceImpl.buffered() = SystemFileSystem.source(path).buffered()
    override suspend fun asInputStream(): InputStream = sourceBuffered.asInputStream()

    override fun getPath(): String {
        return this.path.name
    }

    override fun getFullName(): String {
        return this.path.name
    }
}